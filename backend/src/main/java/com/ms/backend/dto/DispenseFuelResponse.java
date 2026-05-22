package com.ms.backend.dto;

import java.math.BigDecimal;

public record DispenseFuelResponse(
        Long pumpId,
        Long hoseId,
        String fuelType,
        BigDecimal litresRequested,
        BigDecimal pricePerLiter,
        BigDecimal estimatedCost,
        Long estimatedDurationSeconds,
        String status
) {
}
