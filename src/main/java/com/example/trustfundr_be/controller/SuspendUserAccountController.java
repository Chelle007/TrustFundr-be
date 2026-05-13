package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.UserAccount;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User accounts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-accounts")
public class SuspendUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccount userAccountRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SuspendUserAccountResponse {
        private UUID id;
        private String fullName;
        private String username;
        private UUID userProfileId;
        private String userProfileName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/suspend-user-account/{id}")
    @Transactional
    public SuspendUserAccountResponse suspendUserAccount(@PathVariable UUID id) {
        // Suspend user account
        UserAccountModel saved = userAccountRepository.suspendUserAccount(id);

        // Map saved user account to response
        SuspendUserAccountResponse response = modelMapper.map(saved, SuspendUserAccountResponse.class);
        if (saved.getUserProfile() != null) {
            response.setUserProfileId(saved.getUserProfile().getId());
            response.setUserProfileName(saved.getUserProfile().getName());
        }
        return response;
    }
}
