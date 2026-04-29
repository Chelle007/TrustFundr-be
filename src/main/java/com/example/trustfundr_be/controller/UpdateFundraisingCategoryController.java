package com.example.trustfundr_be.controller;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.FundraisingCategoryRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/platform-management/fundraising-categories")
public class UpdateFundraisingCategoryController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingCategoryRepository fundraisingCategoryRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class UpdateFundraisingCategoryRequest {

        @NotBlank(message = "Name is required")
        @Size(max = 255)
        private String name;

        @Size(max = 2000)
        private String description;
    }

    @Data
    @NoArgsConstructor
    public static class UpdateFundraisingCategoryResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('PLATFORM_MANAGEMENT')")
    @PutMapping("/update-fundraising-category/{id}")
    @Transactional
    public UpdateFundraisingCategoryResponse updateFundraisingCategory(@PathVariable UUID id,
            @Valid @RequestBody UpdateFundraisingCategoryRequest request) {
        return modelMapper.map(fundraisingCategoryRepository.updateFundraisingCategory(id, request),
                UpdateFundraisingCategoryResponse.class);
    }
}

