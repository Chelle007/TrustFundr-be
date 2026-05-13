package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.dto.PlatformReportDonationRowDto;
import com.example.trustfundr_be.repository.Donation;
import com.example.trustfundr_be.repository.FundraisingActivity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Platform reports")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/platform-management/reports")
public class GenerateMonthlyReportController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";
    private static final int REPORT_DETAIL_LIMIT = 25;

    private final FundraisingActivity fundraisingActivityRepository;
    private final Donation donationRepository;

    @Data
    @NoArgsConstructor
    public static class GenerateMonthlyReportResponse {
        private Instant startAt;
        private Instant endAt;

        private long newFundraisingActivities;
        private long completedFundraisingActivities;

        private long totalDonations;
        private BigDecimal totalDonationAmount;

        private long totalViews;
        private long totalFavourites;

        private List<PlatformReportDonationRowDto> topDonations;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('PLATFORM_MANAGEMENT')")
    @GetMapping("/generate-monthly-report")
    @Transactional(readOnly = true)
    public GenerateMonthlyReportResponse generateMonthlyReport(@RequestParam("month") @NotBlank String month) {
        YearMonth ym = YearMonth.parse(month.trim());
        Instant startAt = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endAt = ym.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        long newActivities = fundraisingActivityRepository.countCreatedBetween(startAt, endAt);
        long completedActivities = fundraisingActivityRepository.countCompletedBetween(startAt, endAt);
        long totalViews = fundraisingActivityRepository.sumViewCountCreatedBetween(startAt, endAt);
        long totalFavourites = fundraisingActivityRepository.sumFavouriteCountCreatedBetween(startAt, endAt);

        long donationCount = donationRepository.countDonationsBetween(startAt, endAt);
        BigDecimal donationAmount = donationRepository.sumDonationAmountBetween(startAt, endAt);

        GenerateMonthlyReportResponse response = new GenerateMonthlyReportResponse();
        response.setStartAt(startAt);
        response.setEndAt(endAt);

        response.setNewFundraisingActivities(newActivities);
        response.setCompletedFundraisingActivities(completedActivities);

        response.setTotalDonations(donationCount);
        response.setTotalDonationAmount(donationAmount);

        response.setTotalViews(totalViews);
        response.setTotalFavourites(totalFavourites);

        List<PlatformReportDonationRowDto> topRows = donationRepository
                .findTopDonationsBetween(startAt, endAt, PageRequest.of(0, REPORT_DETAIL_LIMIT))
                .stream()
                .map(PlatformReportDonationRowDto::fromEntity)
                .toList();
        response.setTopDonations(topRows);
        return response;
    }
}

