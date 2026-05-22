package com.ms.backend.dto;

import java.math.BigDecimal;

public record PumpHoseDto(
        Long id,
        Long pumpId,
        Long tankId,
        String fuelType,
        BigDecimal pricePerLiter
) {
}
