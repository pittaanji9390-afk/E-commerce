package com.marketplace.subscription.repository;

import com.marketplace.subscription.domain.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice, UUID> {
    List<SubscriptionInvoice> findBySubscriptionIdOrderByCreatedAtDesc(UUID subscriptionId);
}
