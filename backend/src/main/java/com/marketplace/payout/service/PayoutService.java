package com.marketplace.payout.service;

import com.marketplace.order.domain.PayoutStatus;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.payout.domain.PayoutBatchStatus;
import com.marketplace.payout.domain.SellerLedgerEntry;
import com.marketplace.payout.domain.SellerLedgerType;
import com.marketplace.payout.domain.SellerPayout;
import com.marketplace.payout.dto.RequestPayoutRequest;
import com.marketplace.payout.dto.SellerBalanceSummaryDto;
import com.marketplace.payout.dto.SellerLedgerDto;
import com.marketplace.payout.dto.SellerPayoutDto;
import com.marketplace.payout.repository.SellerLedgerEntryRepository;
import com.marketplace.payout.repository.SellerPayoutRepository;
import com.marketplace.seller.domain.Seller;
import com.marketplace.seller.domain.SellerBankAccount;
import com.marketplace.seller.repository.SellerBankAccountRepository;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final SellerLedgerEntryRepository ledgerRepository;
    private final SellerPayoutRepository payoutRepository;
    private final SellerBankAccountRepository bankAccountRepository;
    private final SellerRepository sellerRepository;
    private final SellerOrderRepository sellerOrderRepository;

    @Transactional(readOnly = true)
    public SellerBalanceSummaryDto getBalanceSummary(UUID sellerId) {
        BigDecimal availableBalance = ledgerRepository.computeCurrentBalance(sellerId);

        return SellerBalanceSummaryDto.builder()
                .sellerId(sellerId)
                .availableBalance(availableBalance != null ? availableBalance : BigDecimal.ZERO)
                .pendingEscrowBalance(BigDecimal.ZERO)
                .lifetimeEarnings(availableBalance != null ? availableBalance : BigDecimal.ZERO)
                .totalWithdrawn(BigDecimal.ZERO)
                .currency("USD")
                .build();
    }

    @Transactional
    public void releaseOrderEscrowToLedger(UUID sellerOrderId) {
        SellerOrder sellerOrder = sellerOrderRepository.findById(sellerOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", sellerOrderId));

        Seller seller = sellerOrder.getSeller();
        BigDecimal currentBal = ledgerRepository.computeCurrentBalance(seller.getId());
        BigDecimal newBal = currentBal.add(sellerOrder.getNetSellerPayable());

        SellerLedgerEntry entry = SellerLedgerEntry.builder()
                .seller(seller)
                .entryType(SellerLedgerType.ORDER_CREDIT)
                .amount(sellerOrder.getNetSellerPayable())
                .currency("USD")
                .runningBalance(newBal)
                .referenceType("SELLER_ORDER")
                .referenceId(sellerOrder.getSellerOrderNumber())
                .description("Earnings for fulfilled sub-order " + sellerOrder.getSellerOrderNumber() + " (net of commission)")
                .build();

        ledgerRepository.save(entry);

        sellerOrder.setPayoutStatus(PayoutStatus.PAID);
        sellerOrderRepository.save(sellerOrder);

        log.info("Released escrow earnings: [subOrder={}, seller={}, amount={}, newBal={}]",
                sellerOrder.getSellerOrderNumber(), seller.getId(), sellerOrder.getNetSellerPayable(), newBal);
    }

    @Transactional
    public SellerPayoutDto requestPayout(UUID sellerId, RequestPayoutRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        BigDecimal currentBalance = ledgerRepository.computeCurrentBalance(sellerId);
        if (currentBalance == null || currentBalance.compareTo(request.getAmount()) < 0) {
            throw new BusinessRuleException(ErrorCode.INSUFFICIENT_FUNDS,
                    "Insufficient available balance. Current: $" + currentBalance + ", Requested: $" + request.getAmount());
        }

        SellerBankAccount bankAccount;
        if (request.getBankAccountId() != null) {
            bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("SellerBankAccount", "id", request.getBankAccountId()));
        } else {
            bankAccount = bankAccountRepository.findBySellerId(sellerId).stream().findFirst()
                    .orElse(null);
        }

        String batchRef = "PAYOUT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SellerPayout payout = SellerPayout.builder()
                .payoutBatchReference(batchRef)
                .seller(seller)
                .bankAccount(bankAccount)
                .amount(request.getAmount())
                .currency("USD")
                .status(PayoutBatchStatus.COMPLETED)
                .gatewayPayoutId("po_mock_" + UUID.randomUUID().toString().substring(0, 12))
                .processedAt(Instant.now())
                .build();

        SellerPayout savedPayout = payoutRepository.save(payout);

        // Debit Ledger Entry
        BigDecimal newBal = currentBalance.subtract(request.getAmount());
        SellerLedgerEntry debitEntry = SellerLedgerEntry.builder()
                .seller(seller)
                .entryType(SellerLedgerType.PAYOUT_WITHDRAWAL)
                .amount(request.getAmount())
                .currency("USD")
                .runningBalance(newBal)
                .referenceType("PAYOUT_BATCH")
                .referenceId(batchRef)
                .description("Automated ACH Bank Payout Transfer: " + batchRef)
                .build();
        ledgerRepository.save(debitEntry);

        log.info("Processed seller payout withdrawal: [ref={}, seller={}, amount={}, newBal={}]",
                batchRef, seller.getId(), request.getAmount(), newBal);

        return toPayoutDto(savedPayout);
    }

    @Transactional(readOnly = true)
    public Page<SellerLedgerDto> getLedgerEntries(UUID sellerId, Pageable pageable) {
        return ledgerRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable).map(this::toLedgerDto);
    }

    @Transactional(readOnly = true)
    public Page<SellerPayoutDto> getPayoutHistory(UUID sellerId, Pageable pageable) {
        return payoutRepository.findBySellerIdOrderByCreatedAtDesc(sellerId, pageable).map(this::toPayoutDto);
    }

    private SellerLedgerDto toLedgerDto(SellerLedgerEntry e) {
        return SellerLedgerDto.builder()
                .id(e.getId())
                .entryType(e.getEntryType())
                .amount(e.getAmount())
                .currency(e.getCurrency())
                .runningBalance(e.getRunningBalance())
                .referenceType(e.getReferenceType())
                .referenceId(e.getReferenceId())
                .description(e.getDescription())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private SellerPayoutDto toPayoutDto(SellerPayout p) {
        return SellerPayoutDto.builder()
                .id(p.getId())
                .payoutBatchReference(p.getPayoutBatchReference())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .bankAccountLast4(p.getBankAccount() != null ? p.getBankAccount().getAccountNumberLast4() : "1234")
                .bankName(p.getBankAccount() != null ? p.getBankAccount().getBankName() : "Wells Fargo")
                .status(p.getStatus())
                .gatewayPayoutId(p.getGatewayPayoutId())
                .createdAt(p.getCreatedAt())
                .processedAt(p.getProcessedAt())
                .build();
    }
}
