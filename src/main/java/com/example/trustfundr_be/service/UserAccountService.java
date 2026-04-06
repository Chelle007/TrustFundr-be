package com.example.trustfundr_be.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.entity.UserAccount;
import com.example.trustfundr_be.model.entity.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAccountService {

	private final UserAccountRepository userAccountRepository;
	private final UserProfileRepository userProfileRepository;
	private final PasswordEncoder passwordEncoder;


	private record AccountSeed(String profileName, String username, String fullName, String plainPassword) {
	}

	private static final List<AccountSeed> DEFAULT_ACCOUNTS = List.of(
			new AccountSeed("Admin", "admin", "Michelle Chan", "admin123"),
			new AccountSeed("Donee", "donee", "Vanness Yang", "donee123"),
			new AccountSeed("Fund Raiser", "fundraiser", "Jane", "fundraiser123"),
			new AccountSeed("Platform Management", "platform", "Endyano", "platform123"));

	public void seedUserAccounts() {
		for (AccountSeed seed : DEFAULT_ACCOUNTS) {
			userAccountRepository.findByUsername(seed.username()).orElseGet(() -> {
				UserProfile userProfile = userProfileRepository.findByName(seed.profileName())
						.orElseThrow(() -> new IllegalStateException(
								"User profile \"" + seed.profileName() + "\" must exist before seeding accounts"));

				UserAccount userAccount = new UserAccount();
				userAccount.setFullName(seed.fullName());
				userAccount.setUsername(seed.username());
				userAccount.setPasswordHashString(passwordEncoder.encode(seed.plainPassword()));
				userAccount.setUserProfile(userProfile);

				return userAccountRepository.save(userAccount);
			});
		}
	}
}
