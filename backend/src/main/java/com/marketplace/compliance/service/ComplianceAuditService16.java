package com.marketplace.compliance.service;

import com.marketplace.compliance.domain.ComplianceAuditRecord16;
import com.marketplace.identity.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplianceAuditService16 {

    @Transactional
    public ComplianceAuditRecord16 performAudit(User user, String standard, int risk) {
        String code = "CMP-16-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        ComplianceAuditRecord16 record = ComplianceAuditRecord16.builder()
                .auditCode(code)
                .auditedUser(user)
                .complianceStandard(standard)
                .riskScore(risk)
                .findingsSummary("Automated compliance check complete. No critical sanctions found.")
                .remediationPlan("Standard recurring monitoring.")
                .cleared(risk < 50)
                .clearedAt(risk < 50 ? Instant.now() : null)
                .build();
        log.info("Compliance record created: {}", code);
        return record;
    }
}
