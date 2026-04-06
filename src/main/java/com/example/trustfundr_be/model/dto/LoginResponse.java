package com.example.trustfundr_be.model.dto;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
	private UUID id;
	private String fullName;
	private String username;
	private String token;

}
