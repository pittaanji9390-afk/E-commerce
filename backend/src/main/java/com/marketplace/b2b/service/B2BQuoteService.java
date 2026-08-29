package com.marketplace.b2b.service;

import com.marketplace.b2b.domain.*;
import com.marketplace.b2b.dto.*;
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
