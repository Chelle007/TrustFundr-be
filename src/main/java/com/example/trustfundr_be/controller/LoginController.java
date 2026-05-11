package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.example.trustfundr_be.app.security.JwtService;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Login")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    @Data
    @NoArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class LoginResponse {
        private UUID id;
        private String fullName;
        private String username;
        private String token;
    }

    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        // Fetch user account through repository
        UserAccount userAccount = userAccountRepository
                .findByUsernameAndPassword(loginRequest.getUsername(), loginRequest.getPassword())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        // Map user account to response
        LoginResponse response = modelMapper.map(userAccount, LoginResponse.class);
        response.setToken(jwtService.generateToken(toUserDetails(userAccount)));
        return response;
    }

    // Map to UserDetails (UserAccount's username and password + UserProfile's role)
    private static UserDetails toUserDetails(UserAccount account) {
        String role = "USER";
        if (account.getUserProfile() != null) {
            String profileName = account.getUserProfile().getName();
            if ("Admin".equalsIgnoreCase(profileName)) {
                role = "ADMIN";
            } else if ("Fund Raiser".equalsIgnoreCase(profileName)) {
                role = "FUNDRAISER";
            } else if ("Donee".equalsIgnoreCase(profileName)) {
                role = "DONEE";
            } else if ("Platform Manager".equalsIgnoreCase(profileName)
                    || "Platform Management".equalsIgnoreCase(profileName)) {
                role = "PLATFORM_MANAGEMENT";
            }
        }
        return User.builder()
                .username(account.getUsername())
                .password(account.getPasswordHashString())
                .roles(role)
                .build();
    }
}
