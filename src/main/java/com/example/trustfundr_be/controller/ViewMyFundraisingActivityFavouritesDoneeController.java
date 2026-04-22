package com.example.trustfundr_be.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class ViewMyFundraisingActivityFavouritesDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityFavouriteRepository fundraisingActivityFavouriteRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class ViewMyFundraisingActivityFavouritesDoneeResponse {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private DoneeFundraisingActivitySummary fundraisingActivity;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/view-my-favourites")
    @Transactional(readOnly = true)
    public List<ViewMyFundraisingActivityFavouritesDoneeResponse> viewMyFavourites(
            @AuthenticationPrincipal UserDetails userDetails) {
        // Load this donee's saved favourites (newest first) and map each to response
        return fundraisingActivityFavouriteRepository
                .findAllByDoneeUsernameOrderByCreatedAtDesc(userDetails.getUsername())
                .stream()
                // Map favourite row to response
                .map(f -> modelMapper.map(f, ViewMyFundraisingActivityFavouritesDoneeResponse.class))
                .toList();
    }
}
