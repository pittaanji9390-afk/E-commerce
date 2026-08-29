package com.marketplace.forex.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "forex_hedging_contracts_14")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForexHedgingContract14 extends AuditableEntity {

    @Column(name = "contract_reference", nullable = false, unique = true, length = 50)
    private String contractReference;

    @Column(name = "base_currency", length = 3, nullable = false)
    private String baseCurrency;

    @Column(name = "quote_currency", length = 3, nullable = false)
    private String quoteCurrency;

    @Column(name = "locked_rate", precision = 12, scale = 6, nullable = false)
    private BigDecimal lockedRate;

    @Column(name = "notional_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal notionalAmount;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "is_executed", nullable = false)
    @Builder.Default
    private boolean executed = false;
}
