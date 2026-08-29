package com.marketplace.subscription.repository;

import com.marketplace.subscription.domain.CustomerSubscription;
import com.marketplace.subscription.domain.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerSubscriptionRepository extends JpaRepository<CustomerSubscription, UUID> {
    Optional<CustomerSubscription> findBySubscriptionNumber(String subscriptionNumber);
    Page<CustomerSubscription> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    List<CustomerSubscription> findByStatusAndNextBillingDateLessThanEqual(SubscriptionStatus status, Instant date);
}
