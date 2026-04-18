package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.controller.UpdateUserProfileController;
import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>, UserProfileRepositoryCustom {

    Optional<UserProfile> findByName(String name);

    Optional<UserProfile> findByNameIgnoreCase(String name);

    @Query("SELECT u FROM UserProfile u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))"
            + " OR (u.description IS NOT NULL AND LOWER(u.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<UserProfile> searchByKeyword(@Param("q") String q, Sort sort);
}

interface UserProfileRepositoryCustom {

    UserProfile updateUserProfile(UUID id, UpdateUserProfileController.UpdateUserProfileRequest request);

    UserProfile suspendUserProfile(UUID id);
}

class UserProfileRepositoryImpl implements UserProfileRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserProfile updateUserProfile(UUID id, UpdateUserProfileController.UpdateUserProfileRequest request) {
        UserProfile userProfile = entityManager.find(UserProfile.class, id);
        if (userProfile == null) {
            throw new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found");
        }
        userProfile.setName(request.getName());
        userProfile.setDescription(request.getDescription());
        entityManager.flush();
        return userProfile;
    }

    @Override
    public UserProfile suspendUserProfile(UUID id) {
        UserProfile userProfile = entityManager.find(UserProfile.class, id);
        if (userProfile == null) {
            throw new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found");
        }
        userProfile.softDelete();
        entityManager.flush();
        return userProfile;
    }
}
