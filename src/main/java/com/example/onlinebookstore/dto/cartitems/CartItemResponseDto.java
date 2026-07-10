package com.example.onlinebookstore.dto.cartitems;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {
}
