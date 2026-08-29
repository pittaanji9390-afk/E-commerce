package com.marketplace.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVariantRequest {

    @NotBlank(message = "Variant SKU is required")
    @Size(max = 100)
    private String sku;

    private String barcode;

    @NotBlank(message = "Variant title is required")
    @Size(max = 150)
    private String title;

    @NotNull
    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal weightAdjustmentGrams = BigDecimal.ZERO;

    private String attributesJson;

    @NotNull
    @Builder.Default
    private Integer initialStock = 0;
}
