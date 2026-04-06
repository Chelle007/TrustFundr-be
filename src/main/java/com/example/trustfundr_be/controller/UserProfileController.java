package com.example.trustfundr_be.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    private static final String BASIC_AUTH_SCHEME = "HTTP Basic";

    private final UserProfileService userProfileService;

    @SecurityRequirement(name = BASIC_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user-profile")
    public UserProfileResponse createUserProfile(@Valid @RequestBody CreateUserProfileRequest request) {
        return userProfileService.createUserProfile(request);
    }
}
