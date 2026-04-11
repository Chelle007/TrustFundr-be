package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.UserAccountRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User accounts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-accounts")
public class ViewUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class UserAccountResponse {
        private UUID id;
        private String fullName;
        private String username;
        private UUID userProfileId;
        private String userProfileName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Transactional(readOnly = true)
    public List<UserAccountResponse> listUserAccounts() {
        // Load all user accounts with profiles and map each to response
        return userAccountRepository.findAllWithUserProfileOrderByUsernameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    private UserAccountResponse toResponse(UserAccount account) {
        // Map account fields to response
        UserAccountResponse response = modelMapper.map(account, UserAccountResponse.class);
        // Set user profile id and name on response
        if (account.getUserProfile() != null) {
            response.setUserProfileId(account.getUserProfile().getId());
            response.setUserProfileName(account.getUserProfile().getName());
        }
        return response;
    }
}
