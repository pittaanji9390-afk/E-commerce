package com.marketplace.payment.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payment_webhooks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhook extends BaseEntity {

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_event_id", nullable = false, unique = true, length = 255)
    private String providerEventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload_json", columnDefinition = "JSONB", nullable = false)
    private String payloadJson;

    @Column(name = "signature_header", nullable = false, length = 500)
    private String signatureHeader;

    @Column(name = "processed_status", nullable = false, length = 30)
    @Builder.Default
    private String processedStatus = "RECEIVED";

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    @Column(name = "received_at", nullable = false)
    @Builder.Default
    private Instant receivedAt = Instant.now();
}
