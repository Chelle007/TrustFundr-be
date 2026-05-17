package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.FundraisingActivityFavourite;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activity favourites")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activity-favourites")
public class SaveFundraisingActivityFavouriteDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityFavourite fundraisingActivityFavouriteRepository;

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @PostMapping("/save-favourite/{activityId}")
    @Transactional
    public ResponseEntity<Void> saveFavourite(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID activityId) {
        fundraisingActivityFavouriteRepository.saveFavourite(userDetails.getUsername(), activityId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
