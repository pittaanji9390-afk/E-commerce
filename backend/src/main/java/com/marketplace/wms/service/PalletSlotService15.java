package com.marketplace.wms.service;

import com.marketplace.wms.domain.WarehousePalletSlot15;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PalletSlotService15 {

    public BigDecimal calculateWeight(List<WarehousePalletSlot15> slots) {
        return slots.stream()
                .map(WarehousePalletSlot15::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
