package com.example.trustfundr_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.trustfundr_be.model.UserProfile;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    UserProfile findByName(String name);
}
