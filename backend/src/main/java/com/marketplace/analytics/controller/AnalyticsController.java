package com.marketplace.analytics.controller;

import com.marketplace.analytics.dto.AdminPlatformAnalyticsDto;
import com.marketplace.analytics.dto.SellerDashboardAnalyticsDto;
import com.marketplace.analytics.service.AnalyticsService;
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

@Tag(name = "Analytics & Business Intelligence", description = "Endpoints for merchant metrics, GMV, and platform performance")
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get merchant sales metrics, revenue, and AOV")
    @GetMapping("/seller")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<SellerDashboardAnalyticsDto>> getSellerDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        SellerDashboardAnalyticsDto analytics = analyticsService.getSellerAnalytics(principal.getId());
        return ResponseEntity.ok(Result.ok(analytics));
    }

    @Operation(summary = "Get platform-wide GMV, commission take rate, and growth metrics (Admin)")
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<AdminPlatformAnalyticsDto>> getPlatformDashboard() {
        AdminPlatformAnalyticsDto analytics = analyticsService.getPlatformAnalytics();
        return ResponseEntity.ok(Result.ok(analytics));
    }
}
