const { write } = require('./generator_helper');

console.log('Generating Deep Enterprise Marketplace Subsystems (Affiliate, Moderation, Repricer, Forex)...');

// 1. AFFILIATE & INFLUENCER MARKETING
write('backend/src/main/java/com/marketplace/affiliate/domain/AffiliateStatus.java', `
package com.marketplace.affiliate.domain;

public enum AffiliateStatus {
    PENDING_APPROVAL,
    ACTIVE,
    SUSPENDED,
    TERMINATED
}
`);

write('backend/src/main/java/com/marketplace/affiliate/domain/AffiliatePartner.java', `
package com.marketplace.affiliate.domain;

import com.marketplace.identity.domain.User;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "affiliate_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliatePartner extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "referral_handle", nullable = false, unique = true, length = 50)
    private String referralHandle;

    @Column(name = "commission_rate", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal commissionRate = BigDecimal.valueOf(5.00);

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private AffiliateStatus status = AffiliateStatus.PENDING_APPROVAL;

    @Column(name = "lifetime_earnings", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal lifetimeEarnings = BigDecimal.ZERO;

    @Column(name = "unpaid_balance", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal unpaidBalance = BigDecimal.ZERO;
}
`);

write('backend/src/main/java/com/marketplace/affiliate/domain/AffiliateClick.java', `
package com.marketplace.affiliate.domain;

import com.marketplace.product.domain.Product;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "affiliate_clicks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateClick extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliatePartner affiliate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "clicked_at", nullable = false)
    @Builder.Default
    private Instant clickedAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/affiliate/domain/AffiliateConversion.java', `
package com.marketplace.affiliate.domain;

import com.marketplace.order.domain.Order;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "affiliate_conversions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateConversion extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliatePartner affiliate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_subtotal", precision = 15, scale = 2, nullable = false)
    private BigDecimal orderSubtotal;

    @Column(name = "commission_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal commissionAmount;

    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private boolean paid = false;
}
`);

// 2. AUTOMATED REPRICER & COMPETITOR MONITORING
write('backend/src/main/java/com/marketplace/repricer/domain/RepricingStrategy.java', `
package com.marketplace.repricer.domain;

public enum RepricingStrategy {
    MATCH_BUY_BOX,
    BEAT_BY_PENNY,
    PERCENTAGE_BELOW_LOWEST,
    MAXIMIZE_PROFIT_MARGIN,
    TARGET_VELOCITY
}
`);

write('backend/src/main/java/com/marketplace/repricer/domain/RepricerRule.java', `
package com.marketplace.repricer.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "repricer_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepricerRule extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Enumerated(EnumType.STRING)
    @Column(name = "strategy", length = 40, nullable = false)
    private RepricingStrategy strategy;

    @Column(name = "min_price_floor", precision = 15, scale = 2, nullable = false)
    private BigDecimal minPriceFloor;

    @Column(name = "max_price_ceiling", precision = 15, scale = 2, nullable = false)
    private BigDecimal maxPriceCeiling;

    @Column(name = "step_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal stepAmount = BigDecimal.valueOf(0.01);

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
`);

write('backend/src/main/java/com/marketplace/repricer/domain/RepricingLog.java', `
package com.marketplace.repricer.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "repricing_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepricingLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "previous_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal previousPrice;

    @Column(name = "new_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal newPrice;

    @Column(name = "trigger_reason", nullable = false, length = 200)
    private String triggerReason;

    @Column(name = "repriced_at", nullable = false)
    @Builder.Default
    private Instant repricedAt = Instant.now();
}
`);

// 3. CONTENT & TRADEMARK AUTOMATED MODERATION
write('backend/src/main/java/com/marketplace/moderation/domain/ModerationVerdict.java', `
package com.marketplace.moderation.domain;

public enum ModerationVerdict {
    AUTO_APPROVED,
    FLAGGED_FOR_HUMAN_REVIEW,
    AUTO_REJECTED_TRADEMARK_VIOLATION,
    AUTO_REJECTED_PROHIBITED_GOODS,
    CLEARED_BY_ADMIN
}
`);

write('backend/src/main/java/com/marketplace/moderation/domain/ProductModerationAudit.java', `
package com.marketplace.moderation.domain;

import com.marketplace.product.domain.Product;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "product_moderation_audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductModerationAudit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "verdict", length = 50, nullable = false)
    private ModerationVerdict verdict;

    @Column(name = "confidence_score", precision = 5, scale = 4, nullable = false)
    private double confidenceScore;

    @Column(name = "matched_keywords_json", columnDefinition = "TEXT")
    private String matchedKeywordsJson;

    @Column(name = "audited_at", nullable = false)
    @Builder.Default
    private Instant auditedAt = Instant.now();
}
`);

console.log('Affiliate, Repricer & Moderation layers generated.');
`);
