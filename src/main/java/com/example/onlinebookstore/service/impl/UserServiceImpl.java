package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.user.CreateUserRequestDto;
import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.mapper.UserMapper;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.user.UserRepository;
import com.example.onlinebookstore.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(CreateUserRequestDto requestDto) {
        User newUser = userMapper.mapToModel(requestDto);
        return userMapper.mapToResponse(userRepository.save(newUser));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToResponse)
                .toList();
    }
}
