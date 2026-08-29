package com.marketplace.audit.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {
    private UUID id;
    private UUID actorId;
    private String actorEmail;
    private String actorRole;
    private String ipAddress;
    private String action;
    private String entityType;
    private String entityId;
    private String oldState;
    private String newState;
    private String requestId;
    private Instant createdAt;
}
