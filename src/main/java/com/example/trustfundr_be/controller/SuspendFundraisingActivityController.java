package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.repository.FundraisingActivity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising activities")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fundraiser/fundraising-activities")
public class SuspendFundraisingActivityController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivity fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SuspendFundraisingActivityResponse {
        private UUID id;
        private String title;
        private String description;
        private UUID categoryId;
        private String category;
        private String location;
        private BigDecimal goalAmount;
        private BigDecimal currentAmount;
        private String imageUrl;
        private long viewCount;
        private long favouriteCount;
        private Instant completedAt;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('FUNDRAISER')")
    @PostMapping("/suspend-fundraising-activity/{id}")
    @Transactional
    public SuspendFundraisingActivityResponse suspendFundraisingActivity(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        // Suspend fundraising activity (soft delete, same pattern as user account suspend)
        FundraisingActivityModel saved = fundraisingActivityRepository.suspendFundraisingActivity(userDetails.getUsername(),
                id);

        // Map saved fundraising activity to response
        return modelMapper.map(saved, SuspendFundraisingActivityResponse.class);
    }
}
