package com.example.trustfundr_be.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trustfundr_be.exception.AccountDisabledException;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.model.dto.LoginRequest;
import com.example.trustfundr_be.model.dto.LoginResponse;
import com.example.trustfundr_be.model.entity.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final String BAD_CREDENTIALS = "Invalid username or password";

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        UserAccount userAccount = userAccountRepository.findByUsername(loginRequest.getUsername());
        if (userAccount == null) {
            throw new AuthException(BAD_CREDENTIALS);
        }
        if (userAccount.isDeleted()) {
            throw new AccountDisabledException(
                    "User account has been deactivated. Please contact an administrator");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), userAccount.getPasswordHashString())) {
            throw new AuthException(BAD_CREDENTIALS);
        }
        return modelMapper.map(userAccount, LoginResponse.class);
    }
}
