package com.marketplace.advertising.dto;

import com.marketplace.advertising.domain.CampaignStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdCampaignDto {
    private UUID id;
    private String name;
    private UUID sellerId;
    private UUID promotedProductId;
    private String promotedProductTitle;
    private BigDecimal dailyBudget;
    private BigDecimal cpcBid;
    private CampaignStatus status;
    private long totalImpressions;
    private long totalClicks;
    private BigDecimal totalSpend;
    private double clickThroughRate;
    private Instant createdAt;
}
