package com.example.trustfundr_be.controller;

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

import com.example.trustfundr_be.model.dto.DoneeFundraisingActivitySummary;
import com.example.trustfundr_be.model.dto.ImageUrlResponses;
import com.example.trustfundr_be.repository.FundraisingActivityFavourite;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activity favourites")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activity-favourites")
public class SearchMyFundraisingActivityFavouritesDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityFavourite fundraisingActivityFavouriteRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchMyFundraisingActivityFavouritesDoneeResponse {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private DoneeFundraisingActivitySummary fundraisingActivity;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/search-my-favourites")
    @Transactional(readOnly = true)
    public List<SearchMyFundraisingActivityFavouritesDoneeResponse> searchMyFavourites(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return fundraisingActivityFavouriteRepository
                .searchByDoneeUsername(userDetails.getUsername(), q.trim())
                .stream()
                .map(f -> {
                    SearchMyFundraisingActivityFavouritesDoneeResponse row =
                            modelMapper.map(f, SearchMyFundraisingActivityFavouritesDoneeResponse.class);
                    DoneeFundraisingActivitySummary activity = row.getFundraisingActivity();
                    if (activity != null) {
                        activity.setImageUrl(ImageUrlResponses.forBrowseList(activity.getImageUrl()));
                    }
                    return row;
                })
                .toList();
    }
}
