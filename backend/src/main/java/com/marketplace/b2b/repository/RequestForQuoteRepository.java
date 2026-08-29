package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.QuoteStatus;
import com.marketplace.b2b.domain.RequestForQuote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestForQuoteRepository extends JpaRepository<RequestForQuote, UUID> {
    Optional<RequestForQuote> findByRfqNumber(String rfqNumber);
    Page<RequestForQuote> findByBuyerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    Page<RequestForQuote> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);
    Page<RequestForQuote> findByStatus(QuoteStatus status, Pageable pageable);
}
