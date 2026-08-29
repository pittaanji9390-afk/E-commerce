package com.marketplace.loyalty.repository;

import com.marketplace.loyalty.domain.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {
    Optional<GiftCard> findByCardCodeAndActiveTrue(String cardCode);
}
