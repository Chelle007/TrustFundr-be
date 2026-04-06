package com.example.trustfundr_be.service;

import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.entity.UserAccount;
import com.example.trustfundr_be.model.entity.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;
import com.example.trustfundr_be.repository.UserProfileRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAccountService {
	private final UserAccountRepository userAccountRepository;
	private final UserProfileRepository userProfileRepository;
	private final PasswordEncoder passwordEncoder;
	
    public UserAccount seedUserAccount() {
		UserAccount userAccount = new UserAccount();
		UserProfile userProfile = userProfileRepository.findByName("Admin");
		userAccount.setFullName("Michelle Chan");
		userAccount.setUsername("admin");
		userAccount.setPasswordHashString(passwordEncoder.encode("admin123"));
		userAccount.setUserProfile(userProfile);
		return userAccountRepository.save(userAccount);
	}

}
