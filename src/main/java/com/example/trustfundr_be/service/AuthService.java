package com.example.trustfundr_be.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.trustfundr_be.exception.AccountDisabledException;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.model.dto.LoginRequest;
import com.example.trustfundr_be.model.dto.LoginResponse;
import com.example.trustfundr_be.model.dto.LogoutResponse;
import com.example.trustfundr_be.model.entity.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String BAD_CREDENTIALS = "Invalid username or password";

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    public LoginResponse authenticate(LoginRequest loginRequest, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession(true);
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

        UserAccount userAccount = userAccountRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
        return modelMapper.map(userAccount, LoginResponse.class);
    }

    public LogoutResponse logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        try {
            request.logout();
        } catch (ServletException ignored) {
        }
        return new LogoutResponse("Logged out successfully");
    }
}
