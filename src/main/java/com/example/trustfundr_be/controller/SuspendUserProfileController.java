package com.example.trustfundr_be.controller;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-profiles")
public class SuspendUserProfileController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SuspendUserProfileResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/suspend-user-profile/{id}")
    @Transactional
    public SuspendUserProfileResponse suspendUserProfile(@PathVariable UUID id) {
        // Find existing user profile
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found"));

        // Suspend user profile (soft delete)
        userProfile.softDelete();

        // Save user profile
        UserProfile saved = userProfileRepository.save(userProfile);

        // Map saved user profile to response
        return modelMapper.map(saved, SuspendUserProfileResponse.class);
    }
}
