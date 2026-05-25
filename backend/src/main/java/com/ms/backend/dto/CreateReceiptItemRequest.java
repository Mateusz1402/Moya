package com.ms.backend.dto;

import java.math.BigDecimal;

public record CreateReceiptItemRequest(
        Long receiptId,
        Long productId,
        Long pumpId,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
