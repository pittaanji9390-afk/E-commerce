package com.marketplace.wms.domain;

import com.marketplace.order.domain.SellerOrder;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickList extends AuditableEntity {

    @Column(name = "pick_list_number", nullable = false, unique = true, length = 50)
    private String pickListNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private PickListStatus status = PickListStatus.GENERATED;

    @Column(name = "assigned_picker", length = 100)
    private String assignedPicker;

    @OneToMany(mappedBy = "pickList", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PickListItem> items = new ArrayList<>();

    public void addItem(PickListItem item) {
        items.add(item);
        item.setPickList(this);
    }
}
