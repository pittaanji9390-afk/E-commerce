package com.marketplace.b2b.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "b2b_credit_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class B2BCreditAccount extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    @Column(name = "credit_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "available_credit", precision = 15, scale = 2, nullable = false)
    private BigDecimal availableCredit;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_terms", length = 30, nullable = false)
    @Builder.Default
    private CreditTermType defaultTerms = CreditTermType.NET_30;

    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private boolean approved = false;
}
