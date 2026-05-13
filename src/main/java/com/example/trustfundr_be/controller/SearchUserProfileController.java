package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.UserProfile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-profiles")
public class SearchUserProfileController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserProfile userProfileRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchUserProfileResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search-user-profiles")
    @Transactional(readOnly = true)
    public List<SearchUserProfileResponse> searchUserProfiles(
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return userProfileRepository
                .searchByKeyword(q.trim(), Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                // Map user profile to response
                .map(p -> modelMapper.map(p, SearchUserProfileResponse.class))
                .toList();
    }
}
