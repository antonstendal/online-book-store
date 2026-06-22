package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.user.CreateUserRequestDto;
import com.example.onlinebookstore.dto.user.UserDto;
import java.util.List;

public interface UserService {

    UserDto saveUser(CreateUserRequestDto requestDto);

    List<UserDto> getAllUsers();
}
