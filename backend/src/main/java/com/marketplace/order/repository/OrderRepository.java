package com.marketplace.order.repository;

import com.marketplace.order.domain.Order;
import com.marketplace.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
}
