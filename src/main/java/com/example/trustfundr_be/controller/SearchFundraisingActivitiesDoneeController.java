package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
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

@Tag(name = "Donee - fundraising activities")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activities")
public class SearchFundraisingActivitiesDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchFundraisingActivitiesDoneeResponse {
        private UUID id;
        private String title;
        private String description;
        private String category;
        private String location;
        private BigDecimal goalAmount;
        private BigDecimal currentAmount;
        private String imageUrl;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/search-fundraising-activities")
    @Transactional(readOnly = true)
    public List<SearchFundraisingActivitiesDoneeResponse> searchFundraisingActivities(
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return fundraisingActivityRepository.searchAllPublic(q.trim()).stream()
                // Map fundraising activity to response
                .map(a -> modelMapper.map(a, SearchFundraisingActivitiesDoneeResponse.class))
                .toList();
    }
}
