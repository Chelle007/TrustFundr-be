package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;
import com.example.trustfundr_be.repository.UserProfileRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User accounts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-accounts")
public class UpdateUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class UpdateUserAccountRequest {

        @NotBlank(message = "Full name is required")
        @Size(max = 255)
        private String fullName;

        @NotBlank(message = "Username is required")
        @Size(max = 100)
        private String username;

        @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
        private String password;

        private UUID userProfileId;
    }

    @Data
    @NoArgsConstructor
    public static class UpdateUserAccountResponse {
        private UUID id;
        private String fullName;
        private String username;
        private UUID userProfileId;
        private String userProfileName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-user-account/{id}")
    @Transactional
    public UpdateUserAccountResponse updateUserAccount(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserAccountRequest request) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User account not found"));

        if (request.getUserProfileId() != null) {
            UserProfile profile = userProfileRepository.findById(request.getUserProfileId())
                    .orElseThrow(() -> new UserAccountException(HttpStatus.NOT_FOUND, "User profile not found"));
            userAccount.setUserProfile(profile);
        }

        modelMapper.map(request, userAccount);

        // Update password if provided
        String newPassword = request.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            userAccount.setPasswordHashString(passwordEncoder.encode(newPassword));
        }

        // Save user account
        UserAccount saved = userAccountRepository.save(userAccount);

        // Map saved account to response and set profile fields
        UpdateUserAccountResponse response = modelMapper.map(saved, UpdateUserAccountResponse.class);
        if (saved.getUserProfile() != null) {
            response.setUserProfileId(saved.getUserProfile().getId());
            response.setUserProfileName(saved.getUserProfile().getName());
        }
        return response;
    }
}
