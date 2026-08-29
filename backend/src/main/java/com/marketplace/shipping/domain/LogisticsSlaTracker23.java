package com.marketplace.shipping.domain;

import com.marketplace.order.domain.SellerOrder;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "logistics_sla_trackers_23")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsSlaTracker23 extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;

    @Column(name = "carrier_name", nullable = false, length = 100)
    private String carrierName;

    @Column(name = "promised_delivery_date", nullable = false)
    private Instant promisedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    @Column(name = "is_sla_breached", nullable = false)
    @Builder.Default
    private boolean slaBreached = false;

    @Column(name = "delay_hours", nullable = false)
    @Builder.Default
    private int delayHours = 0;
}
