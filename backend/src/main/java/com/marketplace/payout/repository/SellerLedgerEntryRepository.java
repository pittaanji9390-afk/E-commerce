package com.marketplace.payout.repository;

import com.marketplace.payout.domain.SellerLedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerLedgerEntryRepository extends JpaRepository<SellerLedgerEntry, UUID> {

    Page<SellerLedgerEntry> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    @Query("SELECT e FROM SellerLedgerEntry e WHERE e.seller.id = :sellerId ORDER BY e.createdAt DESC LIMIT 1")
    Optional<SellerLedgerEntry> findLatestEntry(UUID sellerId);

    @Query("SELECT COALESCE(SUM(CASE WHEN e.entryType = 'ORDER_CREDIT' THEN e.amount ELSE -e.amount END), 0) FROM SellerLedgerEntry e WHERE e.seller.id = :sellerId")
    BigDecimal computeCurrentBalance(UUID sellerId);
}
