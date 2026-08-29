package com.marketplace.review.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDto {
    private UUID productId;
    private BigDecimal averageRating;
    private long totalReviews;
    private Map<Integer, Long> ratingBreakdown;
}
