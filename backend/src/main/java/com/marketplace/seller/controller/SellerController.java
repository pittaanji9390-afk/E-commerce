package com.marketplace.seller.controller;

import com.marketplace.security.UserPrincipal;
import com.marketplace.seller.dto.SellerOnboardingRequest;
import com.marketplace.seller.dto.SellerProfileDto;
import com.marketplace.seller.dto.UpdateSellerProfileRequest;
import com.marketplace.seller.service.SellerService;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Seller Management", description = "Endpoints for merchant onboarding, KYC verification, and store profile")
@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "Submit onboarding application and KYC verification")
    @PostMapping("/onboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<SellerProfileDto>> onboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SellerOnboardingRequest request) {
        SellerProfileDto profile = sellerService.onboardSeller(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.ok(profile, "Seller onboarding application submitted for review."));
    }

    @Operation(summary = "Get current authenticated merchant store profile")
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<SellerProfileDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        SellerProfileDto profile = sellerService.getProfile(principal.getId());
        return ResponseEntity.ok(Result.ok(profile));
    }

    @Operation(summary = "Update seller store profile")
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<SellerProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateSellerProfileRequest request) {
        SellerProfileDto updated = sellerService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(Result.ok(updated, "Store profile updated successfully."));
    }

    @Operation(summary = "Get public seller store profile by slug")
    @GetMapping("/store/{slug}")
    public ResponseEntity<Result<SellerProfileDto>> getStoreBySlug(@PathVariable String slug) {
        SellerProfileDto profile = sellerService.getProfileBySlug(slug);
        return ResponseEntity.ok(Result.ok(profile));
    }

    @Operation(summary = "Admin KYC approval for seller")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<Void>> approveSeller(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        sellerService.approveSellerKyc(id, principal.getId());
        return ResponseEntity.ok(Result.ok(null, "Seller KYC approved successfully."));
    }
}
