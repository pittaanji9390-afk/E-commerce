package com.marketplace.inventory.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;

    @Column(name = "on_hand", nullable = false)
    @Builder.Default
    private int onHand = 0;

    @Column(name = "reserved", nullable = false)
    @Builder.Default
    private int reserved = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private int lowStockThreshold = 5;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryTransaction> transactions = new ArrayList<>();

    public int getAvailable() {
        return Math.max(0, onHand - reserved);
    }

    public boolean canReserve(int quantity) {
        return getAvailable() >= quantity;
    }

    public void reserve(int quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalStateException("Insufficient available inventory to reserve " + quantity + " units.");
        }
        this.reserved += quantity;
    }

    public void releaseReservation(int quantity) {
        this.reserved = Math.max(0, this.reserved - quantity);
    }

    public void commitSale(int quantity) {
        this.reserved = Math.max(0, this.reserved - quantity);
        this.onHand = Math.max(0, this.onHand - quantity);
    }

    public void restock(int quantity) {
        this.onHand += quantity;
    }
}
