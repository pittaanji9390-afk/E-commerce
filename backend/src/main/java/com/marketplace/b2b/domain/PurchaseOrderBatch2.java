package com.marketplace.b2b.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_order_batches_2")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderBatch2 extends AuditableEntity {

    @Column(name = "po_number", nullable = false, unique = true, length = 60)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Customer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "corporate_entity_name", nullable = false, length = 200)
    private String corporateEntityName;

    @Column(name = "subtotal_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "payment_due_date", nullable = false)
    private LocalDate paymentDueDate;

    @Column(name = "is_settled", nullable = false)
    @Builder.Default
    private boolean settled = false;
}
