package com.example.trustfundr_be.seeder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trustfundr_be.model.DonationModel;
import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.Donation;
import com.example.trustfundr_be.repository.FundraisingActivity;
import com.example.trustfundr_be.repository.UserAccount;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class DonationSeeder {

    private static final int TARGET_COUNT = 100;
    /** Share of new seed rows that get a historical `created_at` (reports / donation history). */
    private static final double FRACTION_PAST_DONATIONS = 0.65;
    /** If the DB already has donations but almost none older than a week, nudge some rows back in time once. */
    private static final int MIN_HISTORICAL_DONATIONS = 5;
    private static final int BACKFILL_CAP = 40;
    /** Demo donee login is username {@code donee} / password {@code donee123} — not guaranteed by random donor pick. */
    private static final String DEMO_DONEE_USERNAME = "donee";

    private final Donation donationRepository;
    private final UserAccount userAccountRepository;
    private final FundraisingActivity fundraisingActivityRepository;
    private final Faker faker;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void seedDonations() {
        maybeBackfillSparseHistoricalTimestamps();

        long current = donationRepository.count();
        if (current < 10) {
            List<UserAccountModel> donors = new ArrayList<>(userAccountRepository.findAll());
            List<FundraisingActivityModel> activities = new ArrayList<>(fundraisingActivityRepository.findAll());
            if (donors.isEmpty() || activities.isEmpty()) {
                throw new IllegalStateException("Missing donors or activities; cannot seed donations");
            }

            int remaining = (int) (TARGET_COUNT - current);
            for (int i = 0; i < remaining; i++) {
                DonationModel d = new DonationModel();
                d.setDonor(donors.get(ThreadLocalRandom.current().nextInt(donors.size())));
                d.setFundraisingActivity(activities.get(ThreadLocalRandom.current().nextInt(activities.size())));
                d.setAmount(randomAmount());
                d.setMemo(ThreadLocalRandom.current().nextInt(5) == 0 ? null : faker.lorem().sentence(10));
                donationRepository.saveAndFlush(d);
                if (ThreadLocalRandom.current().nextDouble() < FRACTION_PAST_DONATIONS) {
                    applyBackdatedTimestamps(d.getId(), randomPastInstant());
                }
            }
        }

        ensureDemoDoneeHasAtLeastOneDonation();
    }

    /**
     * Ensures the seeded donee account (username {@code donee}, password {@code donee123}) has at least one
     * donation for demo donation history. Bulk seeding picks donors at random, so this is explicit — not Faker.
     */
    private void ensureDemoDoneeHasAtLeastOneDonation() {
        if (donationRepository.countByDonorUsername(DEMO_DONEE_USERNAME) > 0) {
            return;
        }
        UserAccountModel donee = userAccountRepository.findByUsernameIgnoreCase(DEMO_DONEE_USERNAME).orElse(null);
        if (donee == null) {
            return;
        }
        List<FundraisingActivityModel> activities = fundraisingActivityRepository.findAll();
        if (activities.isEmpty()) {
            return;
        }
        FundraisingActivityModel activity = activities.get(ThreadLocalRandom.current().nextInt(activities.size()));
        DonationModel d = new DonationModel();
        d.setDonor(donee);
        d.setFundraisingActivity(activity);
        d.setAmount(randomAmount());
        d.setMemo(null);
        donationRepository.saveAndFlush(d);
        applyBackdatedTimestamps(d.getId(), randomPastInstant());
    }

    private void maybeBackfillSparseHistoricalTimestamps() {
        long total = donationRepository.count();
        if (total == 0) {
            return;
        }
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long olderThanWeek = donationRepository.countByCreatedAtBefore(weekAgo);
        if (olderThanWeek >= MIN_HISTORICAL_DONATIONS) {
            return;
        }

        int limit = (int) Math.min(BACKFILL_CAP, total);
        @SuppressWarnings("unchecked")
        List<UUID> ids = entityManager
                .createNativeQuery("SELECT id FROM donations ORDER BY random() LIMIT :lim")
                .setParameter("lim", limit)
                .getResultList();

        for (UUID id : ids) {
            applyBackdatedTimestamps(id, randomPastInstant());
        }
    }

    private void applyBackdatedTimestamps(UUID id, Instant when) {
        entityManager
                .createNativeQuery(
                        "UPDATE donations SET created_at = :ts, updated_at = :ts WHERE id = :id")
                .setParameter("ts", when)
                .setParameter("id", id)
                .executeUpdate();
    }

    /** A few hours to ~400 days in the past (UTC clock on server). */
    private static Instant randomPastInstant() {
        long minSec = 2 * 3600L;
        long maxSec = 400L * 24 * 3600;
        long span = ThreadLocalRandom.current().nextLong(minSec, maxSec);
        return Instant.now().minusSeconds(span);
    }

    private BigDecimal randomAmount() {
        double raw = ThreadLocalRandom.current().nextDouble(5.0, 500.0);
        return BigDecimal.valueOf(raw).setScale(2, RoundingMode.HALF_UP);
    }
}
