package com.example.trustfundr_be.seeder;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.trustfundr_be.model.FundraisingCategoryModel;
import com.example.trustfundr_be.repository.FundraisingCategory;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
@Service
public class FundraisingCategorySeeder {

    private static final int TARGET_COUNT = 100;

    private final FundraisingCategory fundraisingCategoryRepository;
    private final Faker faker;

    public void seedFundraisingCategories() {
        long current = fundraisingCategoryRepository.count();
        if (current < TARGET_COUNT) {
            int remaining = (int) (TARGET_COUNT - current);
            for (int i = 0; i < remaining; i++) {
                FundraisingCategoryModel category = new FundraisingCategoryModel();
                category.setName(generateUniqueName());
                category.setDescription(faker.lorem().sentence(12, 6));
                fundraisingCategoryRepository.save(category);
            }
        }
    }

    private String generateUniqueName() {
        for (int attempts = 0; attempts < 50; attempts++) {
            String candidate = faker.commerce().department() + " " + faker.commerce().productName();
            candidate = candidate.trim().replaceAll("\\s+", " ");
            if (candidate.isBlank()) {
                continue;
            }
            String finalCandidate = candidate;
            boolean exists = fundraisingCategoryRepository.findByNameIgnoreCase(finalCandidate).isPresent();
            if (!exists) {
                // capitalize nicely but keep original words
                return finalCandidate.substring(0, 1).toUpperCase(Locale.ROOT) + finalCandidate.substring(1);
            }
        }
        return "Category " + System.nanoTime();
    }
}

