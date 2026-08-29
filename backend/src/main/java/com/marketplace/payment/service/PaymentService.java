package com.marketplace.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.inventory.service.InventoryService;
import com.marketplace.order.domain.*;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.order.service.OrderService;
import com.marketplace.payment.domain.Payment;
import com.marketplace.payment.domain.PaymentTransactionStatus;
import com.marketplace.payment.domain.PaymentWebhook;
import com.marketplace.payment.repository.PaymentRepository;
import com.marketplace.payment.repository.PaymentWebhookRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import com.marketplace.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentWebhookRepository webhookRepository;
    private final OrderRepository orderRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final StripePaymentProvider stripeProvider;
    private final ObjectMapper objectMapper;

    @Transactional
    public String createPaymentSession(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Order is already paid.");
        }

        Map<String, Object> meta = new HashMap<>();
        meta.put("orderId", order.getId().toString());
        meta.put("orderNumber", order.getOrderNumber());

        String sessionRef = stripeProvider.createPaymentSession(order, meta);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> Payment.builder()
                        .order(order)
                        .paymentProvider(stripeProvider.getProviderName())
                        .amount(order.getGrandTotal())
                        .currency(order.getCurrency())
                        .paymentMethod("CARD")
                        .status(PaymentTransactionStatus.INITIALIZED)
                        .build());

        payment.setProviderTransactionId(sessionRef);
        paymentRepository.save(payment);

        return sessionRef;
    }

    /**
     * Idempotent, Cryptographically-verified Webhook Ingestion.
     * Prevents replay attacks and settles inventory & financial state.
     */
    @Transactional
    public void processWebhook(String provider, String payload, String signatureHeader, String eventId) {
        // 1. Signature Verification
        if (!stripeProvider.verifyWebhookSignature(payload, signatureHeader)) {
            log.error("Rejected payment webhook with invalid cryptographic signature! Provider={}", provider);
            throw new UnauthorizedException("Invalid webhook cryptographic signature.");
        }

        // 2. Deduplication check
        if (webhookRepository.existsByProviderEventId(eventId)) {
            log.info("Duplicate webhook received and ignored: eventId={}", eventId);
            return;
        }

        PaymentWebhook webhook = PaymentWebhook.builder()
                .provider(provider)
                .providerEventId(eventId)
                .eventType("unknown")
                .payloadJson(payload)
                .signatureHeader(signatureHeader)
                .processedStatus("PROCESSING")
                .build();

        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("type").asText("payment_intent.succeeded");
            String orderNumber = root.path("data").path("object").path("metadata").path("orderNumber").asText(null);
            String txId = root.path("data").path("object").path("id").asText(eventId);

            webhook.setEventType(eventType);

            if (orderNumber != null) {
                Order order = orderRepository.findByOrderNumber(orderNumber)
                        .orElse(null);

                if (order != null) {
                    if ("payment_intent.succeeded".equalsIgnoreCase(eventType) || "checkout.session.completed".equalsIgnoreCase(eventType)) {
                        handlePaymentSuccess(order, txId);
                        webhook.setProcessedStatus("PROCESSED_SUCCESS");
                    } else if ("payment_intent.payment_failed".equalsIgnoreCase(eventType)) {
                        handlePaymentFailure(order);
                        webhook.setProcessedStatus("PROCESSED_FAILURE");
                    }
                }
            } else {
                webhook.setProcessedStatus("PROCESSED_NOOP");
            }
        } catch (Exception e) {
            log.error("Error processing payment webhook: eventId=" + eventId, e);
            webhook.setProcessedStatus("ERROR");
            webhook.setErrorLog(e.getMessage());
        }

        webhookRepository.save(webhook);
    }

    private void handlePaymentSuccess(Order order, String providerTxId) {
        orderService.markOrderPaid(order.getId());

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment != null) {
            payment.setProviderTransactionId(providerTxId);
            payment.setStatus(PaymentTransactionStatus.SUCCEEDED);
            paymentRepository.save(payment);
        }

        // Permanent stock commit across all ordered items
        for (SellerOrder so : order.getSellerOrders()) {
            for (OrderItem it : so.getItems()) {
                inventoryService.commitSale(it.getVariant().getId(), it.getQuantity(), so.getSellerOrderNumber());
            }
        }

        log.info("Payment confirmed and stock committed for Order: {}", order.getOrderNumber());
    }

    private void handlePaymentFailure(Order order) {
        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Release reserved inventory back to available stock
        for (SellerOrder so : order.getSellerOrders()) {
            so.setStatus(SellerOrderStatus.CANCELLED);
            sellerOrderRepository.save(so);

            for (OrderItem it : so.getItems()) {
                inventoryService.releaseReservation(it.getVariant().getId(), it.getQuantity(), so.getSellerOrderNumber());
            }
        }

        log.warn("Payment failed and stock reservations released for Order: {}", order.getOrderNumber());
    }
}
