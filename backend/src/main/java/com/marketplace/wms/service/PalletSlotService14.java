package com.marketplace.wms.service;

import com.marketplace.wms.domain.WarehousePalletSlot14;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PalletSlotService14 {

    public BigDecimal calculateWeight(List<WarehousePalletSlot14> slots) {
        return slots.stream()
                .map(WarehousePalletSlot14::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
