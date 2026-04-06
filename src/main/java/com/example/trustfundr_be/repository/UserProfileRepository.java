package com.example.trustfundr_be.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trustfundr_be.model.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
	UserProfile findByName(String name);
}
