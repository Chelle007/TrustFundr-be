package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.FundraisingCategoryRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/platform-management/fundraising-categories")
public class SuspendFundraisingCategoryController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingCategoryRepository fundraisingCategoryRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SuspendFundraisingCategoryResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('PLATFORM_MANAGEMENT')")
    @PostMapping("/suspend-fundraising-category/{id}")
    @Transactional
    public SuspendFundraisingCategoryResponse suspendFundraisingCategory(@PathVariable UUID id) {
        return modelMapper.map(
                fundraisingCategoryRepository.suspendFundraisingCategory(id),
                SuspendFundraisingCategoryResponse.class);
    }
}

