package com.marketplace.payment.repository;

import com.marketplace.payment.domain.PaymentWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, UUID> {

    Optional<PaymentWebhook> findByProviderEventId(String providerEventId);

    boolean existsByProviderEventId(String providerEventId);
}
