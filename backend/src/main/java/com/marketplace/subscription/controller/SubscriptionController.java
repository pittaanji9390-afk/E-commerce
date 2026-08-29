package com.marketplace.subscription.controller;

import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import com.marketplace.subscription.dto.CreateSubscriptionRequest;
import com.marketplace.subscription.dto.SubscriptionDto;
import com.marketplace.subscription.service.SubscriptionService;
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

import java.util.UUID;

@Tag(name = "Subscriptions & Recurring Billing", description = "Endpoints for subscribe & save auto-delivery and interval plans")
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Subscribe & Save to a product variant")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> subscribe(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionDto sub = subscriptionService.subscribe(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(sub, "Subscription activated with recurring discount."));
    }

    @Operation(summary = "Get current buyer recurring subscriptions")
    @GetMapping("/my-subscriptions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResult<SubscriptionDto>> getMySubscriptions(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SubscriptionDto> page = subscriptionService.getCustomerSubscriptions(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Pause an active auto-delivery subscription")
    @PatchMapping("/{subscriptionId}/pause")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> pause(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID subscriptionId) {
        SubscriptionDto sub = subscriptionService.pauseSubscription(principal.getId(), subscriptionId);
        return ResponseEntity.ok(Result.ok(sub, "Subscription paused."));
    }

    @Operation(summary = "Resume a paused subscription")
    @PatchMapping("/{subscriptionId}/resume")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SubscriptionDto>> resume(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID subscriptionId) {
        SubscriptionDto sub = subscriptionService.resumeSubscription(principal.getId(), subscriptionId);
        return ResponseEntity.ok(Result.ok(sub, "Subscription resumed."));
    }
}
