package com.marketplace.order.controller;

import com.marketplace.order.dto.OrderDto;
import com.marketplace.order.dto.SellerOrderDto;
import com.marketplace.order.service.OrderService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Orders & Fulfillment", description = "Endpoints for customer and seller multi-order tracking")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get order details by ID")
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<OrderDto>> getOrderById(@PathVariable UUID orderId) {
        OrderDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(Result.ok(order));
    }

    @Operation(summary = "Get customer purchase history")
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResult<OrderDto>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderDto> orders = orderService.getCustomerOrders(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(orders));
    }

    @Operation(summary = "Get sub-orders assigned to seller")
    @GetMapping("/seller/my-sub-orders")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<SellerOrderDto>> getSellerSubOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SellerOrderDto> subOrders = orderService.getSellerOrders(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(subOrders));
    }
}
