package com.marketplace.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID id;
    private UUID variantId;
    private UUID productId;
    private String productTitle;
    private String variantTitle;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
}
