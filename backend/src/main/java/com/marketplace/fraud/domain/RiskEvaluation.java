package com.marketplace.fraud.domain;

import com.marketplace.order.domain.Order;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "risk_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskEvaluation extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "risk_score", nullable = false)
    private int riskScore; // 0 - 100

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 30, nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_fingerprint", length = 150)
    private String deviceFingerprint;

    @Column(name = "flags_json", columnDefinition = "TEXT")
    private String flagsJson;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
