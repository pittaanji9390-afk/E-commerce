package com.marketplace.payout.repository;

import com.marketplace.payout.domain.PayoutBatchStatus;
import com.marketplace.payout.domain.SellerPayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerPayoutRepository extends JpaRepository<SellerPayout, UUID> {

    Page<SellerPayout> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    List<SellerPayout> findByStatus(PayoutBatchStatus status);
}
