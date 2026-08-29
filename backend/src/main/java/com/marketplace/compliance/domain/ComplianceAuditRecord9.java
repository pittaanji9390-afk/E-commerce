package com.marketplace.compliance.domain;

import com.marketplace.identity.domain.User;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "compliance_audit_records_9")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceAuditRecord9 extends AuditableEntity {

    @Column(name = "audit_code", nullable = false, unique = true, length = 60)
    private String auditCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audited_user_id")
    private User auditedUser;

    @Column(name = "compliance_standard", nullable = false, length = 100)
    private String complianceStandard;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @Column(name = "findings_summary", columnDefinition = "TEXT")
    private String findingsSummary;

    @Column(name = "remediation_plan", columnDefinition = "TEXT")
    private String remediationPlan;

    @Column(name = "is_cleared", nullable = false)
    @Builder.Default
    private boolean cleared = true;

    @Column(name = "cleared_at")
    private Instant clearedAt;
}
