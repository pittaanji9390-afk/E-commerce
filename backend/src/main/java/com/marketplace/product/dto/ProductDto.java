package com.marketplace.product.dto;

import com.marketplace.product.domain.ProductStatus;
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
public class ProductDto {
    private UUID id;
    private UUID sellerId;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String title;
    private String slug;
    private String sku;
    private String shortDescription;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal compareAtPrice;
    private String currency;
    private String taxCategory;
    private ProductStatus status;
    private BigDecimal ratingAverage;
    private int ratingCount;
    private int totalSales;
    private List<ProductVariantDto> variants;
    private List<ProductImageDto> images;
    private Instant createdAt;
}
