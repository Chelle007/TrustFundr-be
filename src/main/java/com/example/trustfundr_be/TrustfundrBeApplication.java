package com.example.trustfundr_be;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = "file:${user.dir}/.env", ignoreResourceNotFound = true) // FOR LOCAL NOT PRODUCTION
public class TrustfundrBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrustfundrBeApplication.class, args);
	}

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
