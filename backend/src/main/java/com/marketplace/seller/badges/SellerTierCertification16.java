package com.marketplace.seller.badges;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "seller_tier_certifications_16")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerTierCertification16 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;

    @Column(name = "badge_category", nullable = false, length = 50)
    private String badgeCategory;

    @Column(name = "fulfillment_score", nullable = false)
    private double fulfillmentScore;

    @Column(name = "is_verified_top_seller", nullable = false)
    @Builder.Default
    private boolean verifiedTopSeller = true;

    @Column(name = "awarded_at", nullable = false)
    @Builder.Default
    private Instant awardedAt = Instant.now();
}
