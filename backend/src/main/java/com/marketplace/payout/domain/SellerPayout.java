package com.marketplace.payout.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerBankAccount;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "seller_payouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerPayout extends AuditableEntity {

    @Column(name = "payout_batch_reference", nullable = false, unique = true, length = 100)
    private String payoutBatchReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private SellerBankAccount bankAccount;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private PayoutBatchStatus status = PayoutBatchStatus.INITIATED;

    @Column(name = "gateway_payout_id", length = 255)
    private String gatewayPayoutId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "processed_at")
    private Instant processedAt;
}
