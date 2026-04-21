package com.example.trustfundr_be.controller;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising activities")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fundraiser/fundraising-activities")
public class CreateFundraisingActivityController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class CreateFundraisingActivityRequest {

        @NotBlank(message = "Title is required")
        @Size(max = 255)
        private String title;

        @Size(max = 5000)
        private String description;
    }

    @Data
    @NoArgsConstructor
    public static class CreateFundraisingActivityResponse {
        private UUID id;
        private String title;
        private String description;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('FUNDRAISER')")
    @PostMapping("/create-fundraising-activity")
    @Transactional
    public CreateFundraisingActivityResponse createFundraisingActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFundraisingActivityRequest request) {
        // Create fundraising activity
        FundraisingActivity saved = fundraisingActivityRepository.createFundraisingActivity(userDetails.getUsername(),
                request);

        // Map saved fundraising activity to response
        return modelMapper.map(saved, CreateFundraisingActivityResponse.class);
    }
}
