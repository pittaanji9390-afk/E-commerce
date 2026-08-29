package com.marketplace.analytics.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDashboardAnalyticsDto {
    private UUID sellerId;
    private BigDecimal totalGrossRevenue;
    private BigDecimal totalCommissionPaid;
    private BigDecimal netEarnings;
    private long totalOrders;
    private long totalItemsSold;
    private long activeProductsCount;
    private long lowStockCount;
    private BigDecimal averageOrderValue;
    private Map<String, BigDecimal> revenueLast30Days;
}
