package com.marketplace.returns.repository;

import com.marketplace.returns.domain.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, UUID> {

    List<ReturnItem> findByReturnRequestId(UUID returnId);
}
