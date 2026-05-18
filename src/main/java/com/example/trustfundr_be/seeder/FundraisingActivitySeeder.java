package com.example.trustfundr_be.seeder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.FundraisingActivityModel;
import com.example.trustfundr_be.model.FundraisingCategoryModel;
import com.example.trustfundr_be.model.UserAccountModel;
import com.example.trustfundr_be.repository.FundraisingActivity;
import com.example.trustfundr_be.repository.FundraisingCategory;
import com.example.trustfundr_be.repository.UserAccount;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class FundraisingActivitySeeder {

    private static final int TARGET_COUNT = 100;
    /** Demo fundraiser login is username {@code fundraiser} / password {@code fundraiser123} — not guaranteed by random owner pick. */
    private static final String DEMO_FUNDRAISER_USERNAME = "fundraiser";

    private final FundraisingActivity fundraisingActivityRepository;
    private final UserAccount userAccountRepository;
    private final FundraisingCategory fundraisingCategoryRepository;
    private final Faker faker;

    public void seedFundraisingActivities() {
        long current = fundraisingActivityRepository.count();
        if (current < TARGET_COUNT) {
            List<UserAccountModel> owners = new ArrayList<>(userAccountRepository.findAll());
            List<FundraisingCategoryModel> categories = new ArrayList<>(fundraisingCategoryRepository.findAll());
            if (owners.isEmpty()) {
                throw new IllegalStateException("No user accounts found; cannot seed fundraising activities");
            }
            if (categories.isEmpty()) {
                throw new IllegalStateException("No fundraising categories found; cannot seed fundraising activities");
            }

            int remaining = (int) (TARGET_COUNT - current);
            for (int i = 0; i < remaining; i++) {
                FundraisingActivityModel act = new FundraisingActivityModel();
                act.setTitle(faker.book().title());
                act.setDescription(faker.lorem().paragraph(4));
                act.setFundraisingCategory(
                        categories.get(ThreadLocalRandom.current().nextInt(categories.size())));
                act.setLocation(faker.address().city());
                BigDecimal goal = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(5_000, 100_000));
                act.setGoalAmount(goal);
                act.setCurrentAmount(
                        goal.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0, 0.95)))
                                .setScale(2, RoundingMode.HALF_UP));
                act.setOwner(owners.get(i % owners.size()));
                act.setViewCount(ThreadLocalRandom.current().nextLong(0, 10_000));
                act.setFavouriteCount(0); // will be incremented by Favourite seeder via repository method
                act.setCompletedAt(randomCompletedAtOrNull());
                act.setImageUrl(sampleHeroImageUrl(current + i));
                fundraisingActivityRepository.save(act);
            }
        }
        backfillMissingActivityImages();
        ensureDemoFundraiserHasCompletedActivity();
    }

    /**
     * Ensures the seeded fundraiser account (username {@code fundraiser}, password {@code fundraiser123})
     * owns at least one completed fundraising activity ({@code completed_at} set). Bulk seeding assigns
     * owners and completion at random, so this is explicit — not Faker.
     */
    private void ensureDemoFundraiserHasCompletedActivity() {
        if (!fundraisingActivityRepository
                .findCompletedByOwnerUsernameOrderByCompletedAtDesc(DEMO_FUNDRAISER_USERNAME)
                .isEmpty()) {
            return;
        }
        UserAccountModel owner = userAccountRepository.findByUsernameIgnoreCase(DEMO_FUNDRAISER_USERNAME).orElse(null);
        if (owner == null) {
            return;
        }

        List<FundraisingActivityModel> active = fundraisingActivityRepository
                .findActiveByOwnerUsernameOrderByCreatedAtDesc(DEMO_FUNDRAISER_USERNAME);
        if (!active.isEmpty()) {
            FundraisingActivityModel activity = active.get(0);
            markCompleted(activity);
            fundraisingActivityRepository.save(activity);
            return;
        }

        List<FundraisingCategoryModel> categories = new ArrayList<>(fundraisingCategoryRepository.findAll());
        if (categories.isEmpty()) {
            return;
        }

        FundraisingActivityModel activity = new FundraisingActivityModel();
        activity.setTitle("Community Shelter Renovation (Demo)");
        activity.setDescription(
                "Seeded completed campaign for the fundraiser demo account — safe to use in demos and walkthroughs.");
        activity.setFundraisingCategory(categories.get(0));
        activity.setLocation("Sydney");
        BigDecimal goal = BigDecimal.valueOf(25_000);
        activity.setGoalAmount(goal);
        activity.setCurrentAmount(goal);
        activity.setOwner(owner);
        activity.setViewCount(420);
        activity.setFavouriteCount(0);
        activity.setImageUrl(sampleHeroImageUrl(1));
        markCompleted(activity);
        fundraisingActivityRepository.save(activity);
    }

    private static void markCompleted(FundraisingActivityModel act) {
        act.setCompletedAt(Instant.now().minus(45, ChronoUnit.DAYS));
        if (act.getGoalAmount() != null) {
            act.setCurrentAmount(act.getGoalAmount());
        }
    }

    /** Stable placeholder hero images for seeded campaigns (Lorem Picsum). */
    private static String sampleHeroImageUrl(long salt) {
        return String.format("https://picsum.photos/seed/trustfundr-%d/800/520", salt);
    }

    /**
     * One-time style fix for older seeded rows: assign a hero image when missing so donee/fundraiser UIs
     * can show thumbnails.
     */
    private void backfillMissingActivityImages() {
        final int pageSize = 50;
        final int maxPages = 10;
        for (int pageNum = 0; pageNum < maxPages; pageNum++) {
            var page = fundraisingActivityRepository.findMissingHeroImages(PageRequest.of(pageNum, pageSize));
            if (page.isEmpty()) {
                break;
            }
            for (FundraisingActivityModel a : page.getContent()) {
                String seed = a.getId().toString().replace("-", "");
                String key = (seed + "aaaaaaaaaaaa").substring(0, 12);
                a.setImageUrl("https://picsum.photos/seed/tf" + key + "/800/520");
                fundraisingActivityRepository.save(a);
            }
            if (!page.hasNext()) {
                break;
            }
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

