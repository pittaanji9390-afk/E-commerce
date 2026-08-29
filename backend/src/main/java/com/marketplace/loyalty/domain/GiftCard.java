package com.marketplace.loyalty.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "gift_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCard extends AuditableEntity {

    @Column(name = "card_code", nullable = false, unique = true, length = 30)
    private String cardCode;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(name = "initial_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "current_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchased_by_customer_id")
    private Customer purchasedBy;

    @Column(name = "recipient_email", length = 150)
    private String recipientEmail;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
