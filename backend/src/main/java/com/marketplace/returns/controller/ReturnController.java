package com.marketplace.returns.controller;

import com.marketplace.returns.dto.CreateReturnRequest;
import com.marketplace.returns.dto.ReturnDto;
import com.marketplace.returns.dto.ReviewReturnRequest;
import com.marketplace.returns.service.ReturnService;
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

import java.util.UUID;

@Tag(name = "Returns & RMA", description = "Endpoints for customer return merchandise authorizations and vendor inspections")
@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @Operation(summary = "Submit a return RMA request for a delivered order")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<ReturnDto>> createReturn(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReturnRequest request) {
        ReturnDto returnDto = returnService.createReturnRequest(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(returnDto, "Return RMA request submitted."));
    }

    @Operation(summary = "Get customer return history")
    @GetMapping("/my-returns")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResult<ReturnDto>> getMyReturns(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReturnDto> page = returnService.getCustomerReturns(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Get returns submitted against seller store")
    @GetMapping("/seller/my-returns")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<ReturnDto>> getSellerReturns(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReturnDto> page = returnService.getSellerReturns(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Review, inspect, or approve return RMA (Seller / Admin)")
    @PatchMapping("/{returnId}/review")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<ReturnDto>> reviewReturn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID returnId,
            @Valid @RequestBody ReviewReturnRequest request) {
        ReturnDto updated = returnService.reviewReturnRequest(principal.getId(), returnId, request);
        return ResponseEntity.ok(Result.ok(updated, "Return request updated."));
    }
}
