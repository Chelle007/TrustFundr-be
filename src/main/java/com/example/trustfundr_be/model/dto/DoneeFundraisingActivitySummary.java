package com.example.trustfundr_be.model.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

// Shared summary shape for fundraising activity in donee favourites and donation responses.
@Data
@NoArgsConstructor
public class DoneeFundraisingActivitySummary {
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
