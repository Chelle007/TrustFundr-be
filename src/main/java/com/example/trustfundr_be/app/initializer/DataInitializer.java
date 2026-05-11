package com.example.trustfundr_be.app.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.example.trustfundr_be.seeder.UserAccountSeeder;
import com.example.trustfundr_be.seeder.UserProfileSeeder;
import com.example.trustfundr_be.seeder.FundraisingCategorySeeder;
import com.example.trustfundr_be.seeder.FundraisingActivitySeeder;
import com.example.trustfundr_be.seeder.DonationSeeder;
import com.example.trustfundr_be.seeder.FundraisingActivityFavouriteSeeder;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserProfileSeeder userProfileSeeder;
    private final UserAccountSeeder userAccountSeeder;
    private final FundraisingCategorySeeder fundraisingCategorySeeder;
    private final FundraisingActivitySeeder fundraisingActivitySeeder;
    private final DonationSeeder donationSeeder;
    private final FundraisingActivityFavouriteSeeder fundraisingActivityFavouriteSeeder;

    @Override
    public void run(String... args) throws Exception {
        userProfileSeeder.seedUserProfiles();
        userAccountSeeder.seedUserAccounts();
        fundraisingCategorySeeder.seedFundraisingCategories();
        fundraisingActivitySeeder.seedFundraisingActivities();
        donationSeeder.seedDonations();
        fundraisingActivityFavouriteSeeder.seedFavourites();
    }

}
