package com.example.trustfundr_be.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.repository.FundraisingActivity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising activities")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fundraiser/fundraising-activities")
public class ViewMyFundraisingActivitiesController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";
    private static final String STATUS_ALL = "all";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_COMPLETED = "completed";

    private final FundraisingActivity fundraisingActivityRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class ViewMyFundraisingActivitiesResponse {
        private UUID id;
        private String title;
        private String description;
        private UUID categoryId;
        private String category;
        private String location;
        private BigDecimal goalAmount;
        private BigDecimal currentAmount;
        private String imageUrl;
        private long viewCount;
        private long favouriteCount;
        private Instant completedAt;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('FUNDRAISER')")
    @GetMapping("/view-my-fundraising-activities")
    @Transactional(readOnly = true)
    public List<ViewMyFundraisingActivitiesResponse> viewMyFundraisingActivities(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "status", required = false, defaultValue = STATUS_ALL) String status) {
        String s = status == null ? STATUS_ALL : status.trim().toLowerCase();
        if (!STATUS_ALL.equals(s) && !STATUS_ACTIVE.equals(s) && !STATUS_COMPLETED.equals(s)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "status must be \"" + STATUS_ALL + "\", \"" + STATUS_ACTIVE + "\", or \"" + STATUS_COMPLETED + "\"");
        }

        Stream<FundraisingActivityModel> stream;
        if (STATUS_COMPLETED.equals(s)) {
            stream = fundraisingActivityRepository
                    .findCompletedByOwnerUsernameOrderByCompletedAtDesc(userDetails.getUsername())
                    .stream();
        } else if (STATUS_ACTIVE.equals(s)) {
            stream = fundraisingActivityRepository
                    .findActiveByOwnerUsernameOrderByCreatedAtDesc(userDetails.getUsername())
                    .stream();
        } else {
            // Active first, then completed (newest completion last within completed group)
            stream = Stream.concat(
                    fundraisingActivityRepository
                            .findActiveByOwnerUsernameOrderByCreatedAtDesc(userDetails.getUsername())
                            .stream(),
                    fundraisingActivityRepository
                            .findCompletedByOwnerUsernameOrderByCompletedAtDesc(userDetails.getUsername())
                            .stream());
        }

        return stream.map(a -> modelMapper.map(a, ViewMyFundraisingActivitiesResponse.class)).toList();
    }
}
