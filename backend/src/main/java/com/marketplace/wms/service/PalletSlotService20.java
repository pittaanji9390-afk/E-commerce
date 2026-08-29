package com.marketplace.wms.service;

import com.marketplace.wms.domain.WarehousePalletSlot20;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PalletSlotService20 {

    public BigDecimal calculateWeight(List<WarehousePalletSlot20> slots) {
        return slots.stream()
                .map(WarehousePalletSlot20::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
