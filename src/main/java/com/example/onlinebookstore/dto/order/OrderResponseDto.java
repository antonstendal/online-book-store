package com.example.onlinebookstore.dto.order;

import com.example.onlinebookstore.model.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        Long userId,
        List<OrderItemResponseDto> orderItems,
        LocalDateTime orderDate,
        BigDecimal total,
        Status status
) {
}
