package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User accounts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-accounts")
public class CreateUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class CreateUserAccountRequest {

        @NotNull(message = "User profile is required")
        private UUID userProfileId;

        @NotBlank(message = "Full name is required")
        @Size(max = 255)
        private String fullName;

        @NotBlank(message = "Username is required")
        @Size(max = 100)
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class CreateUserAccountResponse {
        private UUID id;
        private String fullName;
        private String username;
        private UUID userProfileId;
        private String userProfileName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user-account")
    @Transactional
    public CreateUserAccountResponse createUserAccount(@Valid @RequestBody CreateUserAccountRequest request) {
        // Create user account
        UserAccount saved = userAccountRepository.createUserAccount(request);

        // Map saved user account to response
        CreateUserAccountResponse response = modelMapper.map(saved, CreateUserAccountResponse.class);
        // Set user profile id and name on response
        if (saved.getUserProfile() != null) {
            response.setUserProfileId(saved.getUserProfile().getId());
            response.setUserProfileName(saved.getUserProfile().getName());
        }
        return response;
    }
}
