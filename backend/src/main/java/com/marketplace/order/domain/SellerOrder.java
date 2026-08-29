package com.marketplace.order.domain;

import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import com.marketplace.shipping.domain.Shipment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOrder extends AuditableEntity {

    @Column(name = "seller_order_number", nullable = false, unique = true, length = 60)
    private String sellerOrderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_order_id", nullable = false)
    private Order parentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

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

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "commission_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal commissionAmount;

    @Column(name = "net_seller_payable", precision = 15, scale = 2, nullable = false)
    private BigDecimal netSellerPayable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private SellerOrderStatus status = SellerOrderStatus.PENDING_PAYMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", length = 30, nullable = false)
    @Builder.Default
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @OneToMany(mappedBy = "sellerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sellerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Shipment> shipments = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setSellerOrder(this);
    }
}
