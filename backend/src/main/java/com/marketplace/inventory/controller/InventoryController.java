package com.marketplace.inventory.controller;

import com.marketplace.inventory.dto.AdjustInventoryRequest;
import com.marketplace.inventory.dto.InventoryDto;
import com.marketplace.inventory.dto.RestockRequest;
import com.marketplace.inventory.service.InventoryService;
import com.marketplace.security.UserPrincipal;
import com.marketplace.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Inventory Management", description = "Endpoints for real-time stock balances, restock batches, and adjustments")
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get real-time stock balance for variant")
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<Result<InventoryDto>> getInventory(@PathVariable UUID variantId) {
        InventoryDto dto = inventoryService.getInventoryByVariantId(variantId);
        return ResponseEntity.ok(Result.ok(dto));
    }

    @Operation(summary = "Restock variant inventory (Seller / Inventory Manager)")
    @PostMapping("/restock")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<InventoryDto>> restock(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RestockRequest request) {
        InventoryDto updated = inventoryService.restock(principal != null ? principal.getId() : null, request);
        return ResponseEntity.ok(Result.ok(updated, "Inventory restocked successfully."));
    }

    @Operation(summary = "Manual inventory adjustment (Damage, loss, correction)")
    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('SELLER', 'SELLER_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<InventoryDto>> adjustInventory(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AdjustInventoryRequest request) {
        InventoryDto updated = inventoryService.adjustInventory(principal != null ? principal.getId() : null, request);
        return ResponseEntity.ok(Result.ok(updated, "Inventory adjusted successfully."));
    }
}
