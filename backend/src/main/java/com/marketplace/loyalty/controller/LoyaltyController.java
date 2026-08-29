package com.marketplace.loyalty.controller;

import com.marketplace.loyalty.dto.LoyaltyAccountDto;
import com.marketplace.loyalty.service.LoyaltyService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Customer Loyalty & Rewards", description = "Endpoints for point balances, tiers, and referral bonuses")
@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @Operation(summary = "Get current customer loyalty tier and point balance")
    @GetMapping("/my-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<LoyaltyAccountDto>> getMyAccount(@AuthenticationPrincipal UserPrincipal principal) {
        LoyaltyAccountDto dto = loyaltyService.getOrCreateAccount(principal.getId());
        return ResponseEntity.ok(Result.ok(dto));
    }
}
