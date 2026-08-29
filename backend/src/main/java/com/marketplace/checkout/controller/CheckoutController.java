package com.marketplace.checkout.controller;

import com.marketplace.checkout.service.CheckoutService;
import com.marketplace.order.dto.CreateOrderRequest;
import com.marketplace.order.dto.OrderDto;
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

@Tag(name = "Checkout Orchestration", description = "Endpoints for initiating multi-seller composite checkout sagas")
@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Operation(summary = "Execute idempotent multi-seller order creation & inventory reservation")
    @PostMapping("/create-order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<OrderDto>> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader(value = "Idempotency-Key", required = false) String headerIdempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        
        if (headerIdempotencyKey != null && !headerIdempotencyKey.isBlank()) {
            request.setIdempotencyKey(headerIdempotencyKey);
        }

        OrderDto order = checkoutService.processCheckout(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.ok(order, "Order placed successfully. Proceed to payment authorization."));
    }
}
