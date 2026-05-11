package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.dto.PageResponse;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activities")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activities")
public class ViewFundraisingActivitiesDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class ViewFundraisingActivitiesDoneeResponse {
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
    @GetMapping("/view-fundraising-activities")
    @Transactional(readOnly = true)
    public PageResponse<ViewFundraisingActivitiesDoneeResponse> viewFundraisingActivities(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FundraisingActivity> result = fundraisingActivityRepository.findAllPublicPage(pageable);
        return toPageResponse(result);
    }

    private PageResponse<ViewFundraisingActivitiesDoneeResponse> toPageResponse(
            Page<FundraisingActivity> page) {
        return new PageResponse<>(
                page.getContent().stream()
                        .map(a -> modelMapper.map(a, ViewFundraisingActivitiesDoneeResponse.class))
                        .toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize());
    }
}
