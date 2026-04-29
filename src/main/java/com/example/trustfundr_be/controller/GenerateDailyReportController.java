package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.DonationRepository;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Platform reports")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/platform-management/reports")
public class GenerateDailyReportController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final DonationRepository donationRepository;

    @Data
    @NoArgsConstructor
    public static class GenerateDailyReportResponse {
        private Instant startAt;
        private Instant endAt;

        private long newFundraisingActivities;
        private long completedFundraisingActivities;

        private long totalDonations;
        private BigDecimal totalDonationAmount;

        // Aggregates over activities created in the reporting window
        private long totalViews;
        private long totalFavourites;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('PLATFORM_MANAGEMENT')")
    @GetMapping("/generate-daily-report")
    @Transactional(readOnly = true)
    public GenerateDailyReportResponse generateDailyReport(@RequestParam("date") Instant date) {
        LocalDate localDate = date.atZone(ZoneOffset.UTC).toLocalDate();
        Instant startAt = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endAt = startAt.plusSeconds(24L * 60L * 60L);

        // Fundraising activity aggregates
        long newActivities = fundraisingActivityRepository.countCreatedBetween(startAt, endAt);
        long completedActivities = fundraisingActivityRepository.countCompletedBetween(startAt, endAt);
        long totalViews = fundraisingActivityRepository.sumViewCountCreatedBetween(startAt, endAt);
        long totalFavourites = fundraisingActivityRepository.sumFavouriteCountCreatedBetween(startAt, endAt);

        // Donation aggregates
        long donationCount = donationRepository.countDonationsBetween(startAt, endAt);
        BigDecimal donationAmount = donationRepository.sumDonationAmountBetween(startAt, endAt);

        GenerateDailyReportResponse response = new GenerateDailyReportResponse();
        response.setStartAt(startAt);
        response.setEndAt(endAt);

        response.setNewFundraisingActivities(newActivities);
        response.setCompletedFundraisingActivities(completedActivities);

        response.setTotalDonations(donationCount);
        response.setTotalDonationAmount(donationAmount);

        response.setTotalViews(totalViews);
        response.setTotalFavourites(totalFavourites);
        return response;
    }
}

