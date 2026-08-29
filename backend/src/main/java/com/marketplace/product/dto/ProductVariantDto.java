package com.marketplace.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDto {
    private UUID id;
    private UUID productId;
    private String sku;
    private String barcode;
    private String title;
    private BigDecimal priceAdjustment;
    private BigDecimal effectivePrice;
    private BigDecimal weightAdjustmentGrams;
    private String attributesJson;
    private int availableStock;
    private boolean active;
}
