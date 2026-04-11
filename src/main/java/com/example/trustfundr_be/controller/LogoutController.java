package com.example.trustfundr_be.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Logout")
@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    public record LogoutResponse(String message) {
    }

    @SecurityRequirements
    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request) {
        // Clear security context
        SecurityContextHolder.clearContext();

        // Logout user
        try {
            request.logout();
        } catch (ServletException ignored) {
        }

        // Return response
        return new LogoutResponse("Logged out successfully");
    }
}
