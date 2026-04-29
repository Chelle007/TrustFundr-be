package com.example.trustfundr_be.seeder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.model.UserProfile;
import com.example.trustfundr_be.repository.UserAccountRepository;
import com.example.trustfundr_be.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class UserAccountSeeder {

    private static final int TARGET_COUNT = 100;

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;

    private record AccountSeed(String profileName, String username, String fullName, String plainPassword) {
    }

    private static final List<AccountSeed> DEFAULT_ACCOUNTS = List.of(
            new AccountSeed("Admin", "admin", "Michelle Chan", "admin123"),
            new AccountSeed("Donee", "donee", "Vanness Yang", "donee123"),
            new AccountSeed("Fund Raiser", "fundraiser", "Jane", "fundraiser123"),
            new AccountSeed("Platform Management", "platform", "Endyano", "platform123"));

    private record ProfileSeed(String name, String description) {
    }

    private static final List<ProfileSeed> REQUIRED_PROFILES = List.of(
            new ProfileSeed("Admin", "User admin profile"),
            new ProfileSeed("Donee", "Recipient or beneficiary of raised funds"),
            new ProfileSeed("Fund Raiser", "Creates and manages fundraising campaigns"),
            new ProfileSeed("Platform Management", "Platform administration and oversight"));

    public void seedUserAccounts() {
        if (userAccountRepository.count() >= 10) {
            return;
        }
        ensureRequiredProfilesExist();
        seedDefaultAccounts();
        seedFakerAccountsUpToTarget();
    }

    private void ensureRequiredProfilesExist() {
        for (ProfileSeed seed : REQUIRED_PROFILES) {
            userProfileRepository.findByNameIgnoreCase(seed.name()).orElseGet(() -> {
                UserProfile profile = new UserProfile();
                profile.setName(seed.name());
                profile.setDescription(seed.description());
                return userProfileRepository.save(profile);
            });
        }
    }

    private void seedDefaultAccounts() {
        for (AccountSeed seed : DEFAULT_ACCOUNTS) {
            userAccountRepository.findByUsernameIgnoreCase(seed.username()).orElseGet(() -> {
                UserProfile userProfile = userProfileRepository.findByNameIgnoreCase(seed.profileName())
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

    private void seedFakerAccountsUpToTarget() {
        long current = userAccountRepository.count();
        if (current >= TARGET_COUNT) {
            return;
        }

        List<UserProfile> profiles = new ArrayList<>(userProfileRepository.findAll());
        if (profiles.isEmpty()) {
            throw new IllegalStateException("No user profiles found; cannot seed user accounts");
        }

        int remaining = (int) (TARGET_COUNT - current);
        for (int i = 0; i < remaining; i++) {
            UserAccount account = new UserAccount();
            account.setFullName(faker.name().fullName());
            account.setUsername(generateUniqueUsername());
            account.setPasswordHashString(passwordEncoder.encode("password123"));
            account.setUserProfile(profiles.get(i % profiles.size()));
            userAccountRepository.save(account);
        }
    }

    private String generateUniqueUsername() {
        // keep it simple, URL-safe, and under common username length limits
        for (int attempts = 0; attempts < 50; attempts++) {
            String candidate = (faker.credentials().username() + faker.number().digits(4))
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9_]", "");
            if (candidate.isBlank()) {
                continue;
            }
            Optional<UserAccount> existing = userAccountRepository.findByUsernameIgnoreCase(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
        }
        // fallback: extremely unlikely to collide
        return ("user" + System.nanoTime()).toLowerCase(Locale.ROOT);
    }
}

