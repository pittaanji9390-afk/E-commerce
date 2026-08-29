package com.marketplace.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long brandId;

    @NotBlank(message = "Product title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Product slug is required")
    @Size(max = 300)
    private String slug;

    @NotBlank(message = "SKU is required")
    @Size(max = 100)
    private String sku;

    private String shortDescription;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be greater than zero")
    private BigDecimal basePrice;

    private BigDecimal compareAtPrice;

    private String currency;

    private BigDecimal weightGrams;

    private String dimensionsCm;

    private List<CreateVariantRequest> variants;
}
