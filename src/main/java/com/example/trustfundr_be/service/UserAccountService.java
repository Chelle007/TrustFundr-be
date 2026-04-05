package com.example.trustfundr_be.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.trustfundr_be.repository.UserAccountRepository;
import com.example.trustfundr_be.repository.UserProfileRepository;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserAccount seedUserAccount() {
        UserAccount userAccount = new UserAccount();
        UserProfile userProfile = userProfileRepository.findByName("Admin");
        userAccount.setFullName("Michelle Chan");
        userAccount.setUsername("admin");
        userAccount.setPasswordHashString("admin");
        userAccount.setUserProfile(userProfile);
        return userAccountRepository.save(userAccount);
    }

}
