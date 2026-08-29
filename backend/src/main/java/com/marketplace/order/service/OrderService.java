package com.marketplace.order.service;

import com.marketplace.order.domain.*;
import com.marketplace.order.dto.OrderDto;
import com.marketplace.order.dto.OrderItemDto;
import com.marketplace.order.dto.SellerOrderDto;
import com.marketplace.order.repository.OrderRepository;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SellerOrderRepository sellerOrderRepository;

    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return toOrderDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getCustomerOrders(UUID customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toOrderDto);
    }

    @Transactional(readOnly = true)
    public Page<SellerOrderDto> getSellerOrders(UUID sellerId, Pageable pageable) {
        return sellerOrderRepository.findBySellerId(sellerId, pageable).map(this::toSellerOrderDto);
    }

    @Transactional
    public void markOrderPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        for (SellerOrder sellerOrder : order.getSellerOrders()) {
            sellerOrder.setStatus(SellerOrderStatus.PAID);
            sellerOrder.setPayoutStatus(PayoutStatus.ESCROW_HELD);
            sellerOrderRepository.save(sellerOrder);
        }

        log.info("Order marked as PAID: [orderNumber={}, subOrders={}]", order.getOrderNumber(), order.getSellerOrders().size());
    }

    public OrderDto toOrderDto(Order order) {
        List<SellerOrderDto> subOrders = order.getSellerOrders().stream()
                .map(this::toSellerOrderDto)
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingAmount(order.getShippingAmount())
                .taxAmount(order.getTaxAmount())
                .grandTotal(order.getGrandTotal())
                .currency(order.getCurrency())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .sellerOrders(subOrders)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public SellerOrderDto toSellerOrderDto(SellerOrder so) {
        List<OrderItemDto> items = so.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .variantId(item.getVariant().getId())
                        .productId(item.getProduct().getId())
                        .productTitle(item.getProductTitleSnapshot())
                        .variantTitle(item.getVariantTitleSnapshot())
                        .sku(item.getSkuSnapshot())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .taxAmount(item.getTaxAmount())
                        .discountAmount(item.getDiscountAmount())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return SellerOrderDto.builder()
                .id(so.getId())
                .sellerOrderNumber(so.getSellerOrderNumber())
                .parentOrderId(so.getParentOrder().getId())
                .sellerId(so.getSeller().getId())
                .sellerName(so.getSeller().getDisplayName())
                .subtotal(so.getSubtotal())
                .discountAmount(so.getDiscountAmount())
                .shippingAmount(so.getShippingAmount())
                .taxAmount(so.getTaxAmount())
                .totalAmount(so.getTotalAmount())
                .commissionRate(so.getCommissionRate())
                .commissionAmount(so.getCommissionAmount())
                .netSellerPayable(so.getNetSellerPayable())
                .status(so.getStatus())
                .payoutStatus(so.getPayoutStatus())
                .items(items)
                .createdAt(so.getCreatedAt())
                .build();
    }
}
