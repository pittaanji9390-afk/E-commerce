package com.marketplace.seller.badges;

import com.marketplace.seller.domain.Seller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class BadgeEvaluationService6 {

    public SellerTierCertification6 evaluateBadge(Seller seller, double onTimeRate, double rating) {
        boolean qualifies = onTimeRate >= 0.95 && rating >= 4.7;
        SellerTierCertification6 badge = SellerTierCertification6.builder()
                .seller(seller)
                .badgeName("PREMIER_SELLER_LEVEL_6")
                .badgeCategory("LOGISTICS_EXCELLENCE")
                .fulfillmentScore(onTimeRate * 100)
                .verifiedTopSeller(qualifies)
                .awardedAt(Instant.now())
                .build();
        log.debug("Seller {} badge qualification status: {}", seller.getDisplayName(), qualifies);
        return badge;
    }
}
