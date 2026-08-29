package com.marketplace.recommendation.dto;

import com.marketplace.recommendation.domain.RecommendationType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedProductDto {
    private UUID productId;
    private String title;
    private String slug;
    private BigDecimal basePrice;
    private String primaryImageUrl;
    private BigDecimal ratingAverage;
    private int ratingCount;
    private RecommendationType recommendationType;
    private double relevanceScore;
}
