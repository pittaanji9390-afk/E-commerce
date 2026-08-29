package com.marketplace.inventory.service;

import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.inventory.domain.Inventory;
import com.marketplace.inventory.domain.InventoryTransaction;
import com.marketplace.inventory.domain.InventoryTransactionType;
import com.marketplace.inventory.dto.AdjustInventoryRequest;
import com.marketplace.inventory.dto.InventoryDto;
import com.marketplace.inventory.dto.RestockRequest;
import com.marketplace.inventory.repository.InventoryRepository;
import com.marketplace.inventory.repository.InventoryTransactionRepository;
import com.marketplace.product.domain.ProductVariant;
import com.marketplace.product.repository.ProductVariantRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public InventoryDto getInventoryByVariantId(UUID variantId) {
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", variantId));
        return toDto(inventory);
    }

    /**
     * Concurrency-safe stock reservation using Pessimistic Write Lock.
     * Guarantees zero overselling even under massive concurrent checkouts.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void reserveStock(UUID variantId, int quantity, String orderReference) {
        Inventory inventory = inventoryRepository.findByVariantIdWithLock(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", variantId));

        if (!inventory.canReserve(quantity)) {
            log.warn("Oversell prevented: variant={}, requested={}, available={}",
                    variantId, quantity, inventory.getAvailable());
            throw new BusinessRuleException(ErrorCode.INSUFFICIENT_INVENTORY,
                    "Insufficient inventory for variant " + inventory.getVariant().getSku() + ". Available: " + inventory.getAvailable());
        }

        int prevReserved = inventory.getReserved();
        inventory.reserve(quantity);
        inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(InventoryTransactionType.RESERVATION)
                .quantity(quantity)
                .previousOnHand(inventory.getOnHand())
                .newOnHand(inventory.getOnHand())
                .previousReserved(prevReserved)
                .newReserved(inventory.getReserved())
                .referenceId(orderReference)
                .reason("Checkout stock reservation for order " + orderReference)
                .build();
        transactionRepository.save(tx);

        log.info("Reserved stock: [variant={}, qty={}, order={}, remainingAvailable={}]",
                variantId, quantity, orderReference, inventory.getAvailable());
    }

    /**
     * Releases reserved stock back to available pool if checkout expires or payment fails.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void releaseReservation(UUID variantId, int quantity, String orderReference) {
        Inventory inventory = inventoryRepository.findByVariantIdWithLock(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", variantId));

        int prevReserved = inventory.getReserved();
        inventory.releaseReservation(quantity);
        inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(InventoryTransactionType.RELEASE)
                .quantity(quantity)
                .previousOnHand(inventory.getOnHand())
                .newOnHand(inventory.getOnHand())
                .previousReserved(prevReserved)
                .newReserved(inventory.getReserved())
                .referenceId(orderReference)
                .reason("Released reservation for order " + orderReference)
                .build();
        transactionRepository.save(tx);

        log.info("Released reservation: [variant={}, qty={}, order={}]", variantId, quantity, orderReference);
    }

    /**
     * Converts reservation into a permanent sale upon verified payment confirmation.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void commitSale(UUID variantId, int quantity, String orderReference) {
        Inventory inventory = inventoryRepository.findByVariantIdWithLock(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", variantId));

        int prevOnHand = inventory.getOnHand();
        int prevReserved = inventory.getReserved();
        inventory.commitSale(quantity);
        inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(inventory)
                .transactionType(InventoryTransactionType.PURCHASE)
                .quantity(quantity)
                .previousOnHand(prevOnHand)
                .newOnHand(inventory.getOnHand())
                .previousReserved(prevReserved)
                .newReserved(inventory.getReserved())
                .referenceId(orderReference)
                .reason("Settled sale for paid order " + orderReference)
                .build();
        transactionRepository.save(tx);

        log.info("Committed sale: [variant={}, qty={}, order={}]", variantId, quantity, orderReference);
    }

    @Transactional
    public InventoryDto restock(UUID actorId, RestockRequest request) {
        Inventory inventory = inventoryRepository.findByVariantIdWithLock(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", request.getVariantId()));

        User actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;

        int prevOnHand = inventory.getOnHand();
        inventory.restock(request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(saved)
                .transactionType(InventoryTransactionType.RESTOCK)
                .quantity(request.getQuantity())
                .previousOnHand(prevOnHand)
                .newOnHand(saved.getOnHand())
                .previousReserved(saved.getReserved())
                .newReserved(saved.getReserved())
                .reason(request.getReason() != null ? request.getReason() : "Merchant warehouse restock")
                .actor(actor)
                .build();
        transactionRepository.save(tx);

        log.info("Restocked: [variant={}, addedQty={}, newOnHand={}]", request.getVariantId(), request.getQuantity(), saved.getOnHand());
        return toDto(saved);
    }

    @Transactional
    public InventoryDto adjustInventory(UUID actorId, AdjustInventoryRequest request) {
        Inventory inventory = inventoryRepository.findByVariantIdWithLock(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "variantId", request.getVariantId()));

        User actor = actorId != null ? userRepository.findById(actorId).orElse(null) : null;

        int prevOnHand = inventory.getOnHand();
        int delta = request.getNewOnHand() - prevOnHand;
        inventory.setOnHand(request.getNewOnHand());
        Inventory saved = inventoryRepository.save(inventory);

        InventoryTransaction tx = InventoryTransaction.builder()
                .inventory(saved)
                .transactionType(request.getType())
                .quantity(delta)
                .previousOnHand(prevOnHand)
                .newOnHand(saved.getOnHand())
                .previousReserved(saved.getReserved())
                .newReserved(saved.getReserved())
                .reason(request.getReason() != null ? request.getReason() : "Manual inventory adjustment: " + request.getType())
                .actor(actor)
                .build();
        transactionRepository.save(tx);

        log.info("Adjusted inventory: [variant={}, prev={}, new={}]", request.getVariantId(), prevOnHand, saved.getOnHand());
        return toDto(saved);
    }

    private InventoryDto toDto(Inventory inv) {
        ProductVariant variant = inv.getVariant();
        return InventoryDto.builder()
                .id(inv.getId())
                .variantId(variant.getId())
                .variantSku(variant.getSku())
                .variantTitle(variant.getTitle())
                .productTitle(variant.getProduct().getTitle())
                .onHand(inv.getOnHand())
                .reserved(inv.getReserved())
                .available(inv.getAvailable())
                .lowStockThreshold(inv.getLowStockThreshold())
                .lowStock(inv.getAvailable() <= inv.getLowStockThreshold())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}
