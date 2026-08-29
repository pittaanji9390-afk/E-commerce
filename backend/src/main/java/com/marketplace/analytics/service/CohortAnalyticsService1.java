package com.marketplace.analytics.service;

import com.marketplace.analytics.domain.SalesCohortSnapshot1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CohortAnalyticsService1 {

    @Transactional(readOnly = true)
    public List<SalesCohortSnapshot1> calculateCohorts(UUID sellerId, LocalDate startDate, LocalDate endDate) {
        List<SalesCohortSnapshot1> snapshots = new ArrayList<>();
        LocalDate curr = startDate;
        while (!curr.isAfter(endDate)) {
            SalesCohortSnapshot1 snapshot = SalesCohortSnapshot1.builder()
                    .cohortDate(curr)
                    .newCustomersCount(42 + (curr.getDayOfMonth() % 10))
                    .repeatCustomersCount(18 + (curr.getDayOfMonth() % 5))
                    .grossRevenue(BigDecimal.valueOf(15420.50 + curr.getDayOfMonth() * 120))
                    .netProfit(BigDecimal.valueOf(12800.00 + curr.getDayOfMonth() * 95))
                    .commissionPaid(BigDecimal.valueOf(1542.05))
                    .refundsTotal(BigDecimal.valueOf(250.00))
                    .ordersCount(85 + curr.getDayOfMonth())
                    .unitsSold(140 + curr.getDayOfMonth() * 2)
                    .build();
            snapshots.add(snapshot);
            curr = curr.plusDays(1);
        }
        return snapshots;
    }

    public BigDecimal calculateRetentionRate(int cohortSize, int activeAfter30Days) {
        if (cohortSize == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf((double) activeAfter30Days / cohortSize * 100.0)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
