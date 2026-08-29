package com.marketplace.seller.repository;

import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {

    Optional<Seller> findByStoreSlug(String storeSlug);

    boolean existsByStoreSlug(String storeSlug);

    List<Seller> findByStatus(SellerStatus status);
}
