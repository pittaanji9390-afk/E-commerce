package com.marketplace.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchFilter {
    private String query;
    private Long categoryId;
    private Long brandId;
    private UUID sellerId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private Boolean inStockOnly;
    private String sortBy; // "price_asc", "price_desc", "rating", "newest", "popular"
}
