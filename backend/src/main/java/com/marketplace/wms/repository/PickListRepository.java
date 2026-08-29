package com.marketplace.wms.repository;

import com.marketplace.wms.domain.PickList;
import com.marketplace.wms.domain.PickListStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PickListRepository extends JpaRepository<PickList, UUID> {
    Optional<PickList> findByPickListNumber(String number);
    Page<PickList> findByWarehouseIdOrderByCreatedAtDesc(UUID warehouseId, Pageable pageable);
    Page<PickList> findByStatus(PickListStatus status, Pageable pageable);
}
