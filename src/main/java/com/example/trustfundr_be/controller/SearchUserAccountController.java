package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.exception.UserAccountException;
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
public class SearchUserAccountController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserAccountRepository userAccountRepository;
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
    public List<SearchUserAccountResponse> searchUserAccounts(@RequestParam("q") String q) {
        // Validate search query
        String term = q != null ? q.trim() : "";
        if (term.isEmpty()) {
            throw new UserAccountException(HttpStatus.BAD_REQUEST, "Search query is required");
        }

        // Search user accounts by keyword and map each to response
        return userAccountRepository.searchByKeyword(term).stream()
                .map(this::toResponse)
                .toList();
    }

    private SearchUserAccountResponse toResponse(UserAccount account) {
        // Map account fields to response
        SearchUserAccountResponse response = modelMapper.map(account, SearchUserAccountResponse.class);
        // Set user profile id and name on response
        if (account.getUserProfile() != null) {
            response.setUserProfileId(account.getUserProfile().getId());
            response.setUserProfileName(account.getUserProfile().getName());
        }
        return response;
    }
}
