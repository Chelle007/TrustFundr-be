package com.example.trustfundr_be.app.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.trustfundr_be.service.UserProfileService;
import com.example.trustfundr_be.service.UserAccountService;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserProfileService userProfileService;
    private final UserAccountService userAccountService;

    @Override
    public void run(String... args) throws Exception {
        userProfileService.seedUserProfiles();
        userAccountService.seedUserAccounts();
    }

}
