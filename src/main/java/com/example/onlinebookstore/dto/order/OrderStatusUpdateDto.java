package com.example.onlinebookstore.dto.order;

import com.example.onlinebookstore.model.Status;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDto(
        @NotNull
        Status status
) {
}
