package com.marketplace.shipping.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentEventDto {
    private UUID id;
    private String status;
    private String location;
    private String description;
    private Instant eventTimestamp;
}
