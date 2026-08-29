package com.marketplace.wms.repository;

import com.marketplace.wms.domain.StockTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID> {
    Optional<StockTransfer> findByTransferNumber(String transferNumber);
    Page<StockTransfer> findBySourceWarehouseIdOrDestinationWarehouseIdOrderByCreatedAtDesc(UUID src, UUID dest, Pageable pageable);
}
