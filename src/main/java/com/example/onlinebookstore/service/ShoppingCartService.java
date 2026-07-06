package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.cartitems.CartItemRequestDto;
import com.example.onlinebookstore.dto.cartitems.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCartForCurrentUser();

    ShoppingCartResponseDto addBookToCart(CartItemRequestDto request);

    ShoppingCartResponseDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto request);

    void removeCartItem(Long cartItemId);
}
