package com.example.trustfundr_be.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;
}
