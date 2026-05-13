package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.UserAccount;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User accounts")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-accounts")
public class SearchUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccount userAccountRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchUserAccountResponse {
        private UUID id;
        private String fullName;
        private String username;
        private UUID userProfileId;
        private String userProfileName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search-user-accounts")
    @Transactional(readOnly = true)
    public List<SearchUserAccountResponse> searchUserAccounts(
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return userAccountRepository.searchByKeyword(q.trim()).stream()
                .map(this::toResponse)
                .toList();
    }

    // Map user account to response
    private SearchUserAccountResponse toResponse(UserAccountModel account) {
        SearchUserAccountResponse response = modelMapper.map(account, SearchUserAccountResponse.class);
        if (account.getUserProfile() != null) {
            response.setUserProfileId(account.getUserProfile().getId());
            response.setUserProfileName(account.getUserProfile().getName());
        }
        return response;
    }
}
