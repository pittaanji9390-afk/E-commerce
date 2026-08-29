package com.marketplace.advertising.domain;

import com.marketplace.product.domain.Product;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ad_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdCampaign extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promoted_product_id", nullable = false)
    private Product promotedProduct;

    @Column(name = "daily_budget", precision = 15, scale = 2, nullable = false)
    private BigDecimal dailyBudget;

    @Column(name = "cpc_bid", precision = 15, scale = 2, nullable = false)
    private BigDecimal cpcBid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Column(name = "total_impressions", nullable = false)
    @Builder.Default
    private long totalImpressions = 0;

    @Column(name = "total_clicks", nullable = false)
    @Builder.Default
    private long totalClicks = 0;

    @Column(name = "total_spend", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalSpend = BigDecimal.ZERO;
}
