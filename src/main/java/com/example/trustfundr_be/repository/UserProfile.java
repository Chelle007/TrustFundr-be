package com.example.trustfundr_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import com.example.trustfundr_be.controller.CreateUserProfileController;
import com.example.trustfundr_be.controller.UpdateUserProfileController;
import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.UserProfileModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public interface UserProfile extends JpaRepository<UserProfileModel, UUID>, UserProfileCustom {

    Optional<UserProfileModel> findByName(String name);

    Optional<UserProfileModel> findByNameIgnoreCase(String name);

    @Query("SELECT u FROM UserProfileModel u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))"
            + " OR (u.description IS NOT NULL AND LOWER(u.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<UserProfileModel> searchByKeyword(@Param("q") String q, Sort sort);
}

interface UserProfileCustom {

    UserProfileModel updateUserProfile(UUID id, UpdateUserProfileController.UpdateUserProfileRequest request);

    UserProfileModel suspendUserProfile(UUID id);

    UserProfileModel createUserProfile(CreateUserProfileController.CreateUserProfileRequest request);
}

class UserProfileImpl implements UserProfileCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;
    private final UserProfile userProfiles;

    UserProfileImpl(
            ModelMapper modelMapper,
            @Lazy UserProfile userProfiles) {
        this.modelMapper = modelMapper;
        this.userProfiles = userProfiles;
    }

    @Override
    public UserProfileModel updateUserProfile(UUID id, UpdateUserProfileController.UpdateUserProfileRequest request) {
        UserProfileModel userProfile = entityManager.find(UserProfileModel.class, id);
        if (userProfile == null) {
            throw new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found");
        }
        modelMapper.map(request, userProfile);
        entityManager.flush();
        return userProfile;
    }

    @Override
    public UserProfileModel suspendUserProfile(UUID id) {
        UserProfileModel userProfile = entityManager.find(UserProfileModel.class, id);
        if (userProfile == null) {
            throw new UserProfileException(HttpStatus.NOT_FOUND, "User profile not found");
        }
        userProfile.softDelete();
        entityManager.flush();
        return userProfile;
    }

    @Override
    public UserProfileModel createUserProfile(CreateUserProfileController.CreateUserProfileRequest request) {
        UserProfileModel userProfile = modelMapper.map(request, UserProfileModel.class);
        return userProfiles.save(userProfile);
    }
}
