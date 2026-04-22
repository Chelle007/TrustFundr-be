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

import com.example.trustfundr_be.model.dto.DoneeFundraisingActivitySummary;
import com.example.trustfundr_be.repository.DonationRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - donations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/donations")
public class SearchMyDonationsDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final DonationRepository donationRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchMyDonationsDoneeResponse {
        private UUID id;
        private BigDecimal amount;
        private String memo;
        private Instant createdAt;
        private Instant updatedAt;
        private DoneeFundraisingActivitySummary fundraisingActivity;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/search-my-donations")
    @Transactional(readOnly = true)
    public List<SearchMyDonationsDoneeResponse> searchMyDonations(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return donationRepository.searchDonationHistoryForDonee(userDetails.getUsername(), q.trim()).stream()
                // Map donation to response
                .map(d -> modelMapper.map(d, SearchMyDonationsDoneeResponse.class))
                .toList();
    }
}
