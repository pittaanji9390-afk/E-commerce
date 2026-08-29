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
