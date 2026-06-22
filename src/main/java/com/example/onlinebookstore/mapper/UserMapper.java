package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.user.CreateUserRequestDto;
import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto mapToResponse(User user);

    User mapToModel(CreateUserRequestDto requestDto);
}
