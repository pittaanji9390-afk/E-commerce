package com.marketplace.loyalty.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccount extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    @Column(name = "current_points_balance", nullable = false)
    @Builder.Default
    private int currentPointsBalance = 0;

    @Column(name = "lifetime_points_earned", nullable = false)
    @Builder.Default
    private int lifetimePointsEarned = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 30, nullable = false)
    @Builder.Default
    private LoyaltyTier tier = LoyaltyTier.BRONZE;

    @Column(name = "referral_code", nullable = false, unique = true, length = 30)
    private String referralCode;
}
