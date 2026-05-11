package com.example.trustfundr_be.app.config;

import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.datafaker.Faker;

@Configuration
public class FakerConfig {

    @Bean
    Faker faker(@Value("${app.seeding.faker.seed:314159}") long seed) {
        return new Faker(Locale.ENGLISH, new Random(seed));
    }
}

