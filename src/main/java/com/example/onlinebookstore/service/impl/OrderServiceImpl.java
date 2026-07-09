package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.order.OrderItemResponseDto;
import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderStatusUpdateDto;
import com.example.onlinebookstore.mapper.OrderItemMapper;
import com.example.onlinebookstore.mapper.OrderMapper;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.Status;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.repository.order.OrderItemRepository;
import com.example.onlinebookstore.repository.order.OrderRepository;
import com.example.onlinebookstore.service.OrderService;
import java.math.BigDecimal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        User currentUser = getCurrentUser();
        ShoppingCart shoppingCart = currentUser.getShoppingCart();
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        BigDecimal total = cartItems.stream()
                .map(cartItem ->
                        cartItem.getBook().getPrice()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order newOrder = new Order();
        newOrder.setUser(getCurrentUser());
        newOrder.setStatus(Status.PENDING);
        newOrder.setShippingAddress(requestDto.shippingAddress());
        newOrder.setTotal(total);
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            newOrder.getOrderItems().add(orderItem);
        }
        Order savedOrder = orderRepository.save(newOrder);
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Page<OrderResponseDto> getOrderHistory(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Order> orders = orderRepository.findAllByUserId(currentUser.getId(), pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    public OrderItemResponseDto getSingleOrderItem(Long orderId, Long itemId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getId());
        return null;
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto requestDto) {
        return null;
    }

    private User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
