package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.user.UserLoginRequestDto;
import com.example.onlinebookstore.dto.user.UserLoginResponseDto;

public interface AuthentificationService {

    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
