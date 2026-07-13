package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.order.OrderItemResponseDto;
import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderStatusUpdateDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.exception.OrderProcessingException;
import com.example.onlinebookstore.mapper.OrderItemMapper;
import com.example.onlinebookstore.mapper.OrderMapper;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.Order;
import com.example.onlinebookstore.model.OrderItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.Status;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.ShoppingCartRepository;
import com.example.onlinebookstore.repository.order.OrderRepository;
import com.example.onlinebookstore.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        User currentUser = getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserIdWithItems(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart for user " + currentUser.getId() + " not found"));

        Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new OrderProcessingException(
                    "Shopping cart for user " + currentUser.getId() + " is empty");
        }
        BigDecimal total = cartItems.stream()
                .map(cartItem ->
                        cartItem.getBook().getPrice()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order newOrder = new Order();
        newOrder.setUser(currentUser);
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
    public List<OrderItemResponseDto> getOrderItems(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order with id " + orderId + " not found"));
        return order.getOrderItems().stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    public OrderItemResponseDto getSingleOrderItem(Long orderId, Long itemId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order with id " + orderId + " not found"));
        Set<OrderItem> orderItems = order.getOrderItems();
        return orderItemMapper.toDto(orderItems
                .stream()
                .filter(orderItem -> Objects.equals(orderItem.getId(), itemId))
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("Can't find item with id " + itemId)));
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto requestDto) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order with id " + orderId + " not found"));
        existingOrder.setStatus(requestDto.status());
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }

    private User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
