package com.example.trustfundr_be.model.dto;

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
    private Instant createdAt;
    private Instant updatedAt;
}
