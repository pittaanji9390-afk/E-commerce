package com.marketplace.seller.badges;

import com.marketplace.seller.domain.Seller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class BadgeEvaluationService38 {

    public SellerTierCertification38 evaluateBadge(Seller seller, double onTimeRate, double rating) {
        boolean qualifies = onTimeRate >= 0.95 && rating >= 4.7;
        SellerTierCertification38 badge = SellerTierCertification38.builder()
                .seller(seller)
                .badgeName("PREMIER_SELLER_LEVEL_38")
                .badgeCategory("LOGISTICS_EXCELLENCE")
                .fulfillmentScore(onTimeRate * 100)
                .verifiedTopSeller(qualifies)
                .awardedAt(Instant.now())
                .build();
        log.debug("Seller {} badge qualification status: {}", seller.getDisplayName(), qualifies);
        return badge;
    }
}
