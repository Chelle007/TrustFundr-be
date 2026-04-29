package com.example.trustfundr_be.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.trustfundr_be.app.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v1/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private static final String[] GET_WHITELIST = {
    };

    private static final String[] POST_WHITELIST = {
            "/api/auth/login",
            "/api/auth/logout"
    };

    /** Role-based API prefixes. */
    private static final String[] ADMIN_API_PATHS = {
            "/api/admin/**"
    };
    private static final String[] DONEE_API_PATHS = {
            "/api/donee/**"
    };
    private static final String[] FUNDRAISER_API_PATHS = {
            "/api/fundraiser/**"
    };
    private static final String[] PLATFORM_MANAGEMENT_API_PATHS = {
            "/api/platform-management/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(GET_WHITELIST).permitAll()
                        .requestMatchers(POST_WHITELIST).permitAll()
                        .requestMatchers(ADMIN_API_PATHS).hasRole("ADMIN")
                        .requestMatchers(DONEE_API_PATHS).hasRole("DONEE")
                        .requestMatchers(FUNDRAISER_API_PATHS).hasRole("FUNDRAISER")
                        .requestMatchers(PLATFORM_MANAGEMENT_API_PATHS).hasRole("PLATFORM_MANAGEMENT")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
