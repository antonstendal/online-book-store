package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.order.OrderRequestDto;
import com.example.onlinebookstore.dto.order.OrderResponseDto;
import com.example.onlinebookstore.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderResponseDto toDto(Order order);

    Order toEntity(OrderRequestDto requestDto);
}
