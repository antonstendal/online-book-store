package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.cartitems.CartItemRequestDto;
import com.example.onlinebookstore.dto.cartitems.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.onlinebookstore.exception.DataProcessingException;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.ShoppingCartMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.CartItemRepository;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartResponseDto getCartForCurrentUser() {
        ShoppingCart cart = findCart(getCurrentUser().getId());
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDto addBookToCart(CartItemRequestDto request) {
        User currentUser = getCurrentUser();
        ShoppingCart cart = findCart(currentUser.getId());
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book with id " + request.bookId()));
        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getBook().getId().equals(request.bookId()))
                .findFirst()
                .orElse(null);
        if (item != null) {
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setShoppingCart(cart);
            newItem.setQuantity(request.quantity());
            cart.getCartItems().add(newItem);
        }
        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDto updateCartItem(Long cartItemId,
                                                  UpdateCartItemRequestDto request) {
        User currentUser = getCurrentUser();
        ShoppingCart cart = findCart(currentUser.getId());

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item with id " + cartItemId));
        validateOwner(cartItem, cart);
        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        User currentUser = getCurrentUser();
        ShoppingCart cart = findCart(currentUser.getId());

        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find cart item with id " + cartItemId));
        validateOwner(item, cart);
        cartItemRepository.delete(item);
    }

    private User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private ShoppingCart findCart(Long userId) {
        return shoppingCartRepository.findByUserIdWithItems(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping cart for user " + userId));
    }

    private void validateOwner(CartItem item, ShoppingCart cart) {
        if (!item.getShoppingCart().getId().equals(cart.getId())) {
            throw new DataProcessingException(
                    "Cart item does not belong to the current user's shopping cart");
        }
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }
}
