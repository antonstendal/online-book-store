package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.order.OrderItemResponseDto;
import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.dto.order.OrderStatusUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    Page<OrderResponseDto> getOrderHistory(Pageable pageable);

    OrderItemResponseDto getSingleOrderItem(Long orderId, Long itemId);

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateDto requestDto);
}
