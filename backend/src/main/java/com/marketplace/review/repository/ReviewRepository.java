package com.marketplace.review.repository;

import com.marketplace.review.domain.Review;
import com.marketplace.review.domain.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByProductIdAndStatus(UUID productId, ReviewStatus status, Pageable pageable);

    Optional<Review> findByCustomerIdAndProductId(UUID customerId, UUID productId);

    boolean existsByCustomerIdAndProductId(UUID customerId, UUID productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    Double calculateProductAverageRating(UUID productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    long countApprovedProductReviews(UUID productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.seller.id = :sellerId AND r.status = 'APPROVED'")
    Double calculateSellerAverageRating(UUID sellerId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.seller.id = :sellerId AND r.status = 'APPROVED'")
    long countApprovedSellerReviews(UUID sellerId);
}
