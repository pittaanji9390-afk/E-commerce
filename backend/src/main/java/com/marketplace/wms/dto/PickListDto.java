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
