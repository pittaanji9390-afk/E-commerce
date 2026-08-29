package com.marketplace.order.dto;

import com.marketplace.order.domain.PayoutStatus;
import com.marketplace.order.domain.SellerOrderStatus;
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
public class SellerOrderDto {
    private UUID id;
    private String sellerOrderNumber;
    private UUID parentOrderId;
    private UUID sellerId;
    private String sellerName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private BigDecimal netSellerPayable;
    private SellerOrderStatus status;
    private PayoutStatus payoutStatus;
    private List<OrderItemDto> items;
    private Instant createdAt;
}
