package com.marketplace.wms.controller;

import com.marketplace.shared.response.PagedResult;
import com.marketplace.shared.response.Result;
import com.marketplace.wms.dto.PickListDto;
import com.marketplace.wms.dto.WarehouseDto;
import com.marketplace.wms.service.WarehouseManagementService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Warehouse Management & Multi-Location Fulfillment", description = "Endpoints for warehouse facilities, pick-pack-ship lists, and bin allocations")
@RestController
@RequestMapping("/api/v1/wms")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseManagementService wmsService;

    @Operation(summary = "Get list of all operational fulfillment centers")
    @GetMapping("/warehouses")
    public ResponseEntity<Result<List<WarehouseDto>>> getWarehouses() {
        List<WarehouseDto> list = wmsService.getAllWarehouses();
        return ResponseEntity.ok(Result.ok(list));
    }

    @Operation(summary = "Register a new regional warehouse facility (Admin)")
    @PostMapping("/warehouses")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Result<WarehouseDto>> createWarehouse(@Valid @RequestBody WarehouseDto dto) {
        WarehouseDto created = wmsService.createWarehouse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(created, "Warehouse facility registered."));
    }

    @Operation(summary = "Generate pick list for fulfillment warehouse (Seller / Logistics)")
    @PostMapping("/warehouses/{warehouseId}/pick-lists")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'LOGISTICS_AGENT')")
    public ResponseEntity<Result<PickListDto>> generatePickList(
            @PathVariable UUID warehouseId,
            @RequestParam UUID sellerOrderId) {
        PickListDto pickList = wmsService.generatePickList(warehouseId, sellerOrderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Result.ok(pickList, "Pick list generated."));
    }

    @Operation(summary = "Verify barcode scan and picked quantities")
    @PatchMapping("/pick-lists/{pickListId}/items/{itemId}/verify")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'LOGISTICS_AGENT')")
    public ResponseEntity<Result<PickListDto>> verifyPick(
            @PathVariable UUID pickListId,
            @PathVariable UUID itemId,
            @RequestParam int quantityPicked) {
        PickListDto updated = wmsService.verifyPickedItem(pickListId, itemId, quantityPicked);
        return ResponseEntity.ok(Result.ok(updated, "Item pick verified."));
    }

    @Operation(summary = "Get pick lists for warehouse")
    @GetMapping("/warehouses/{warehouseId}/pick-lists")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'LOGISTICS_AGENT')")
    public ResponseEntity<PagedResult<PickListDto>> getPickLists(
            @PathVariable UUID warehouseId,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PickListDto> page = wmsService.getWarehousePickLists(warehouseId, pageable);
        return ResponseEntity.ok(PagedResult.of(page));
    }
}
