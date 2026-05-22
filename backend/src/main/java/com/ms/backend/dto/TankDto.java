package com.ms.backend.dto;

import java.math.BigDecimal;

public record TankDto(
        Long id,
        String fuelType,
        BigDecimal capacityLitres,
        BigDecimal currentLevelLitres,
        BigDecimal fillPercentage
) {
}
