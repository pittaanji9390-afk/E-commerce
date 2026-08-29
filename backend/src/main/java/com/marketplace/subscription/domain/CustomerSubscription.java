package com.marketplace.subscription.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSubscription extends AuditableEntity {

    @Column(name = "subscription_number", nullable = false, unique = true, length = 50)
    private String subscriptionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private CustomerAddress shippingAddress;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Column(name = "recurring_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal recurringPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "next_billing_date", nullable = false)
    private Instant nextBillingDate;

    @Column(name = "last_billed_at")
    private Instant lastBilledAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;
}
