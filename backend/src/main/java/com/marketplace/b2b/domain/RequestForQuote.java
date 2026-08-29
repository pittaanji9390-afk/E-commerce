package com.marketplace.b2b.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfq_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestForQuote extends AuditableEntity {

    @Column(name = "rfq_number", nullable = false, unique = true, length = 50)
    private String rfqNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "tax_exemption_number", length = 100)
    private String taxExemptionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_terms", length = 30, nullable = false)
    @Builder.Default
    private CreditTermType creditTerms = CreditTermType.PREPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.SUBMITTED;

    @Column(name = "target_price", precision = 15, scale = 2)
    private BigDecimal targetPrice;

    @Column(name = "quoted_total", precision = 15, scale = 2)
    private BigDecimal quotedTotal;

    @Column(name = "buyer_message", columnDefinition = "TEXT")
    private String buyerMessage;

    @Column(name = "seller_notes", columnDefinition = "TEXT")
    private String sellerNotes;

    @Column(name = "valid_until")
    private Instant validUntil;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RfqItem> items = new ArrayList<>();

    public void addItem(RfqItem item) {
        items.add(item);
        item.setRfq(this);
    }
}
