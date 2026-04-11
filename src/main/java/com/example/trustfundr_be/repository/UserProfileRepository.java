package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.trustfundr_be.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
	Optional<UserProfile> findByName(String name);

	Optional<UserProfile> findByNameIgnoreCase(String name);

	@Query("SELECT u FROM UserProfile u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))"
			+ " OR (u.description IS NOT NULL AND LOWER(u.description) LIKE LOWER(CONCAT('%', :q, '%')))")
	List<UserProfile> searchByKeyword(@Param("q") String q, Sort sort);
}
