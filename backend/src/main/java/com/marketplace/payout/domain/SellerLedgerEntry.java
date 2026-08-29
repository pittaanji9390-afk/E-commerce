package com.marketplace.payout.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "seller_ledger_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerLedgerEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", length = 30, nullable = false)
    private SellerLedgerType entryType;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "running_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal runningBalance;

    @Column(name = "reference_type", length = 50, nullable = false)
    private String referenceType;

    @Column(name = "reference_id", length = 100, nullable = false)
    private String referenceId;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
