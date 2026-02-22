package com.vbforge.projectstracker.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test Security Configuration - disables most security for tests
 * This is only loaded in test profile (@ActiveProfiles("test"))
 *
 * CRITICAL: This must provide PasswordEncoder bean because UserServiceImpl depends on it!
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Provide PasswordEncoder for UserServiceImpl
     * MUST be present or integration tests will fail with:
     * "No qualifying bean of type 'PasswordEncoder' available"
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Simplified security for tests - permit all requests
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
