package com.marketplace.shipping.domain;

import com.marketplace.order.domain.SellerOrder;
import com.marketplace.shared.domain.AuditableEntity;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;

    @Column(name = "carrier", nullable = false, length = 100)
    private String carrier;

    @Column(name = "tracking_number", nullable = false, length = 100)
    private String trackingNumber;

    @Column(name = "shipping_label_url", length = 500)
    private String shippingLabelUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.LABEL_CREATED;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "estimated_delivery")
    private Instant estimatedDelivery;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShipmentEvent> events = new ArrayList<>();

    public void addEvent(ShipmentEvent event) {
        events.add(event);
        event.setShipment(this);
    }
}
