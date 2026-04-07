package com.example.trustfundr_be.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trustfundr_be.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
	Optional<UserProfile> findByName(String name);

	Optional<UserProfile> findByNameIgnoreCase(String name);
}
