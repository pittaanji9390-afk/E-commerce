package com.marketplace.refund.service;

import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.order.domain.OrderStatus;
import com.marketplace.order.domain.PaymentStatus;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.domain.SellerOrderStatus;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.payment.domain.Payment;
import com.marketplace.payment.domain.PaymentTransactionStatus;
import com.marketplace.payment.repository.PaymentRepository;
import com.marketplace.payment.service.StripePaymentProvider;
import com.marketplace.refund.domain.Refund;
import com.marketplace.refund.domain.RefundStatus;
import com.marketplace.refund.repository.RefundRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final UserRepository userRepository;
    private final StripePaymentProvider paymentProvider;

    @Transactional
    public Refund processSellerOrderRefund(UUID sellerOrderId, BigDecimal amount, String reason, UUID requestedByUserId) {
        SellerOrder sellerOrder = sellerOrderRepository.findById(sellerOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", sellerOrderId));

        Payment payment = paymentRepository.findByOrderId(sellerOrder.getParentOrder().getId())
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.RESOURCE_NOT_FOUND, "No payment record found for order."));

        if (payment.getStatus() != PaymentTransactionStatus.SUCCEEDED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Cannot refund an uncaptured or failed payment.");
        }

        if (amount.compareTo(sellerOrder.getTotalAmount()) > 0) {
            throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Refund amount cannot exceed sub-order total.");
        }

        User actor = requestedByUserId != null ? userRepository.findById(requestedByUserId).orElse(null) : null;

        // Call Payment Provider
        String providerRefundId = paymentProvider.processRefund(payment.getProviderTransactionId(), amount, reason);

        Refund refund = Refund.builder()
                .payment(payment)
                .sellerOrder(sellerOrder)
                .providerRefundId(providerRefundId)
                .amount(amount)
                .currency(payment.getCurrency())
                .reason(reason)
                .status(RefundStatus.COMPLETED)
                .requestedBy(actor)
                .completedAt(Instant.now())
                .build();

        Refund savedRefund = refundRepository.save(refund);

        sellerOrder.setStatus(SellerOrderStatus.REFUNDED);
        sellerOrderRepository.save(sellerOrder);

        payment.setStatus(PaymentTransactionStatus.REFUNDED);
        paymentRepository.save(payment);

        sellerOrder.getParentOrder().setPaymentStatus(PaymentStatus.REFUNDED);
        sellerOrder.getParentOrder().setOrderStatus(OrderStatus.REFUNDED);

        log.info("Processed refund [id={}, subOrder={}, amount={}]", savedRefund.getId(), sellerOrder.getSellerOrderNumber(), amount);
        return savedRefund;
    }
}
