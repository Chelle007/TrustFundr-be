package com.example.trustfundr_be.app.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.trustfundr_be.seeder.UserAccountSeeder;
import com.example.trustfundr_be.seeder.UserProfileSeeder;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserProfileSeeder userProfileSeeder;
    private final UserAccountSeeder userAccountSeeder;

    @Override
    public void run(String... args) throws Exception {
        userProfileSeeder.seedUserProfiles();
        userAccountSeeder.seedUserAccounts();
    }

}
