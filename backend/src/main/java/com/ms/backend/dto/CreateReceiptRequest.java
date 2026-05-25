package com.ms.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateReceiptRequest(
        LocalDateTime transactionDate,
        String paymentMethod,
        BigDecimal totalAmount,
        Long cashierId
) {
}
