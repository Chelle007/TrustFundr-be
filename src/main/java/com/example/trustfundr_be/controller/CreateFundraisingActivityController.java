package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
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

import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.repository.FundraisingActivity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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

    private final FundraisingActivity fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class CreateFundraisingActivityRequest {

        @NotBlank(message = "Title is required")
        @Size(max = 255)
        private String title;

        @Size(max = 5000)
        private String description;

        @Size(max = 255)
        private String category;

        @Size(max = 255)
        private String location;

        /** Optional; defaults to 0 when omitted. */
        @DecimalMin(value = "0.0", inclusive = true, message = "Goal amount must be zero or positive")
        private BigDecimal goalAmount;

        /** Optional; defaults to 0 when omitted. */
        @DecimalMin(value = "0.0", inclusive = true, message = "Current amount must be zero or positive")
        private BigDecimal currentAmount;

        /** Optional; may be a URL or a data URL for uploaded images. */
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    public static class CreateFundraisingActivityResponse {
        private UUID id;
        private String title;
        private String description;
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
    @PostMapping("/create-fundraising-activity")
    @Transactional
    public CreateFundraisingActivityResponse createFundraisingActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFundraisingActivityRequest request) {
        // Create fundraising activity
        FundraisingActivityModel saved = fundraisingActivityRepository.createFundraisingActivity(userDetails.getUsername(),
                request);

        // Map saved fundraising activity to response
        return modelMapper.map(saved, CreateFundraisingActivityResponse.class);
    }
}
