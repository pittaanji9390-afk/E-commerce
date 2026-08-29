package com.marketplace.inventory.ledger;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "inventory_fifo_valuation_records_8")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryFifoValuationRecord8 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "batch_receipt_number", nullable = false, length = 60)
    private String batchReceiptNumber;

    @Column(name = "quantity_received", nullable = false)
    private int quantityReceived;

    @Column(name = "quantity_remaining", nullable = false)
    private int quantityRemaining;

    @Column(name = "unit_cost_basis", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitCostBasis;

    @Column(name = "landed_cost_adjustment", precision = 15, scale = 2, nullable = false)
    private BigDecimal landedCostAdjustment;

    @Column(name = "received_at", nullable = false)
    @Builder.Default
    private Instant receivedAt = Instant.now();
}
