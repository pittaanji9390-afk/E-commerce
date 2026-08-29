package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pick_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private WarehouseBin bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "quantity_to_pick", nullable = false)
    private int quantityToPick;

    @Column(name = "quantity_picked", nullable = false)
    @Builder.Default
    private int quantityPicked = 0;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;
}
