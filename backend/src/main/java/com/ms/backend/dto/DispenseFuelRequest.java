package com.ms.backend.dto;

import java.math.BigDecimal;

public record DispenseFuelRequest(
        Long pumpId,
        Long hoseId,
        BigDecimal litres
) {
}
