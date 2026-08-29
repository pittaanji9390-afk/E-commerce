package com.marketplace.review.dto;

import com.marketplace.review.domain.ReviewStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private UUID id;
    private UUID productId;
    private UUID customerId;
    private String customerName;
    private int rating;
    private String title;
    private String comment;
    private boolean verifiedPurchase;
    private int helpfulVotes;
    private List<String> imageUrls;
    private ReviewStatus status;
    private Instant createdAt;
}
