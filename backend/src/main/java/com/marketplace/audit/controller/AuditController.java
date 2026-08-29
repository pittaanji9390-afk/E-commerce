package com.marketplace.audit.controller;

import com.marketplace.audit.dto.AuditLogDto;
import com.marketplace.audit.service.AuditService;
import com.marketplace.shared.response.PagedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Compliance & Audit Trail", description = "Endpoints for immutable system action logging and forensics")
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @Operation(summary = "Get immutable platform audit trail (Admin / Super Admin)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'COMPLIANCE_OFFICER')")
    public ResponseEntity<PagedResult<AuditLogDto>> getAuditLogs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLogDto> page = auditService.getAuditLogs(pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Get audit logs for a specific entity ID")
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'COMPLIANCE_OFFICER')")
    public ResponseEntity<PagedResult<AuditLogDto>> getEntityLogs(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditLogDto> page = auditService.getEntityAuditLogs(entityType, entityId, pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
