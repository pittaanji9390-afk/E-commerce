package com.marketplace.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPlatformAnalyticsDto {
    private BigDecimal platformGmv; // Gross Merchandise Volume
    private BigDecimal totalTakeRateRevenue; // Total platform commission earned
    private long totalUsers;
    private long totalSellers;
    private long pendingKycCount;
    private long totalOrders;
    private long totalProducts;
    private Map<String, BigDecimal> gmvLast30Days;
}
