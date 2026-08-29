package com.marketplace.seller.repository;

import com.marketplace.seller.domain.SellerBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerBankAccountRepository extends JpaRepository<SellerBankAccount, UUID> {

    List<SellerBankAccount> findBySellerId(UUID sellerId);
}
