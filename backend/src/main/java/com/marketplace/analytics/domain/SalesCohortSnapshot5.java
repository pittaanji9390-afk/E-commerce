package com.marketplace.analytics.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_cohort_snapshots_5")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesCohortSnapshot5 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "cohort_date", nullable = false)
    private LocalDate cohortDate;

    @Column(name = "new_customers_count", nullable = false)
    private int newCustomersCount;

    @Column(name = "repeat_customers_count", nullable = false)
    private int repeatCustomersCount;

    @Column(name = "gross_revenue", precision = 15, scale = 2, nullable = false)
    private BigDecimal grossRevenue;

    @Column(name = "net_profit", precision = 15, scale = 2, nullable = false)
    private BigDecimal netProfit;

    @Column(name = "commission_paid", precision = 15, scale = 2, nullable = false)
    private BigDecimal commissionPaid;

    @Column(name = "refunds_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal refundsTotal;

    @Column(name = "orders_count", nullable = false)
    private int ordersCount;

    @Column(name = "units_sold", nullable = false)
    private int unitsSold;
}
