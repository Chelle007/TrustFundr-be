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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.dto.DoneeFundraisingActivitySummary;
import com.example.trustfundr_be.repository.Donation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - donations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/donations")
public class ViewMyDonationsDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final Donation donationRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class ViewMyDonationsDoneeResponse {
        private UUID id;
        private BigDecimal amount;
        private String memo;
        private Instant createdAt;
        private Instant updatedAt;
        private DoneeFundraisingActivitySummary fundraisingActivity;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/view-my-donations")
    @Transactional(readOnly = true)
    public List<ViewMyDonationsDoneeResponse> viewMyDonations(@AuthenticationPrincipal UserDetails userDetails) {
        // Load this donee's donation history (newest first) and map each to response
        return donationRepository.findByDonorUsernameForHistory(userDetails.getUsername()).stream()
                // Map donation to response
                .map(d -> modelMapper.map(d, ViewMyDonationsDoneeResponse.class))
                .toList();
    }
}
