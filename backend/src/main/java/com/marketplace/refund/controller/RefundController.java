package com.marketplace.refund.controller;

import com.marketplace.refund.domain.Refund;
import com.marketplace.refund.service.RefundService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Refunds & Reversals", description = "Endpoints for seller and admin refund processing")
@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @Operation(summary = "Process refund on a seller sub-order")
    @PostMapping("/seller-orders/{sellerOrderId}")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<Map<String, Object>>> refundSellerOrder(
            @PathVariable UUID sellerOrderId,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "Customer return") String reason,
            @AuthenticationPrincipal UserPrincipal principal) {
        Refund refund = refundService.processSellerOrderRefund(sellerOrderId, amount, reason, principal != null ? principal.getId() : null);
        return ResponseEntity.ok(Result.ok(Map.of(
                "refundId", refund.getId(),
                "status", refund.getStatus(),
                "amount", refund.getAmount(),
                "providerRefundId", refund.getProviderRefundId()
        ), "Refund processed successfully."));
    }
}
