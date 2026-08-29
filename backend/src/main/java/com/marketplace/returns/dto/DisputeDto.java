package com.marketplace.returns.dto;

import com.marketplace.returns.domain.DisputeStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeDto {
    private UUID id;
    private String disputeNumber;
    private UUID sellerOrderId;
    private UUID returnId;
    private UUID customerId;
    private UUID sellerId;
    private String sellerName;
    private String reason;
    private String description;
    private String evidenceUrls;
    private DisputeStatus status;
    private String resolutionNotes;
    private Instant createdAt;
    private Instant resolvedAt;
}
