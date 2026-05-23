package com.ms.backend.dto;
import java.math.BigDecimal;
public record CreateProductRequest(
        String sku,
        String name,
        String category,
        BigDecimal price,
        Integer stock_quantity
) {
}
