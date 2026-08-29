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
