package com.marketplace.loyalty.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.loyalty.domain.LoyaltyAccount;
import com.marketplace.loyalty.domain.LoyaltyTier;
import com.marketplace.loyalty.dto.LoyaltyAccountDto;
import com.marketplace.loyalty.repository.LoyaltyAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final CustomerService customerService;

    @Transactional
    public LoyaltyAccountDto getOrCreateAccount(UUID customerId) {
        return accountRepository.findById(customerId)
                .map(this::toDto)
                .orElseGet(() -> {
                    Customer customer = customerService.getOrCreateCustomer(customerId);
                    String refCode = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    LoyaltyAccount account = LoyaltyAccount.builder()
                            .customer(customer)
                            .currentPointsBalance(100) // Welcome bonus
                            .lifetimePointsEarned(100)
                            .tier(LoyaltyTier.BRONZE)
                            .referralCode(refCode)
                            .build();
                    return toDto(accountRepository.save(account));
                });
    }

    @Transactional
    public void awardPoints(UUID customerId, int points, String reason) {
        LoyaltyAccount account = accountRepository.findById(customerId).orElse(null);
        if (account != null) {
            account.setCurrentPointsBalance(account.getCurrentPointsBalance() + points);
            account.setLifetimePointsEarned(account.getLifetimePointsEarned() + points);
            updateTier(account);
            accountRepository.save(account);
            log.info("Awarded {} loyalty points to customer {}. Reason: {}", points, customerId, reason);
        }
    }

    private void updateTier(LoyaltyAccount a) {
        int pts = a.getLifetimePointsEarned();
        if (pts >= 10000) a.setTier(LoyaltyTier.DIAMOND);
        else if (pts >= 5000) a.setTier(LoyaltyTier.PLATINUM);
        else if (pts >= 2000) a.setTier(LoyaltyTier.GOLD);
        else if (pts >= 500) a.setTier(LoyaltyTier.SILVER);
        else a.setTier(LoyaltyTier.BRONZE);
    }

    private LoyaltyAccountDto toDto(LoyaltyAccount a) {
        return LoyaltyAccountDto.builder()
                .customerId(a.getCustomer().getId())
                .currentPointsBalance(a.getCurrentPointsBalance())
                .lifetimePointsEarned(a.getLifetimePointsEarned())
                .tier(a.getTier())
                .referralCode(a.getReferralCode())
                .pointCashValueUsd(a.getCurrentPointsBalance() * 0.01) // 100 pts = $1.00
                .build();
    }
}
