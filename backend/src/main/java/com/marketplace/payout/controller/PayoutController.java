package com.marketplace.payout.controller;

import com.marketplace.payout.dto.RequestPayoutRequest;
import com.marketplace.payout.dto.SellerBalanceSummaryDto;
import com.marketplace.payout.dto.SellerLedgerDto;
import com.marketplace.payout.dto.SellerPayoutDto;
import com.marketplace.payout.service.PayoutService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payouts & Escrow Ledger", description = "Endpoints for merchant balance management, double-entry ledger, and ACH payouts")
@RestController
@RequestMapping("/api/v1/seller/finance")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    @Operation(summary = "Get current available and escrow balances")
    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<SellerBalanceSummaryDto>> getBalanceSummary(@AuthenticationPrincipal UserPrincipal principal) {
        SellerBalanceSummaryDto summary = payoutService.getBalanceSummary(principal.getId());
        return ResponseEntity.ok(Result.ok(summary));
    }

    @Operation(summary = "Get double-entry audit ledger entries (Paginated)")
    @GetMapping("/ledger")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<SellerLedgerDto>> getLedger(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SellerLedgerDto> page = payoutService.getLedgerEntries(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Request payout transfer from available balance to linked bank account")
    @PostMapping("/payouts")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<SellerPayoutDto>> requestPayout(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RequestPayoutRequest request) {
        SellerPayoutDto payout = payoutService.requestPayout(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(payout, "Payout transfer initiated."));
    }

    @Operation(summary = "Get payout withdrawal history (Paginated)")
    @GetMapping("/payouts")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<SellerPayoutDto>> getPayoutHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SellerPayoutDto> page = payoutService.getPayoutHistory(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
