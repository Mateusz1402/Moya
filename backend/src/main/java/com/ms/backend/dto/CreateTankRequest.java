package com.ms.backend.dto;

import java.math.BigDecimal;

public record CreateTankRequest(
        String fuelType,
        BigDecimal capacityLitres,
        BigDecimal currentLevelLitres
) {
}
