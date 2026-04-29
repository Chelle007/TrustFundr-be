package com.example.trustfundr_be.seeder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trustfundr_be.model.FundraisingActivity;
import com.example.trustfundr_be.model.FundraisingActivityFavourite;
import com.example.trustfundr_be.model.UserAccount;
import com.example.trustfundr_be.repository.FundraisingActivityFavouriteRepository;
import com.example.trustfundr_be.repository.FundraisingActivityRepository;
import com.example.trustfundr_be.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FundraisingActivityFavouriteSeeder {

    private static final int TARGET_COUNT = 100;

    private final FundraisingActivityFavouriteRepository favouriteRepository;
    private final UserAccountRepository userAccountRepository;
    private final FundraisingActivityRepository fundraisingActivityRepository;

    @Transactional
    public void seedFavourites() {
        long current = favouriteRepository.count();
        if (current >= 10) {
            return;
        }

        List<UserAccount> donees = new ArrayList<>(userAccountRepository.findAll());
        List<FundraisingActivity> activities = new ArrayList<>(fundraisingActivityRepository.findAll());
        if (donees.isEmpty() || activities.isEmpty()) {
            throw new IllegalStateException("Missing donees or activities; cannot seed favourites");
        }

        int remaining = (int) (TARGET_COUNT - current);
        int created = 0;
        int attempts = 0;
        Set<String> usedPairs = new HashSet<>();

        while (created < remaining && attempts < remaining * 20) {
            attempts++;
            UserAccount donee = donees.get(ThreadLocalRandom.current().nextInt(donees.size()));
            FundraisingActivity activity = activities.get(ThreadLocalRandom.current().nextInt(activities.size()));

            UUID doneeId = donee.getId();
            UUID activityId = activity.getId();
            if (doneeId == null || activityId == null) {
                continue;
            }

            String key = doneeId + ":" + activityId;
            if (!usedPairs.add(key)) {
                continue;
            }

            try {
                FundraisingActivityFavourite row = new FundraisingActivityFavourite();
                row.setDonee(donee);
                row.setFundraisingActivity(activity);
                favouriteRepository.save(row);
                fundraisingActivityRepository.incrementFavouriteCountById(activityId);
                created++;
            } catch (DataIntegrityViolationException ignored) {
                // unique constraint collisions are fine; just retry with another pair
            }
        }
    }
}

