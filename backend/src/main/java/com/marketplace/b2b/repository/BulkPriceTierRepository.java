package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.BulkPriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BulkPriceTierRepository extends JpaRepository<BulkPriceTier, UUID> {
    List<BulkPriceTier> findByVariantIdOrderByMinQuantityAsc(UUID variantId);
}
