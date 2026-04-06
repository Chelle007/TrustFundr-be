package com.example.trustfundr_be.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import com.example.trustfundr_be.model.dto.CreateUserProfileRequest;
import com.example.trustfundr_be.model.dto.UserProfileResponse;
import com.example.trustfundr_be.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Create a user profile (admin only)",
            description = "Requires an authenticated user whose profile is Admin (e.g. session cookie after POST /api/auth/login). "
                    + "HTTP Basic also works if enabled on the server.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserProfileResponse createUserProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        return userProfileService.createUserProfile(request);
    }
}
