package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "warehouse_bins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseBin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "zone_code", nullable = false, length = 20)
    private String zoneCode;

    @Column(name = "aisle", nullable = false, length = 20)
    private String aisle;

    @Column(name = "shelf", nullable = false, length = 20)
    private String shelf;

    @Column(name = "bin_code", nullable = false, unique = true, length = 50)
    private String binCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant assignedVariant;

    @Column(name = "quantity_on_hand", nullable = false)
    @Builder.Default
    private int quantityOnHand = 0;

    @Column(name = "max_capacity", nullable = false)
    @Builder.Default
    private int maxCapacity = 500;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
