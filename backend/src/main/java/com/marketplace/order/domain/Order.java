package com.marketplace.order.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends AuditableEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "grand_total", precision = 15, scale = 2, nullable = false)
    private BigDecimal grandTotal;

    @Column(name = "currency", length = 3, nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30, nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 30, nullable = false)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING_PAYMENT;

    @Column(name = "shipping_address_json", columnDefinition = "JSONB", nullable = false)
    private String shippingAddressJson;

    @Column(name = "billing_address_json", columnDefinition = "JSONB", nullable = false)
    private String billingAddressJson;

    @Column(name = "idempotency_key", length = 100, unique = true)
    private String idempotencyKey;

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SellerOrder> sellerOrders = new ArrayList<>();

    public void addSellerOrder(SellerOrder sellerOrder) {
        sellerOrders.add(sellerOrder);
        sellerOrder.setParentOrder(this);
    }
}
