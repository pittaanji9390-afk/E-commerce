package com.marketplace.review.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.order.domain.OrderItem;
import com.marketplace.product.domain.Product;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "product_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "is_verified_purchase", nullable = false)
    @Builder.Default
    private boolean verifiedPurchase = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.APPROVED;

    @Column(name = "helpful_votes", nullable = false)
    @Builder.Default
    private int helpfulVotes = 0;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    public void addImage(ReviewImage image) {
        images.add(image);
        image.setReview(this);
    }
}
