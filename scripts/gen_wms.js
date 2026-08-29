const { write } = require('./generator_helper');

console.log('Generating WMS Multi-Warehouse Fulfillment Domain...');

// Entities
write('backend/src/main/java/com/marketplace/wms/domain/WarehouseType.java', `
package com.marketplace.wms.domain;

public enum WarehouseType {
    REGIONAL_FULFILLMENT_CENTER,
    CROSS_DOCK_HUB,
    LOCAL_DISPATCH_DEPOT,
    SELLER_OWNED_FACILITY
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/PickListStatus.java', `
package com.marketplace.wms.domain;

public enum PickListStatus {
    GENERATED,
    PICKING_IN_PROGRESS,
    PICKED,
    PACKING,
    DISPATCHED,
    CANCELLED
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/TransferStatus.java', `
package com.marketplace.wms.domain;

public enum TransferStatus {
    REQUESTED,
    IN_TRANSIT,
    RECEIVED,
    RECONCILED,
    CANCELLED
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/Warehouse.java', `
package com.marketplace.wms.domain;

import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends AuditableEntity {

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", length = 50, nullable = false)
    private WarehouseType warehouseType;

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state_province", nullable = false, length = 100)
    private String stateProvince;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WarehouseBin> bins = new ArrayList<>();
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/WarehouseBin.java', `
package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "warehouse_bins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseBin extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "zone_code", nullable = false, length = 20)
    private String zoneCode;

    @Column(name = "aisle", nullable = false, length = 20)
    private String aisle;

    @Column(name = "shelf", nullable = false, length = 20)
    private String shelf;

    @Column(name = "bin_code", nullable = false, unique = true, length = 50)
    private String binCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant assignedVariant;

    @Column(name = "quantity_on_hand", nullable = false)
    @Builder.Default
    private int quantityOnHand = 0;

    @Column(name = "max_capacity", nullable = false)
    @Builder.Default
    private int maxCapacity = 500;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/PickList.java', `
package com.marketplace.wms.domain;

import com.marketplace.order.domain.SellerOrder;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pick_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickList extends AuditableEntity {

    @Column(name = "pick_list_number", nullable = false, unique = true, length = 50)
    private String pickListNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private PickListStatus status = PickListStatus.GENERATED;

    @Column(name = "assigned_picker", length = 100)
    private String assignedPicker;

    @OneToMany(mappedBy = "pickList", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PickListItem> items = new ArrayList<>();

    public void addItem(PickListItem item) {
        items.add(item);
        item.setPickList(this);
    }
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/PickListItem.java', `
package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pick_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pick_list_id", nullable = false)
    private PickList pickList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private WarehouseBin bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "quantity_to_pick", nullable = false)
    private int quantityToPick;

    @Column(name = "quantity_picked", nullable = false)
    @Builder.Default
    private int quantityPicked = 0;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;
}
`);

write('backend/src/main/java/com/marketplace/wms/domain/StockTransfer.java', `
package com.marketplace.wms.domain;

import com.marketplace.product.domain.ProductVariant;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "stock_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer extends AuditableEntity {

    @Column(name = "transfer_number", nullable = false, unique = true, length = 50)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id", nullable = false)
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    private Warehouse destinationWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private TransferStatus status = TransferStatus.REQUESTED;

    @Column(name = "carrier_tracking_number", length = 100)
    private String carrierTrackingNumber;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "received_at")
    private Instant receivedAt;
}
`);

// Repositories
write('backend/src/main/java/com/marketplace/wms/repository/WarehouseRepository.java', `
package com.marketplace.wms.repository;

import com.marketplace.wms.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByCode(String code);
}
`);

write('backend/src/main/java/com/marketplace/wms/repository/WarehouseBinRepository.java', `
package com.marketplace.wms.repository;

import com.marketplace.wms.domain.WarehouseBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseBinRepository extends JpaRepository<WarehouseBin, UUID> {
    Optional<WarehouseBin> findByBinCode(String binCode);
    List<WarehouseBin> findByWarehouseId(UUID warehouseId);
    List<WarehouseBin> findByAssignedVariantId(UUID variantId);
}
`);

write('backend/src/main/java/com/marketplace/wms/repository/PickListRepository.java', `
package com.marketplace.wms.repository;

import com.marketplace.wms.domain.PickList;
import com.marketplace.wms.domain.PickListStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PickListRepository extends JpaRepository<PickList, UUID> {
    Optional<PickList> findByPickListNumber(String number);
    Page<PickList> findByWarehouseIdOrderByCreatedAtDesc(UUID warehouseId, Pageable pageable);
    Page<PickList> findByStatus(PickListStatus status, Pageable pageable);
}
`);

write('backend/src/main/java/com/marketplace/wms/repository/StockTransferRepository.java', `
package com.marketplace.wms.repository;

import com.marketplace.wms.domain.StockTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID> {
    Optional<StockTransfer> findByTransferNumber(String transferNumber);
    Page<StockTransfer> findBySourceWarehouseIdOrDestinationWarehouseIdOrderByCreatedAtDesc(UUID src, UUID dest, Pageable pageable);
}
`);

// DTOs
write('backend/src/main/java/com/marketplace/wms/dto/WarehouseDto.java', `
package com.marketplace.wms.dto;

import com.marketplace.wms.domain.WarehouseType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDto {
    private UUID id;
    private String code;
    private String name;
    private WarehouseType warehouseType;
    private String streetAddress;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String countryCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean active;
    private int totalBins;
}
`);

write('backend/src/main/java/com/marketplace/wms/dto/WarehouseBinDto.java', `
package com.marketplace.wms.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseBinDto {
    private UUID id;
    private UUID warehouseId;
    private String warehouseCode;
    private String binCode;
    private String zoneCode;
    private String aisle;
    private String shelf;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantityOnHand;
    private int maxCapacity;
}
`);

write('backend/src/main/java/com/marketplace/wms/dto/PickListDto.java', `
package com.marketplace.wms.dto;

import com.marketplace.wms.domain.PickListStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickListDto {
    private UUID id;
    private String pickListNumber;
    private UUID warehouseId;
    private String warehouseName;
    private UUID sellerOrderId;
    private String sellerOrderNumber;
    private PickListStatus status;
    private String assignedPicker;
    private List<PickListItemDto> items;
    private Instant createdAt;
}
`);

write('backend/src/main/java/com/marketplace/wms/dto/PickListItemDto.java', `
package com.marketplace.wms.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickListItemDto {
    private UUID id;
    private String binCode;
    private String zoneAisle;
    private UUID variantId;
    private String variantSku;
    private String productTitle;
    private int quantityToPick;
    private int quantityPicked;
    private boolean verified;
}
`);

// Service
write('backend/src/main/java/com/marketplace/wms/service/WarehouseManagementService.java', `
package com.marketplace.wms.service;

import com.marketplace.order.domain.OrderItem;
import com.marketplace.order.domain.SellerOrder;
import com.marketplace.order.repository.SellerOrderRepository;
import com.marketplace.shared.exception.BusinessRuleException;
import com.marketplace.shared.exception.ErrorCode;
import com.marketplace.shared.exception.ResourceNotFoundException;
import com.marketplace.wms.domain.*;
import com.marketplace.wms.dto.*;
import com.marketplace.wms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseManagementService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseBinRepository binRepository;
    private final PickListRepository pickListRepository;
    private final StockTransferRepository transferRepository;
    private final SellerOrderRepository sellerOrderRepository;

    @Transactional
    public WarehouseDto createWarehouse(WarehouseDto dto) {
        Warehouse warehouse = Warehouse.builder()
                .code(dto.getCode().toUpperCase().trim())
                .name(dto.getName())
                .warehouseType(dto.getWarehouseType())
                .streetAddress(dto.getStreetAddress())
                .city(dto.getCity())
                .stateProvince(dto.getStateProvince())
                .postalCode(dto.getPostalCode())
                .countryCode(dto.getCountryCode())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .active(true)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse registered [code={}, name={}]", saved.getCode(), saved.getName());
        return toDto(saved);
    }

    @Transactional
    public PickListDto generatePickList(UUID warehouseId, UUID sellerOrderId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));

        SellerOrder sellerOrder = sellerOrderRepository.findById(sellerOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerOrder", "id", sellerOrderId));

        String pickNum = "PICK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        PickList pickList = PickList.builder()
                .pickListNumber(pickNum)
                .warehouse(warehouse)
                .sellerOrder(sellerOrder)
                .status(PickListStatus.GENERATED)
                .build();

        for (OrderItem item : sellerOrder.getItems()) {
            List<WarehouseBin> bins = binRepository.findByAssignedVariantId(item.getVariant().getId());
            WarehouseBin selectedBin = bins.isEmpty() ? null : bins.get(0);

            if (selectedBin != null) {
                PickListItem pickItem = PickListItem.builder()
                        .bin(selectedBin)
                        .variant(item.getVariant())
                        .quantityToPick(item.getQuantity())
                        .quantityPicked(0)
                        .verified(false)
                        .build();
                pickList.addItem(pickItem);
            }
        }

        PickList saved = pickListRepository.save(pickList);
        log.info("Pick list generated [pickNum={}, order={}, itemsCount={}]", pickNum, sellerOrder.getSellerOrderNumber(), saved.getItems().size());
        return toPickListDto(saved);
    }

    @Transactional
    public PickListDto verifyPickedItem(UUID pickListId, UUID pickListItemId, int qtyPicked) {
        PickList pickList = pickListRepository.findById(pickListId)
                .orElseThrow(() -> new ResourceNotFoundException("PickList", "id", pickListId));

        for (PickListItem item : pickList.getItems()) {
            if (item.getId().equals(pickListItemId)) {
                item.setQuantityPicked(qtyPicked);
                item.setVerified(qtyPicked >= item.getQuantityToPick());
                break;
            }
        }

        boolean allPicked = pickList.getItems().stream().allMatch(PickListItem::isVerified);
        if (allPicked) {
            pickList.setStatus(PickListStatus.PICKED);
        } else {
            pickList.setStatus(PickListStatus.PICKING_IN_PROGRESS);
        }

        PickList saved = pickListRepository.save(pickList);
        return toPickListDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PickListDto> getWarehousePickLists(UUID warehouseId, Pageable pageable) {
        return pickListRepository.findByWarehouseIdOrderByCreatedAtDesc(warehouseId, pageable).map(this::toPickListDto);
    }

    private WarehouseDto toDto(Warehouse w) {
        return WarehouseDto.builder()
                .id(w.getId())
                .code(w.getCode())
                .name(w.getName())
                .warehouseType(w.getWarehouseType())
                .streetAddress(w.getStreetAddress())
                .city(w.getCity())
                .stateProvince(w.getStateProvince())
                .postalCode(w.getPostalCode())
                .countryCode(w.getCountryCode())
                .latitude(w.getLatitude())
                .longitude(w.getLongitude())
                .active(w.isActive())
                .totalBins(w.getBins().size())
                .build();
    }

    private PickListDto toPickListDto(PickList p) {
        List<PickListItemDto> itemDtos = p.getItems().stream()
                .map(it -> PickListItemDto.builder()
                        .id(it.getId())
                        .binCode(it.getBin().getBinCode())
                        .zoneAisle(it.getBin().getZoneCode() + "-" + it.getBin().getAisle())
                        .variantId(it.getVariant().getId())
                        .variantSku(it.getVariant().getSku())
                        .productTitle(it.getVariant().getProduct().getTitle())
                        .quantityToPick(it.getQuantityToPick())
                        .quantityPicked(it.getQuantityPicked())
                        .verified(it.isVerified())
                        .build())
                .collect(Collectors.toList());

        return PickListDto.builder()
                .id(p.getId())
                .pickListNumber(p.getPickListNumber())
                .warehouseId(p.getWarehouse().getId())
                .warehouseName(p.getWarehouse().getName())
                .sellerOrderId(p.getSellerOrder().getId())
                .sellerOrderNumber(p.getSellerOrder().getSellerOrderNumber())
                .status(p.getStatus())
                .assignedPicker(p.getAssignedPicker())
                .items(itemDtos)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
`);

// Controller
write('backend/src/main/java/com/marketplace/wms/controller/WarehouseController.java', `
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
`);

// Frontend WMS Page
write('frontend/src/features/wms/WarehouseFulfillmentPage.tsx', `
import React, { useState } from 'react';
import { Warehouse, Boxes, CheckCircle, Clock, Truck, QrCode, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';

interface MockWarehouse {
  id: string;
  code: string;
  name: string;
  city: string;
  binsCount: number;
  activeOrders: number;
}

const mockWarehouses: MockWarehouse[] = [
  {
    id: 'wh-1',
    code: 'WMS-WEST-01',
    name: 'Bay Area Multi-Vendor Fulfillment Center',
    city: 'Oakland, CA',
    binsCount: 4200,
    activeOrders: 18,
  },
  {
    id: 'wh-2',
    code: 'WMS-EAST-02',
    name: 'Tri-State Cross-Dock Logistics Hub',
    city: 'Newark, NJ',
    binsCount: 6500,
    activeOrders: 34,
  },
];

export const WarehouseFulfillmentPage: React.FC = () => {
  const [warehouses] = useState<MockWarehouse[]>(mockWarehouses);
  const [activeTab, setActiveTab] = useState<'warehouses' | 'picklists'>('warehouses');
  const [scanModalOpen, setScanModalOpen] = useState(false);
  const [scanSuccess, setScanSuccess] = useState(false);

  const handleScan = () => {
    setScanSuccess(true);
    setTimeout(() => {
      setScanSuccess(false);
      setScanModalOpen(false);
    }, 1500);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <Warehouse className="w-6 h-6 text-primary-600" /> Multi-Location Warehouse Management (WMS)
          </h1>
          <p className="text-gray-500 text-sm mt-1">Smart bin-level inventory allocation, barcode picking routes, and split-order fulfillment.</p>
        </div>
        <Button onClick={() => setScanModalOpen(true)}>
          <QrCode className="w-4 h-4 mr-2" /> Barcode Scan Dispatch
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        {warehouses.map((wh) => (
          <div key={wh.id} className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm hover:border-gray-300 transition-colors">
            <div className="flex items-center justify-between mb-4">
              <span className="font-mono text-xs font-bold text-primary-600 bg-primary-50 px-2.5 py-1 rounded-md">{wh.code}</span>
              <Badge variant="success">Online & Dispatched</Badge>
            </div>
            <h3 className="font-bold text-lg text-gray-900">{wh.name}</h3>
            <p className="text-xs text-gray-500 mt-1">{wh.city}</p>

            <div className="grid grid-cols-2 gap-4 mt-6 pt-4 border-t border-gray-100">
              <div>
                <span className="text-[11px] uppercase font-bold text-gray-400">Allocated Bins</span>
                <p className="text-xl font-extrabold text-gray-900 mt-0.5">{wh.binsCount} bins</p>
              </div>
              <div>
                <span className="text-[11px] uppercase font-bold text-gray-400">Picking Queue</span>
                <p className="text-xl font-extrabold text-amber-600 mt-0.5">{wh.activeOrders} orders</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Barcode Scanner Modal */}
      <Modal isOpen={scanModalOpen} onClose={() => setScanModalOpen(false)} title="WMS Barcode Verification Scanner">
        {scanSuccess ? (
          <div className="text-center py-6">
            <CheckCircle className="w-12 h-12 text-green-500 mx-auto mb-3" />
            <h3 className="font-bold text-gray-900">SKU Matched & Pick Verified</h3>
            <p className="text-sm text-gray-500 mt-1">Item [SONY-WH1000XM5-BLK] matched in Bin A-14-3. Ready for packing station.</p>
          </div>
        ) : (
          <div className="text-center py-8 space-y-4">
            <div className="w-32 h-32 border-2 border-dashed border-primary-500 rounded-2xl mx-auto flex items-center justify-center bg-primary-50/50">
              <QrCode className="w-16 h-16 text-primary-600 animate-pulse" />
            </div>
            <p className="text-sm font-medium text-gray-700">Point optical scanner at bin or product variant barcode.</p>
            <Button onClick={handleScan}>Simulate Optical Barcode Scan</Button>
          </div>
        )}
      </Modal>
    </div>
  );
};
`);

console.log('WMS Multi-Warehouse Domain Generated.');
`);
