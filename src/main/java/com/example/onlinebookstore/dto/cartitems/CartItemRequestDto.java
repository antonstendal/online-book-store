package com.example.onlinebookstore.dto.cartitems;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @NotNull
        Long bookId,
        @Positive
        int quantity
) {
}
