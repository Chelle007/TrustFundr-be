package com.example.trustfundr_be.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trustfundr_be.exception.UserProfileException;
import com.example.trustfundr_be.model.dto.CreateUserProfileRequest;
import com.example.trustfundr_be.model.dto.UserProfileResponse;
import com.example.trustfundr_be.model.entity.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private static final List<ProfileSeed> DEFAULT_PROFILES = List.of(
            new ProfileSeed("Admin", "User admin profile"),
            new ProfileSeed("Donee", "Recipient or beneficiary of raised funds"),
            new ProfileSeed("Fund Raiser", "Creates and manages fundraising campaigns"),
            new ProfileSeed("Platform Management", "Platform administration and oversight"));

    private record ProfileSeed(String name, String description) {
    }

    public void seedUserProfiles() {
        for (ProfileSeed seed : DEFAULT_PROFILES) {
            userProfileRepository.findByName(seed.name()).orElseGet(() -> {
                UserProfile userProfile = new UserProfile();
                userProfile.setName(seed.name());
                userProfile.setDescription(seed.description());
                return userProfileRepository.save(userProfile);
            });
        }
    }

    @Transactional
    public UserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        String name = request.getName().trim();
        if (name.isEmpty()) {
            throw new UserProfileException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        }
        if (userProfileRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new UserProfileException(HttpStatus.CONFLICT, "A user profile with this name already exists");
        }
        
        UserProfile userProfile = new UserProfile();
        userProfile.setName(name);
        userProfile.setDescription(request.getDescription());
        UserProfile saved = userProfileRepository.save(userProfile);

        return new UserProfileResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

}
