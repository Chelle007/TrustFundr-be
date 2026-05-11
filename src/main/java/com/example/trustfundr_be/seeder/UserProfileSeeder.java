package com.example.trustfundr_be.seeder;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileSeeder {

    private final UserProfileRepository userProfileRepository;

    private record ProfileSeed(String name, String description) {
    }

    private static final List<ProfileSeed> DEFAULT_PROFILES = List.of(
            new ProfileSeed("Admin", "User admin profile"),
            new ProfileSeed("Donee", "Recipient or beneficiary of raised funds"),
            new ProfileSeed("Fund Raiser", "Creates and manages fundraising campaigns"),
            new ProfileSeed("Platform Manager", "Platform administration and oversight"));

    public void seedUserProfiles() {
        if (userProfileRepository.count() >= 10) {
            return;
        }
        for (ProfileSeed seed : DEFAULT_PROFILES) {
            userProfileRepository.findByName(seed.name()).orElseGet(() -> {

                UserProfile userProfile = new UserProfile();
                userProfile.setName(seed.name());
                userProfile.setDescription(seed.description());

                return userProfileRepository.save(userProfile);
            });
        }
    }
}

