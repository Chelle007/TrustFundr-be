package com.example.trustfundr_be.seeder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class FundraisingActivitySeeder {

    private static final int TARGET_COUNT = 100;

    private final FundraisingActivityRepository fundraisingActivityRepository;
    private final UserAccountRepository userAccountRepository;
    private final Faker faker;

    public void seedFundraisingActivities() {
        long current = fundraisingActivityRepository.count();
        if (current >= 10) {
            return;
        }

        List<UserAccount> owners = new ArrayList<>(userAccountRepository.findAll());
        if (owners.isEmpty()) {
            throw new IllegalStateException("No user accounts found; cannot seed fundraising activities");
        }

        int remaining = (int) (TARGET_COUNT - current);
        for (int i = 0; i < remaining; i++) {
            FundraisingActivity act = new FundraisingActivity();
            act.setTitle(faker.book().title());
            act.setDescription(faker.lorem().paragraph(4));
            act.setOwner(owners.get(i % owners.size()));
            act.setViewCount(ThreadLocalRandom.current().nextLong(0, 10_000));
            act.setFavouriteCount(0); // will be incremented by Favourite seeder via repository method
            act.setCompletedAt(randomCompletedAtOrNull());
            fundraisingActivityRepository.save(act);
        }
    }

    private Instant randomCompletedAtOrNull() {
        // ~25% completed; rest active (null)
        if (ThreadLocalRandom.current().nextInt(4) != 0) {
            return null;
        }
        Instant now = Instant.now();
        long daysAgo = ThreadLocalRandom.current().nextLong(1, 365);
        return now.minus(daysAgo, ChronoUnit.DAYS);
    }
}

