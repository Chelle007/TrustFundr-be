package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.exception.FundraisingActivityException;
import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activities")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activities")
public class ViewFundraisingActivityDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class ViewFundraisingActivityDoneeResponse {
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
        private String ownerFullName;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('DONEE')")
    @GetMapping("/view-fundraising-activity/{id}")
    @Transactional
    public ViewFundraisingActivityDoneeResponse viewFundraisingActivity(@PathVariable UUID id) {
        // Load activity with fundraiser (owner) for detail view
        FundraisingActivity activity = fundraisingActivityRepository.findByIdWithOwner(id)
                .orElseThrow(() -> new FundraisingActivityException(HttpStatus.NOT_FOUND,
                        "Fundraising activity not found"));

        // Count a view when a donee opens the activity detail (denormalized counter on the activity)
        fundraisingActivityRepository.incrementViewCountById(id);

        // Map fundraising activity to response
        ViewFundraisingActivityDoneeResponse response = modelMapper.map(activity, ViewFundraisingActivityDoneeResponse.class);
        if (activity.getOwner() != null) {
            response.setOwnerFullName(activity.getOwner().getFullName());
        }
        return response;
    }
}
