package com.marketplace.returns.repository;

import com.marketplace.returns.domain.Return;
import com.marketplace.returns.domain.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReturnRepository extends JpaRepository<Return, UUID> {

    Optional<Return> findByReturnNumber(String returnNumber);

    Page<Return> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);

    Page<Return> findBySellerOrderSellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    Page<Return> findByStatus(ReturnStatus status, Pageable pageable);
}
