package com.marketplace.wms.repository;

import com.marketplace.wms.domain.WarehouseBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseBinRepository extends JpaRepository<WarehouseBin, UUID> {
    Optional<WarehouseBin> findByBinCode(String binCode);
    List<WarehouseBin> findByWarehouseId(UUID warehouseId);
    List<WarehouseBin> findByAssignedVariantId(UUID variantId);
}
