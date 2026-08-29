package com.marketplace.order.dto;

import com.marketplace.order.domain.OrderStatus;
import com.marketplace.order.domain.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String currency;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private List<SellerOrderDto> sellerOrders;
    private Instant createdAt;
}
