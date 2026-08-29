package com.marketplace.messaging.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "customer_inquiry_tickets_30")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerInquiryTicket30 extends AuditableEntity {

    @Column(name = "ticket_number", nullable = false, unique = true, length = 60)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "inquiry_subject", nullable = false, length = 200)
    private String inquirySubject;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "priority", length = 20, nullable = false)
    @Builder.Default
    private String priority = "NORMAL";

    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private String status = "OPEN";

    @Column(name = "message_body", columnDefinition = "TEXT", nullable = false)
    private String messageBody;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
