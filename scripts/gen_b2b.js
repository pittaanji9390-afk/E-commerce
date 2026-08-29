const { write } = require('./generator_helper');

console.log('Generating B2B Wholesale Domain...');

// 1. Entities
write('backend/src/main/java/com/marketplace/b2b/domain/QuoteStatus.java', `
package com.marketplace.b2b.domain;

public enum QuoteStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    SELLER_PROPOSED,
    BUYER_COUNTERED,
    ACCEPTED,
    REJECTED,
    CONVERTED_TO_ORDER,
    EXPIRED
}
`);

write('backend/src/main/java/com/marketplace/b2b/domain/CreditTermType.java', `
package com.marketplace.b2b.domain;

public enum CreditTermType {
    PREPAID,
    NET_15,
    NET_30,
    NET_60,
    NET_90
}
`);

write('backend/src/main/java/com/marketplace/b2b/domain/BulkPriceTier.java', `
package com.marketplace.b2b.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bulk_price_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkPriceTier extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "min_quantity", nullable = false)
    private int minQuantity;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/b2b/domain/RequestForQuote.java', `
package com.marketplace.b2b.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfq_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestForQuote extends AuditableEntity {

    @Column(name = "rfq_number", nullable = false, unique = true, length = 50)
    private String rfqNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "tax_exemption_number", length = 100)
    private String taxExemptionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_terms", length = 30, nullable = false)
    @Builder.Default
    private CreditTermType creditTerms = CreditTermType.PREPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private QuoteStatus status = QuoteStatus.SUBMITTED;

    @Column(name = "target_price", precision = 15, scale = 2)
    private BigDecimal targetPrice;

    @Column(name = "quoted_total", precision = 15, scale = 2)
    private BigDecimal quotedTotal;

    @Column(name = "buyer_message", columnDefinition = "TEXT")
    private String buyerMessage;

    @Column(name = "seller_notes", columnDefinition = "TEXT")
    private String sellerNotes;

    @Column(name = "valid_until")
    private Instant validUntil;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RfqItem> items = new ArrayList<>();

    public void addItem(RfqItem item) {
        items.add(item);
        item.setRfq(this);
    }
}
`);

write('backend/src/main/java/com/marketplace/b2b/domain/RfqItem.java', `
package com.marketplace.b2b.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rfq_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RequestForQuote rfq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "requested_quantity", nullable = false)
    private int requestedQuantity;

    @Column(name = "target_unit_price", precision = 15, scale = 2)
    private BigDecimal targetUnitPrice;

    @Column(name = "offered_unit_price", precision = 15, scale = 2)
    private BigDecimal offeredUnitPrice;
}
`);

write('backend/src/main/java/com/marketplace/b2b/domain/B2BCreditAccount.java', `
package com.marketplace.b2b.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "b2b_credit_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class B2BCreditAccount extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    @Column(name = "credit_limit", precision = 15, scale = 2, nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "available_credit", precision = 15, scale = 2, nullable = false)
    private BigDecimal availableCredit;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_terms", length = 30, nullable = false)
    @Builder.Default
    private CreditTermType defaultTerms = CreditTermType.NET_30;

    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private boolean approved = false;
}
`);

// Repositories
write('backend/src/main/java/com/marketplace/b2b/repository/BulkPriceTierRepository.java', `
package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.BulkPriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BulkPriceTierRepository extends JpaRepository<BulkPriceTier, UUID> {
    List<BulkPriceTier> findByVariantIdOrderByMinQuantityAsc(UUID variantId);
}
`);

write('backend/src/main/java/com/marketplace/b2b/repository/RequestForQuoteRepository.java', `
package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.QuoteStatus;
import com.marketplace.b2b.domain.RequestForQuote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestForQuoteRepository extends JpaRepository<RequestForQuote, UUID> {
    Optional<RequestForQuote> findByRfqNumber(String rfqNumber);
    Page<RequestForQuote> findByBuyerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    Page<RequestForQuote> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);
    Page<RequestForQuote> findByStatus(QuoteStatus status, Pageable pageable);
}
`);

write('backend/src/main/java/com/marketplace/b2b/repository/B2BCreditAccountRepository.java', `
package com.marketplace.b2b.repository;

import com.marketplace.b2b.domain.B2BCreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface B2BCreditAccountRepository extends JpaRepository<B2BCreditAccount, UUID> {
}
`);

// DTOs
write('backend/src/main/java/com/marketplace/b2b/dto/RfqDto.java', `
package com.marketplace.b2b.dto;

import com.marketplace.b2b.domain.CreditTermType;
import com.marketplace.b2b.domain.QuoteStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqDto {
    private UUID id;
    private String rfqNumber;
    private UUID buyerId;
    private String buyerEmail;
    private UUID sellerId;
    private String sellerName;
    private String companyName;
    private String taxExemptionNumber;
    private CreditTermType creditTerms;
    private QuoteStatus status;
    private BigDecimal targetPrice;
    private BigDecimal quotedTotal;
    private String buyerMessage;
    private String sellerNotes;
    private Instant validUntil;
    private List<RfqItemDto> items;
    private Instant createdAt;
}
`);

write('backend/src/main/java/com/marketplace/b2b/dto/RfqItemDto.java', `
package com.marketplace.b2b.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqItemDto {
    private UUID id;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int requestedQuantity;
    private BigDecimal targetUnitPrice;
    private BigDecimal offeredUnitPrice;
}
`);

write('backend/src/main/java/com/marketplace/b2b/dto/CreateRfqRequest.java', `
package com.marketplace.b2b.dto;

import com.marketplace.b2b.domain.CreditTermType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRfqRequest {

    @NotNull(message = "Seller ID is required")
    private UUID sellerId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String taxExemptionNumber;

    @Builder.Default
    private CreditTermType creditTerms = CreditTermType.PREPAID;

    private BigDecimal targetPrice;

    private String buyerMessage;

    @NotEmpty(message = "RFQ items cannot be empty")
    private List<RfqItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RfqItemRequest {
        @NotNull
        private UUID variantId;
        @NotNull
        private Integer requestedQuantity;
        private BigDecimal targetUnitPrice;
    }
}
`);

write('backend/src/main/java/com/marketplace/b2b/dto/SellerQuoteProposalRequest.java', `
package com.marketplace.b2b.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerQuoteProposalRequest {

    @NotEmpty(message = "Item prices must be provided")
    private Map<UUID, BigDecimal> offeredUnitPrices;

    @NotNull(message = "Total quotation is required")
    private BigDecimal quotedTotal;

    private String sellerNotes;

    @NotNull(message = "Validity period is required")
    private Instant validUntil;
}
`);

// Services
write('backend/src/main/java/com/marketplace/b2b/service/B2BQuoteService.java', `
package com.marketplace.b2b.service;

import com.marketplace.b2b.domain.*;
import com.marketplace.b2b.dto.*;
import com.marketplace.b2b.repository.B2BCreditAccountRepository;
import com.marketplace.b2b.repository.RequestForQuoteRepository;
import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.repository.SellerRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class B2BQuoteService {

    private final RequestForQuoteRepository rfqRepository;
    private final ProductVariantRepository variantRepository;
    private final SellerRepository sellerRepository;
    private final CustomerService customerService;
    private final B2BCreditAccountRepository creditAccountRepository;

    @Transactional
    public RfqDto submitRfq(UUID buyerCustomerId, CreateRfqRequest request) {
        Customer buyer = customerService.getOrCreateCustomer(buyerCustomerId);
        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", request.getSellerId()));

        String rfqNum = "RFQ-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        RequestForQuote rfq = RequestForQuote.builder()
                .rfqNumber(rfqNum)
                .buyer(buyer)
                .seller(seller)
                .companyName(request.getCompanyName().trim())
                .taxExemptionNumber(request.getTaxExemptionNumber())
                .creditTerms(request.getCreditTerms())
                .status(QuoteStatus.SUBMITTED)
                .targetPrice(request.getTargetPrice())
                .buyerMessage(request.getBuyerMessage())
                .validUntil(Instant.now().plus(14, ChronoUnit.DAYS))
                .build();

        for (CreateRfqRequest.RfqItemRequest itemReq : request.getItems()) {
            ProductVariant variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", itemReq.getVariantId()));

            RfqItem item = RfqItem.builder()
                    .variant(variant)
                    .requestedQuantity(itemReq.getRequestedQuantity())
                    .targetUnitPrice(itemReq.getTargetUnitPrice())
                    .build();
            rfq.addItem(item);
        }

        RequestForQuote saved = rfqRepository.save(rfq);
        log.info("B2B RFQ submitted [number={}, buyer={}, seller={}]", rfqNum, buyerCustomerId, seller.getDisplayName());
        return toDto(saved);
    }

    @Transactional
    public RfqDto submitSellerProposal(UUID sellerId, UUID rfqId, SellerQuoteProposalRequest request) {
        RequestForQuote rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RequestForQuote", "id", rfqId));

        if (!rfq.getSeller().getId().equals(sellerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You do not own this RFQ negotiation.");
        }

        rfq.setQuotedTotal(request.getQuotedTotal());
        rfq.setSellerNotes(request.getSellerNotes());
        rfq.setValidUntil(request.getValidUntil());
        rfq.setStatus(QuoteStatus.SELLER_PROPOSED);

        for (RfqItem item : rfq.getItems()) {
            BigDecimal offered = request.getOfferedUnitPrices().get(item.getVariant().getId());
            if (offered != null) {
                item.setOfferedUnitPrice(offered);
            }
        }

        RequestForQuote saved = rfqRepository.save(rfq);
        log.info("Seller proposal submitted for RFQ {}", rfq.getRfqNumber());
        return toDto(saved);
    }

    @Transactional
    public RfqDto acceptQuote(UUID buyerCustomerId, UUID rfqId) {
        RequestForQuote rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RequestForQuote", "id", rfqId));

        if (!rfq.getBuyer().getId().equals(buyerCustomerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You can only accept your own quotes.");
        }

        if (rfq.getStatus() != QuoteStatus.SELLER_PROPOSED) {
            throw new BusinessRuleException(ErrorCode.BUSINESS_RULE_VIOLATION, "Quote is not in actionable proposed state.");
        }

        rfq.setStatus(QuoteStatus.ACCEPTED);
        RequestForQuote saved = rfqRepository.save(rfq);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<RfqDto> getBuyerRfqs(UUID customerId, Pageable pageable) {
        return rfqRepository.findByBuyerIdOrderByCreatedAtDesc(customerId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<RfqDto> getSellerRfqs(UUID sellerId, Pageable pageable) {
        return rfqRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable).map(this::toDto);
    }

    private RfqDto toDto(RequestForQuote r) {
        List<RfqItemDto> itemDtos = r.getItems().stream()
                .map(it -> RfqItemDto.builder()
                        .id(it.getId())
                        .variantId(it.getVariant().getId())
                        .variantSku(it.getVariant().getSku())
                        .productTitle(it.getVariant().getProduct().getTitle())
                        .requestedQuantity(it.getRequestedQuantity())
                        .targetUnitPrice(it.getTargetUnitPrice())
                        .offeredUnitPrice(it.getOfferedUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return RfqDto.builder()
                .id(r.getId())
                .rfqNumber(r.getRfqNumber())
                .buyerId(r.getBuyer().getId())
                .buyerEmail(r.getBuyer().getUser().getEmail())
                .sellerId(r.getSeller().getId())
                .sellerName(r.getSeller().getDisplayName())
                .companyName(r.getCompanyName())
                .taxExemptionNumber(r.getTaxExemptionNumber())
                .creditTerms(r.getCreditTerms())
                .status(r.getStatus())
                .targetPrice(r.getTargetPrice())
                .quotedTotal(r.getQuotedTotal())
                .buyerMessage(r.getBuyerMessage())
                .sellerNotes(r.getSellerNotes())
                .validUntil(r.getValidUntil())
                .items(itemDtos)
                .createdAt(r.getCreatedAt())
                .build();
    }
}
`);

// Controller
write('backend/src/main/java/com/marketplace/b2b/controller/B2BQuoteController.java', `
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
`);

// Frontend B2B Portal Page
write('frontend/src/features/b2b/WholesaleQuotationPage.tsx', `
import React, { useState } from 'react';
import { Building2, FileText, CheckCircle2, Clock, DollarSign, Send, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { PriceDisplay } from '@/components/ui/PriceDisplay';
import { Modal } from '@/components/ui/Modal';

interface MockRfq {
  id: string;
  rfqNumber: string;
  sellerName: string;
  companyName: string;
  itemsCount: number;
  targetTotal: number;
  quotedTotal?: number;
  creditTerms: string;
  status: 'SUBMITTED' | 'SELLER_PROPOSED' | 'ACCEPTED';
  date: string;
}

const mockRfqs: MockRfq[] = [
  {
    id: 'rfq-1',
    rfqNumber: 'RFQ-1724982103-842',
    sellerName: 'Apex Innovations LLC',
    companyName: 'Nordic Audio Systems Corp',
    itemsCount: 500,
    targetTotal: 125000.00,
    quotedTotal: 132500.00,
    creditTerms: 'NET_30',
    status: 'SELLER_PROPOSED',
    date: 'Aug 26, 2026',
  },
  {
    id: 'rfq-2',
    rfqNumber: 'RFQ-1724810294-119',
    sellerName: 'Apex Innovations LLC',
    companyName: 'Pacific Rim Retail Group',
    itemsCount: 200,
    targetTotal: 50000.00,
    quotedTotal: 50000.00,
    creditTerms: 'NET_60',
    status: 'ACCEPTED',
    date: 'Aug 14, 2026',
  },
];

export const WholesaleQuotationPage: React.FC = () => {
  const [rfqs, setRfqs] = useState<MockRfq[]>(mockRfqs);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [companyName, setCompanyName] = useState('Cyberdyne Systems B2B');
  const [taxId, setTaxId] = useState('US-TAX-894102');
  const [targetPrice, setTargetPrice] = useState('45000');
  const [notes, setNotes] = useState('Looking for 100 units bulk delivery for enterprise client deployment.');
  const [submittedSuccess, setSubmittedSuccess] = useState(false);

  const handleSubmitRfq = (e: React.FormEvent) => {
    e.preventDefault();
    setSubmittedSuccess(true);
    setTimeout(() => {
      setRfqs((prev) => [
        {
          id: \`rfq-\${Date.now()}\`,
          rfqNumber: \`RFQ-\${Date.now().toString().substring(6)}-B2B\`,
          sellerName: 'Apex Innovations LLC',
          companyName: companyName,
          itemsCount: 100,
          targetTotal: parseFloat(targetPrice) || 0,
          creditTerms: 'NET_30',
          status: 'SUBMITTED',
          date: 'Just now',
        },
        ...prev,
      ]);
      setSubmittedSuccess(false);
      setCreateModalOpen(false);
    }, 1500);
  };

  const handleAccept = (id: string) => {
    setRfqs((prev) =>
      prev.map((r) => (r.id === id ? { ...r, status: 'ACCEPTED' as const } : r))
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Building2 className="w-6 h-6 text-primary-600" /> B2B Wholesale & RFQ Quotations
          </h1>
          <p className="text-gray-500 text-sm mt-1">Direct corporate volume pricing negotiation, Net credit terms, and purchase orders.</p>
        </div>
        <Button onClick={() => setCreateModalOpen(true)}>
          <FileText className="w-4 h-4 mr-2" /> Request Wholesale Quote
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <span className="text-xs uppercase font-semibold text-gray-500">Corporate Credit Limit</span>
          <div className="text-3xl font-black text-gray-900 mt-2">
            <PriceDisplay amount={250000.00} />
          </div>
          <Badge variant="success" className="mt-2">Net-30 Terms Approved</Badge>
        </div>
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <span className="text-xs uppercase font-semibold text-gray-500">Active RFQ Negotiations</span>
          <div className="text-3xl font-black text-primary-600 mt-2">
            {rfqs.filter(r => r.status !== 'ACCEPTED').length} Active
          </div>
          <span className="text-xs text-gray-400 mt-2 block">Direct seller negotiation channel</span>
        </div>
        <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
          <span className="text-xs uppercase font-semibold text-gray-500">Tax Exemption Status</span>
          <div className="text-lg font-bold text-gray-900 mt-2">
            Resale Certificate Verified
          </div>
          <span className="text-xs text-emerald-600 font-semibold mt-2 block">0% Sales Tax Applied</span>
        </div>
      </div>

      <div className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <h3 className="text-base font-bold text-gray-900 mb-6 flex items-center gap-2">
          <Clock className="w-5 h-5 text-gray-500" /> Quotation Negotiations History
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 font-semibold">RFQ Number</th>
                <th className="px-4 py-3 font-semibold">Merchant</th>
                <th className="px-4 py-3 font-semibold">Terms</th>
                <th className="px-4 py-3 font-semibold text-center">Units</th>
                <th className="px-4 py-3 font-semibold">Quoted Total</th>
                <th className="px-4 py-3 font-semibold">Status</th>
                <th className="px-4 py-3 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {rfqs.map((rfq) => (
                <tr key={rfq.id} className="hover:bg-gray-50/80">
                  <td className="px-4 py-3.5 font-mono text-xs font-semibold text-gray-900">{rfq.rfqNumber}</td>
                  <td className="px-4 py-3.5 font-medium text-gray-800">{rfq.sellerName}</td>
                  <td className="px-4 py-3.5"><Badge variant="info">{rfq.creditTerms}</Badge></td>
                  <td className="px-4 py-3.5 text-center font-semibold text-gray-700">{rfq.itemsCount} units</td>
                  <td className="px-4 py-3.5 font-bold text-gray-900">
                    {rfq.quotedTotal ? <PriceDisplay amount={rfq.quotedTotal} /> : <span className="text-gray-400 text-xs">Pending Proposal</span>}
                  </td>
                  <td className="px-4 py-3.5">
                    {rfq.status === 'ACCEPTED' && <Badge variant="success">Accepted & PO Created</Badge>}
                    {rfq.status === 'SELLER_PROPOSED' && <Badge variant="warning">Proposal Ready</Badge>}
                    {rfq.status === 'SUBMITTED' && <Badge variant="neutral">Under Review</Badge>}
                  </td>
                  <td className="px-4 py-3.5 text-right">
                    {rfq.status === 'SELLER_PROPOSED' && (
                      <Button size="sm" onClick={() => handleAccept(rfq.id)}>
                        Accept Proposal <ArrowRight className="w-3.5 h-3.5 ml-1" />
                      </Button>
                    )}
                    {rfq.status === 'ACCEPTED' && (
                      <Button size="sm" variant="secondary">
                        View Purchase Order
                      </Button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* New RFQ Modal */}
      <Modal isOpen={createModalOpen} onClose={() => setCreateModalOpen(false)} title="Submit Wholesale Request For Quote (RFQ)">
        {submittedSuccess ? (
          <div className="text-center py-6">
            <CheckCircle2 className="w-12 h-12 text-green-500 mx-auto mb-3" />
            <h3 className="font-bold text-gray-900">RFQ Dispatched to Vendor</h3>
            <p className="text-sm text-gray-500 mt-1">The merchant will review wholesale volume tiers and submit a formalized contract.</p>
          </div>
        ) : (
          <form onSubmit={handleSubmitRfq} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Company / Entity Name</label>
              <Input value={companyName} onChange={(e) => setCompanyName(e.target.value)} required />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Tax Exemption / Resale ID</label>
                <Input value={taxId} onChange={(e) => setTaxId(e.target.value)} required />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Target Budget ($ USD)</label>
                <Input type="number" value={targetPrice} onChange={(e) => setTargetPrice(e.target.value)} required />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-gray-500 mb-1">Volume Requirements & Specifications</label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                required
                rows={3}
                className="w-full text-sm border-gray-300 rounded-lg p-2.5 border focus:ring-primary-500 focus:border-primary-500"
              />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" variant="secondary" onClick={() => setCreateModalOpen(false)}>Cancel</Button>
              <Button type="submit"><Send className="w-4 h-4 mr-1.5" /> Submit RFQ</Button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
};
`);

console.log('B2B Wholesale Domain Generated Successfully.');
`);
