package com.marketplace.seller.repository;

import com.marketplace.seller.domain.SellerVerification;
import com.marketplace.seller.domain.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerVerificationRepository extends JpaRepository<SellerVerification, UUID> {

    List<SellerVerification> findBySellerId(UUID sellerId);

    List<SellerVerification> findByStatus(VerificationStatus status);
}
