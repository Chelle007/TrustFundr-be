package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.UserProfileRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "User profiles")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-profiles")
public class ViewUserProfileController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class UserProfileResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Transactional(readOnly = true)
    public List<UserProfileResponse> listUserProfiles() {
        return userProfileRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(p -> modelMapper.map(p, UserProfileResponse.class))
                .toList();
    }
}

