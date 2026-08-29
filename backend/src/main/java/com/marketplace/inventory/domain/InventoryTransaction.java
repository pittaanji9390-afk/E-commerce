package com.marketplace.inventory.domain;

import com.marketplace.identity.domain.User;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 30, nullable = false)
    private InventoryTransactionType transactionType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "previous_on_hand", nullable = false)
    private int previousOnHand;

    @Column(name = "new_on_hand", nullable = false)
    private int newOnHand;

    @Column(name = "previous_reserved", nullable = false)
    private int previousReserved;

    @Column(name = "new_reserved", nullable = false)
    private int newReserved;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
