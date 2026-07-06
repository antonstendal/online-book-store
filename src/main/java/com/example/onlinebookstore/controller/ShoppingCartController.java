package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.cartitems.CartItemRequestDto;
import com.example.onlinebookstore.dto.cartitems.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get shopping cart", description = "Return shopping cart for login user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ShoppingCartResponseDto getCart() {
        return shoppingCartService.getCartForCurrentUser();
    }

    @Operation(summary = "Add book to shopping cart", description = "Add book to shopping cart")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartResponseDto addBookToCart(
            @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addBookToCart(requestDto);
    }

    @Operation(summary = "Update shopping cart", description = "Update shopping cart items")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{id}")
    public ShoppingCartResponseDto updateCartItem(@PathVariable("id") Long cartItemId,
                                                  @RequestBody @Valid
                                                  UpdateCartItemRequestDto request) {
        return shoppingCartService.updateCartItem(cartItemId, request);
    }

    @Operation(summary = "Delete items from shopping cart",
            description = "Remove items from shopping cart")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PathVariable("id") Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
    }
}
