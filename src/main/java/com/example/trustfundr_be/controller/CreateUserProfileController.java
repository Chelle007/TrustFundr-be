package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-profiles")
public class CreateUserProfileController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserProfileRepository userProfileRepository;

    @Data
    @NoArgsConstructor
    public static class CreateUserProfileRequest {

        @NotBlank(message = "Name is required")
        @Size(max = 255)
        private String name;

        @Size(max = 2000)
        private String description;
    }

    public record CreateUserProfileResponse(UUID id, String name, String description) {
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user-profile")
    @Transactional
    public CreateUserProfileResponse createUserProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new UserProfileException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        }
        if (userProfileRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new UserProfileException(HttpStatus.CONFLICT, "A user profile with this name already exists");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setName(name);
        userProfile.setDescription(request.getDescription());
        UserProfile saved = userProfileRepository.save(userProfile);

        return new CreateUserProfileResponse(saved.getId(), saved.getName(), saved.getDescription());
    }
}

