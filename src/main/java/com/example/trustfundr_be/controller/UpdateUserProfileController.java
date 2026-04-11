package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/user-profiles")
public class UpdateUserProfileController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class UpdateUserProfileRequest {

        @NotBlank(message = "Name is required")
        @Size(max = 255)
        private String name;

        @Size(max = 2000)
        private String description;
    }

    @Data
    @NoArgsConstructor
    public static class UpdateUserProfileResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-user-profile/{id}")
    @Transactional
    public UpdateUserProfileResponse updateUserProfile(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        // Validate request body
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new UserProfileException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        }

        // Find existing user profile
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found"));

        // Ensure unique name (case-insensitive) across profiles
        userProfileRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(userProfile.getId())) {
                throw new UserProfileException(HttpStatus.CONFLICT, "A user profile with this name already exists");
            }
        });

        // Update user profile
        userProfile.setName(name);
        userProfile.setDescription(request.getDescription());

        // Save updated profile
        UserProfile saved = userProfileRepository.save(userProfile);

        // Map saved user profile to response
        return modelMapper.map(saved, UpdateUserProfileResponse.class);
    }
}

