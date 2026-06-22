package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.user.CreateUserRequestDto;
import com.example.onlinebookstore.dto.user.UserDto;
import com.example.onlinebookstore.exception.RegistrationException;
import com.example.onlinebookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthentificationController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserDto register(@RequestBody @Valid CreateUserRequestDto request)
            throws RegistrationException {
        return userService.saveUser(request);
    }
}
