package com.example.onlinebookstore.dto.shoppingcart;

import com.example.onlinebookstore.dto.cartitems.CartItemResponseDto;
import java.util.List;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems
) {
}
