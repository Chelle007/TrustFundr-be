package com.example.trustfundr_be.service;

import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.entity.UserProfile;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

	public UserProfile seedUserProfile() {
		UserProfile userProfile = new UserProfile();
		userProfile.setName("Admin");
		userProfile.setDescription("User admin profile");
		return userProfileRepository.save(userProfile);
	}

}
