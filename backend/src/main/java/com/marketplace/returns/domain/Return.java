package com.marketplace.returns.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return extends AuditableEntity {

    @Column(name = "return_number", nullable = false, unique = true, length = 50)
    private String returnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 50, nullable = false)
    private ReturnReason reason;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "evidence_urls", columnDefinition = "JSONB")
    private String evidenceUrls;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private ReturnStatus status = ReturnStatus.REQUESTED;

    @Column(name = "seller_response_notes", columnDefinition = "TEXT")
    private String sellerResponseNotes;

    @Column(name = "refund_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReturnItem> items = new ArrayList<>();

    public void addItem(ReturnItem item) {
        items.add(item);
        item.setReturnRequest(this);
    }
}
