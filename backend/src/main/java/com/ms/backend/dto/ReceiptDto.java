package com.ms.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptDto(
        Long id,
        LocalDateTime transactionDate,
        String paymentMethod,
        BigDecimal totalAmount,
        Long cashierId
) {
}
