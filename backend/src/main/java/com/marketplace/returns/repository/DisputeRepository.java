package com.marketplace.returns.repository;

import com.marketplace.returns.domain.Dispute;
import com.marketplace.returns.domain.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {

    Optional<Dispute> findByDisputeNumber(String disputeNumber);

    Page<Dispute> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);

    Page<Dispute> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    Page<Dispute> findByStatusOrderByCreatedAtDesc(DisputeStatus status, Pageable pageable);
}
