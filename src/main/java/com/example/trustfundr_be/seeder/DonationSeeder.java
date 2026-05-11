package com.example.trustfundr_be.seeder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.Donation;
import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.DonationRepository;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class DonationSeeder {

    private static final int TARGET_COUNT = 100;

    private final DonationRepository donationRepository;
    private final UserAccountRepository userAccountRepository;
    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final Faker faker;

    public void seedDonations() {
        long current = donationRepository.count();
        if (current >= 10) {
            return;
        }

        List<UserAccount> donors = new ArrayList<>(userAccountRepository.findAll());
        List<FundraisingActivity> activities = new ArrayList<>(fundraisingActivityRepository.findAll());
        if (donors.isEmpty() || activities.isEmpty()) {
            throw new IllegalStateException("Missing donors or activities; cannot seed donations");
        }

        int remaining = (int) (TARGET_COUNT - current);
        for (int i = 0; i < remaining; i++) {
            Donation d = new Donation();
            d.setDonor(donors.get(ThreadLocalRandom.current().nextInt(donors.size())));
            d.setFundraisingActivity(activities.get(ThreadLocalRandom.current().nextInt(activities.size())));
            d.setAmount(randomAmount());
            d.setMemo(ThreadLocalRandom.current().nextInt(5) == 0 ? null : faker.lorem().sentence(10));
            donationRepository.save(d);
        }
    }

    private BigDecimal randomAmount() {
        double raw = ThreadLocalRandom.current().nextDouble(5.0, 500.0);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }
}

