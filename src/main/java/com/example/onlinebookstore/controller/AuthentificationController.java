package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.user.UserLoginRequestDto;
import com.example.onlinebookstore.dto.user.UserLoginResponseDto;
import com.example.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.example.onlinebookstore.dto.user.UserResponseDto;
import com.example.onlinebookstore.exception.RegistrationException;
import com.example.onlinebookstore.service.AuthentificationService;
import com.example.onlinebookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for authentication users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthentificationController {
    private final UserService userService;
    private final AuthentificationService authentificationService;

    @Operation(summary = "Registration", description = "New user registration")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @Operation(summary = "Login", description = "Login user")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authentificationService.authenticate(request);
    }
}
