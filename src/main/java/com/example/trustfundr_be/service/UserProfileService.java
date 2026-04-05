package com.example.trustfundr_be.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.trustfundr_be.repository.UserProfileRepository;
import com.example.trustfundr_be.model.UserProfile;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile seedUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setName("Admin");
        userProfile.setDescription("User admin profile");
        return userProfileRepository.save(userProfile);
    }

}
