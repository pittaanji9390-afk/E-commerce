package com.marketplace.advertising.controller;

import com.marketplace.advertising.dto.AdCampaignDto;
import com.marketplace.advertising.service.AdvertisingService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Seller Advertising & CPC Sponsored Ads", description = "Endpoints for sponsored product bidding, impression auctions, and campaign analytics")
@RestController
@RequestMapping("/api/v1/advertising/campaigns")
@RequiredArgsConstructor
public class AdvertisingController {

    private final AdvertisingService adService;

    @Operation(summary = "Create a new sponsored product ad campaign (Seller)")
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<AdCampaignDto>> createCampaign(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String name,
            @RequestParam UUID productId,
            @RequestParam BigDecimal dailyBudget,
            @RequestParam BigDecimal cpcBid) {
        AdCampaignDto campaign = adService.createCampaign(principal.getId(), name, productId, dailyBudget, cpcBid);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(campaign, "Ad Campaign launched."));
    }

    @Operation(summary = "Get seller ad campaigns with CTR and spend analytics")
    @GetMapping("/my-campaigns")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<AdCampaignDto>> getMyCampaigns(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdCampaignDto> page = adService.getSellerCampaigns(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
