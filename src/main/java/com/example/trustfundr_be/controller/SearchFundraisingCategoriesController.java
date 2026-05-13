package com.example.trustfundr_be.controller;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trustfundr_be.repository.FundraisingCategory;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Tag(name = "Fundraising categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/platform-management/fundraising-categories")
public class SearchFundraisingCategoriesController {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    private final FundraisingCategory fundraisingCategoryRepository;
    private final ModelMapper modelMapper;

    @Data
    @NoArgsConstructor
    public static class SearchFundraisingCategoriesResponse {
        private UUID id;
        private String name;
        private String description;
    }

    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    @PreAuthorize("hasRole('PLATFORM_MANAGEMENT')")
    @GetMapping("/search-fundraising-categories")
    @Transactional(readOnly = true)
    public List<SearchFundraisingCategoriesResponse> searchFundraisingCategories(
            @RequestParam("q") @NotBlank(message = "Search query is required") String q) {
        return fundraisingCategoryRepository
                .searchByKeyword(q.trim(), Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(c -> modelMapper.map(c, SearchFundraisingCategoriesResponse.class))
                .toList();
    }
}

