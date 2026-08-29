package com.marketplace.wms.service;

import com.marketplace.wms.domain.WarehousePalletSlot4;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PalletSlotService4 {

    public BigDecimal calculateWeight(List<WarehousePalletSlot4> slots) {
        return slots.stream()
                .map(WarehousePalletSlot4::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
