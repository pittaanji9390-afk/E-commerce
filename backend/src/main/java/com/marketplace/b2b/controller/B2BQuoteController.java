package com.marketplace.b2b.controller;

import com.marketplace.b2b.dto.CreateRfqRequest;
import com.marketplace.b2b.dto.RfqDto;
import com.marketplace.b2b.dto.SellerQuoteProposalRequest;
import com.marketplace.b2b.service.B2BQuoteService;
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

@Tag(name = "B2B Wholesale & RFQ Quotations", description = "Endpoints for wholesale quotation negotiation and bulk purchase orders")
@RestController
@RequestMapping("/api/v1/b2b/rfq")
@RequiredArgsConstructor
public class B2BQuoteController {

    private final B2BQuoteService quoteService;

    @Operation(summary = "Submit a wholesale RFQ request (Buyer)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<RfqDto>> submitRfq(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateRfqRequest request) {
        RfqDto rfq = quoteService.submitRfq(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(rfq, "Wholesale RFQ submitted to seller."));
    }

    @Operation(summary = "Propose wholesale quotation response (Seller)")
    @PostMapping("/{rfqId}/propose")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<Result<RfqDto>> submitProposal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID rfqId,
            @Valid @RequestBody SellerQuoteProposalRequest request) {
        RfqDto rfq = quoteService.submitSellerProposal(principal.getId(), rfqId, request);
        return ResponseEntity.ok(Result.ok(rfq, "Quotation proposal submitted."));
    }

    @Operation(summary = "Accept seller quotation and lock wholesale terms (Buyer)")
    @PostMapping("/{rfqId}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Result<RfqDto>> acceptQuote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID rfqId) {
        RfqDto rfq = quoteService.acceptQuote(principal.getId(), rfqId);
        return ResponseEntity.ok(Result.ok(rfq, "Quotation accepted. Proceeding to purchase order generation."));
    }

    @Operation(summary = "Get buyer RFQ history")
    @GetMapping("/my-quotes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResult<RfqDto>> getMyQuotes(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RfqDto> page = quoteService.getBuyerRfqs(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }

    @Operation(summary = "Get seller RFQ inquiries")
    @GetMapping("/seller/inquiries")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER')")
    public ResponseEntity<PagedResult<RfqDto>> getSellerInquiries(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RfqDto> page = quoteService.getSellerRfqs(principal.getId(), pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
