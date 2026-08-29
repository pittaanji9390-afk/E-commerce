package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "warehouse_pallet_slots_19")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehousePalletSlot19 extends BaseEntity {

    @Column(name = "pallet_barcode", nullable = false, unique = true, length = 50)
    private String palletBarcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "unit_count", nullable = false)
    private int unitCount;

    @Column(name = "weight_kg", precision = 10, scale = 2, nullable = false)
    private BigDecimal weightKg;

    @Column(name = "slot_code", length = 30, nullable = false)
    private String slotCode;

    @Column(name = "is_quarantined", nullable = false)
    @Builder.Default
    private boolean quarantined = false;
}
