package com.marketplace.coupon.controller;

import com.marketplace.coupon.dto.ApplyCouponRequest;
import com.marketplace.coupon.dto.CouponDiscountResult;
import com.marketplace.coupon.dto.CouponDto;
import com.marketplace.coupon.dto.CreateCouponRequest;
import com.marketplace.coupon.service.CouponService;
import com.marketplace.security.UserPrincipal;
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

@Tag(name = "Coupons & Discounts", description = "Endpoints for creating and validating marketplace and seller coupons")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Validate and preview coupon discount against cart subtotal")
    @PostMapping("/validate")
    public ResponseEntity<Result<CouponDiscountResult>> validateCoupon(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApplyCouponRequest request) {
        UUID customerId = principal != null ? principal.getId() : null;
        CouponDiscountResult result = couponService.validateAndCalculateDiscount(customerId, request);
        return ResponseEntity.ok(Result.ok(result, "Coupon applied successfully."));
    }

    @Operation(summary = "Create coupon (Seller or Admin)")
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<CouponDto>> createCoupon(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateCouponRequest request) {
        // If seller, scope to own store
        if (principal.hasRole(com.marketplace.security.RoleEnum.ROLE_SELLER) && request.getSellerId() == null) {
            request.setSellerId(principal.getId());
        }
        CouponDto coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(coupon, "Coupon created."));
    }
}
