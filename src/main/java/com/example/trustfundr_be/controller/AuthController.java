package com.example.trustfundr_be.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.example.trustfundr_be.model.dto.LoginRequest;
import com.example.trustfundr_be.model.dto.LoginResponse;
import com.example.trustfundr_be.model.dto.LogoutResponse;
import com.example.trustfundr_be.service.AuthService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return authService.authenticate(loginRequest, request);
    }

    @SecurityRequirements
    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
