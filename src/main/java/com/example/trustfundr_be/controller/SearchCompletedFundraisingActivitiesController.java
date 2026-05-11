package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.FundraisingActivityRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising activities")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fundraiser/fundraising-activities")
public class SearchCompletedFundraisingActivitiesController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchCompletedFundraisingActivitiesResponse {
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
    @GetMapping("/search-completed-fundraising-activities")
    @Transactional(readOnly = true)
    public List<SearchCompletedFundraisingActivitiesResponse> searchCompletedFundraisingActivities(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return fundraisingActivityRepository
                .searchCompletedForOwner(userDetails.getUsername(), q.trim())
                .stream()
                // Map fundraising activity to response
                .map(a -> modelMapper.map(a, SearchCompletedFundraisingActivitiesResponse.class))
                .toList();
    }
}
