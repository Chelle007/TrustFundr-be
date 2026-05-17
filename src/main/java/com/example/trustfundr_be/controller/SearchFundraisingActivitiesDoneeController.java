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

import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.model.dto.ImageUrlResponses;
import com.example.trustfundr_be.model.dto.PageResponse;
import com.example.trustfundr_be.repository.FundraisingActivity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Donee - fundraising activities")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donee/fundraising-activities")
public class SearchFundraisingActivitiesDoneeController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingActivity fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchFundraisingActivitiesDoneeResponse {
        private UUID id;
        private String title;
        private String description;
        private UUID categoryId;
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
    @GetMapping("/search-fundraising-activities")
    @Transactional(readOnly = true)
    public PageResponse<SearchFundraisingActivitiesDoneeResponse> searchFundraisingActivities(
            @RequestParam("q") @NotBlank(message = "Search query is required") String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FundraisingActivityModel> result =
                fundraisingActivityRepository.searchAllPublicPage(q.trim(), pageable);
        return new PageResponse<>(
                result.getContent().stream()
                        .map(a -> {
                            SearchFundraisingActivitiesDoneeResponse row =
                                    modelMapper.map(a, SearchFundraisingActivitiesDoneeResponse.class);
                            row.setImageUrl(ImageUrlResponses.forBrowseList(row.getImageUrl()));
                            return row;
                        })
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize());
    }
}
