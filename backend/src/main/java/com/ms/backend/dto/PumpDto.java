package com.ms.backend.dto;

import java.util.List;

public record PumpDto(
        Long id,
        String pumpStatus,
        List<PumpHoseDto> hoses
) {
}
