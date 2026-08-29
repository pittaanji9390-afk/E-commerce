package com.marketplace.payout.escrow;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "escrow_dispute_reserves_46")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscrowDisputeReserve46 extends AuditableEntity {

    @Column(name = "reserve_reference", nullable = false, unique = true, length = 60)
    private String reserveReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "held_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal heldAmount;

    @Column(name = "reserve_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal reservePercentage;

    @Column(name = "release_scheduled_at", nullable = false)
    private Instant releaseScheduledAt;

    @Column(name = "is_released", nullable = false)
    @Builder.Default
    private boolean released = false;

    @Column(name = "reason", length = 255)
    private String reason;
}
