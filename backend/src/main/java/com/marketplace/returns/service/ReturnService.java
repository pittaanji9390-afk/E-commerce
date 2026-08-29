package com.marketplace.returns.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.service.CustomerService;
import com.marketplace.order.domain.OrderItem;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.repository.OrderItemRepository;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.refund.service.RefundService;
import com.marketplace.returns.domain.*;
import com.marketplace.returns.dto.*;
import com.marketplace.returns.repository.DisputeRepository;
import com.marketplace.returns.repository.ReturnItemRepository;
import com.marketplace.returns.repository.ReturnRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final ReturnItemRepository returnItemRepository;
    private final DisputeRepository disputeRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerService customerService;
    private final RefundService refundService;

    @Transactional
    public ReturnDto createReturnRequest(UUID customerId, CreateReturnRequest request) {
        Customer customer = customerService.getOrCreateCustomer(customerId);

        SellerOrder sellerOrder = sellerOrderRepository.findById(request.getSellerOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", request.getSellerOrderId()));

        if (!sellerOrder.getParentOrder().getCustomer().getId().equals(customerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You can only request returns for your own orders.");
        }

        String returnNumber = "RMA-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Return returnRequest = Return.builder()
                .returnNumber(returnNumber)
                .sellerOrder(sellerOrder)
                .customer(customer)
                .reason(request.getReason())
                .customerNotes(request.getCustomerNotes())
                .evidenceUrls(request.getEvidenceUrls())
                .status(ReturnStatus.REQUESTED)
                .refundAmount(BigDecimal.ZERO)
                .build();

        BigDecimal calculatedRefund = BigDecimal.ZERO;
        for (CreateReturnRequest.ReturnItemRequest itemReq : request.getItems()) {
            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemReq.getOrderItemId()));

            if (itemReq.getQuantity() > orderItem.getQuantity()) {
                throw new BusinessRuleException(ErrorCode.VALIDATION_ERROR, "Return quantity cannot exceed purchased quantity.");
            }

            BigDecimal itemTotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            calculatedRefund = calculatedRefund.add(itemTotal);

            ReturnItem returnItem = ReturnItem.builder()
                    .orderItem(orderItem)
                    .quantity(itemReq.getQuantity())
                    .build();
            returnRequest.addItem(returnItem);
        }

        returnRequest.setRefundAmount(calculatedRefund);
        Return saved = returnRepository.save(returnRequest);

        log.info("RMA Return request created: [returnNumber={}, subOrder={}, refundAmount={}]",
                returnNumber, sellerOrder.getSellerOrderNumber(), calculatedRefund);

        return toDto(saved);
    }

    @Transactional
    public ReturnDto reviewReturnRequest(UUID sellerId, UUID returnId, ReviewReturnRequest request) {
        Return returnRequest = returnRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return", "id", returnId));

        if (sellerId != null && !returnRequest.getSellerOrder().getSeller().getId().equals(sellerId)) {
            throw new BusinessRuleException(ErrorCode.FORBIDDEN, "You do not have permission to review this return request.");
        }

        returnRequest.setStatus(request.getStatus());
        if (request.getResponseNotes() != null) {
            returnRequest.setSellerResponseNotes(request.getResponseNotes());
        }
        if (request.getRefundAmount() != null) {
            returnRequest.setRefundAmount(request.getRefundAmount());
        }

        if (request.getStatus() == ReturnStatus.REFUNDED) {
            // Trigger actual financial refund
            refundService.processSellerOrderRefund(
                    returnRequest.getSellerOrder().getId(),
                    returnRequest.getRefundAmount(),
                    "RMA Return Approved: " + returnRequest.getReturnNumber(),
                    sellerId
            );
        }

        Return saved = returnRepository.save(returnRequest);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReturnDto> getCustomerReturns(UUID customerId, Pageable pageable) {
        return returnRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReturnDto> getSellerReturns(UUID sellerId, Pageable pageable) {
        return returnRepository.findBySellerOrderSellerIdOrderByCreatedAtDesc(sellerId, pageable).map(this::toDto);
    }

    @Transactional
    public DisputeDto createDispute(UUID customerId, CreateDisputeRequest request) {
        Customer customer = customerService.getOrCreateCustomer(customerId);

        SellerOrder sellerOrder = sellerOrderRepository.findById(request.getSellerOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", request.getSellerOrderId()));

        Return returnReq = null;
        if (request.getReturnId() != null) {
            returnReq = returnRepository.findById(request.getReturnId()).orElse(null);
        }

        String disputeNumber = "DSP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Dispute dispute = Dispute.builder()
                .disputeNumber(disputeNumber)
                .sellerOrder(sellerOrder)
                .returnRequest(returnReq)
                .customer(customer)
                .seller(sellerOrder.getSeller())
                .reason(request.getReason())
                .description(request.getDescription())
                .evidenceUrls(request.getEvidenceUrls())
                .status(DisputeStatus.OPEN)
                .build();

        Dispute saved = disputeRepository.save(dispute);
        log.info("Dispute opened: [disputeNumber={}, subOrder={}, customer={}]", disputeNumber, sellerOrder.getSellerOrderNumber(), customerId);
        return toDisputeDto(saved);
    }

    private ReturnDto toDto(Return r) {
        List<ReturnItemDto> itemDtos = r.getItems().stream()
                .map(it -> ReturnItemDto.builder()
                        .id(it.getId())
                        .orderItemId(it.getOrderItem().getId())
                        .productTitle(it.getOrderItem().getProductTitleSnapshot())
                        .variantTitle(it.getOrderItem().getVariantTitleSnapshot())
                        .sku(it.getOrderItem().getSkuSnapshot())
                        .unitPrice(it.getOrderItem().getUnitPrice())
                        .quantity(it.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return ReturnDto.builder()
                .id(r.getId())
                .returnNumber(r.getReturnNumber())
                .sellerOrderId(r.getSellerOrder().getId())
                .sellerOrderNumber(r.getSellerOrder().getSellerOrderNumber())
                .sellerId(r.getSellerOrder().getSeller().getId())
                .sellerName(r.getSellerOrder().getSeller().getDisplayName())
                .customerId(r.getCustomer().getId())
                .reason(r.getReason())
                .customerNotes(r.getCustomerNotes())
                .evidenceUrls(r.getEvidenceUrls())
                .status(r.getStatus())
                .sellerResponseNotes(r.getSellerResponseNotes())
                .refundAmount(r.getRefundAmount())
                .items(itemDtos)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private DisputeDto toDisputeDto(Dispute d) {
        return DisputeDto.builder()
                .id(d.getId())
                .disputeNumber(d.getDisputeNumber())
                .sellerOrderId(d.getSellerOrder().getId())
                .returnId(d.getReturnRequest() != null ? d.getReturnRequest().getId() : null)
                .customerId(d.getCustomer().getId())
                .sellerId(d.getSeller().getId())
                .sellerName(d.getSeller().getDisplayName())
                .reason(d.getReason())
                .description(d.getDescription())
                .evidenceUrls(d.getEvidenceUrls())
                .status(d.getStatus())
                .resolutionNotes(d.getResolutionNotes())
                .createdAt(d.getCreatedAt())
                .resolvedAt(d.getResolvedAt())
                .build();
    }
}
