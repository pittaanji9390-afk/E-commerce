package com.marketplace.order.repository;

import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.domain.SellerOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerOrderRepository extends JpaRepository<SellerOrder, UUID> {

    Optional<SellerOrder> findBySellerOrderNumber(String sellerOrderNumber);

    List<SellerOrder> findByParentOrderId(UUID parentOrderId);

    Page<SellerOrder> findBySellerId(UUID sellerId, Pageable pageable);

    Page<SellerOrder> findBySellerIdAndStatus(UUID sellerId, SellerOrderStatus status, Pageable pageable);
}
