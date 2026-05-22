package com.ms.backend.dto;

import java.math.BigDecimal;

public record CreatePumpHoseRequest(
        Long pumpId,
        Long tankId,
        String fuelType,
        BigDecimal pricePerLiter
) {
}
