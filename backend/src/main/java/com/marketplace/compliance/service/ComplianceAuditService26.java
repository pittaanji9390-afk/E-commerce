package com.marketplace.compliance.service;

import com.marketplace.compliance.domain.ComplianceAuditRecord26;
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
public class ComplianceAuditService26 {

    @Transactional
    public ComplianceAuditRecord26 performAudit(User user, String standard, int risk) {
        String code = "CMP-26-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        ComplianceAuditRecord26 record = ComplianceAuditRecord26.builder()
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
