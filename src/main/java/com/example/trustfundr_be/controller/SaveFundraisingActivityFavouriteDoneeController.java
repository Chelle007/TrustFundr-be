package com.example.trustfundr_be.controller;

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

import com.example.trustfundr_be.model.FundraisingActivityFavourite;
import com.example.trustfundr_be.model.dto.DoneeFundraisingActivitySummary;
import com.example.trustfundr_be.repository.FundraisingActivityFavouriteRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activity favourites")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activity-favourites")
public class SaveFundraisingActivityFavouriteDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityFavouriteRepository fundraisingActivityFavouriteRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SaveFundraisingActivityFavouriteDoneeResponse {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private DoneeFundraisingActivitySummary fundraisingActivity;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @PostMapping("/save-favourite/{activityId}")
    @Transactional
    public SaveFundraisingActivityFavouriteDoneeResponse saveFavourite(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID activityId) {
        // Save favourite (idempotent if already saved)
        FundraisingActivityFavourite saved = fundraisingActivityFavouriteRepository
                .saveFavourite(userDetails.getUsername(), activityId);

        // Map saved favourite to response
        return modelMapper.map(saved, SaveFundraisingActivityFavouriteDoneeResponse.class);
    }
}
