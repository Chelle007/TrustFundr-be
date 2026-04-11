package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.example.trustfundr_be.exception.AccountDisabledException;
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

    private static final String BAD_CREDENTIALS = "Invalid username or password";

    private final AuthenticationManager authenticationManager;
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
        // Validate request body
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(), loginRequest.getPassword());

        // Authenticate user
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException e) {
            throw new AccountDisabledException(
                    e.getMessage() != null
                            ? e.getMessage()
                            : "User account has been deactivated. Please contact an administrator");
        } catch (BadCredentialsException e) {
            throw new AuthException(BAD_CREDENTIALS);
        } catch (AuthenticationException e) {
            throw new AuthException(BAD_CREDENTIALS);
        }

        // Find user account
        UserAccount userAccount = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        LoginResponse response = modelMapper.map(userAccount, LoginResponse.class);

        // Generate token
        response.setToken(jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()));

        // Return response
        return response;
    }
}
