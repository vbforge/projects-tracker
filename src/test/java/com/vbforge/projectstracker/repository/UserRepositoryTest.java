package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hashedpassword")
                .role(Role.USER)
                .enabled(true)
                .build();
        user1 = userRepository.save(user1);
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckExistsByUsername() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void shouldEnforceUniqueUsername() {
        // Given
        User duplicate = User.builder()
                .username("testuser") // Same username
                .email("different@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    userRepository.save(duplicate);
                    userRepository.flush();
                }
        );
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmail() {
        // Given
        User duplicate = User.builder()
                .username("different")
                .email("test@example.com") // Same email
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.dao.DataIntegrityViolationException.class,
                () -> {
                    userRepository.save(duplicate);
                    userRepository.flush();
                }
        );
    }

    @Test
    @DisplayName("Should set createdAt on persist")
    void shouldSetCreatedAtOnPersist() {
        // When
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}