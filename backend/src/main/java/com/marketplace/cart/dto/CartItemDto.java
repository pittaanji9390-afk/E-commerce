package com.marketplace.cart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private UUID itemId;
    private UUID variantId;
    private UUID productId;
    private String productTitle;
    private String variantTitle;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal itemTotal;
    private String imageUrl;
    private int availableStock;
}
