package com.marketplace.audit.service;

import com.marketplace.audit.domain.AuditLog;
import com.marketplace.audit.dto.AuditLogDto;
import com.marketplace.audit.repository.AuditLogRepository;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logAction(UUID actorId, String actorRole, String ipAddress, String action,
                          String entityType, String entityId, String oldState, String newState, String requestId) {
        User actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;

        AuditLog logEntry = AuditLog.builder()
                .actor(actor)
                .actorRole(actorRole)
                .ipAddress(ipAddress)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldState(oldState)
                .newState(newState)
                .requestId(requestId)
                .build();

        auditLogRepository.save(logEntry);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getEntityAuditLogs(String entityType, String entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pageable).map(this::toDto);
    }

    private AuditLogDto toDto(AuditLog a) {
        return AuditLogDto.builder()
                .id(a.getId())
                .actorId(a.getActor() != null ? a.getActor().getId() : null)
                .actorEmail(a.getActor() != null ? a.getActor().getEmail() : "system")
                .actorRole(a.getActorRole())
                .ipAddress(a.getIpAddress())
                .action(a.getAction())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .oldState(a.getOldState())
                .newState(a.getNewState())
                .requestId(a.getRequestId())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
