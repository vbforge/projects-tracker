package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.dto.RegisterDTO;
import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.repository.UserRepository;
import com.vbforge.projectstracker.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterDTO validRegisterDTO;

    @BeforeEach
    void setUp() {
        validRegisterDTO = new RegisterDTO();
        validRegisterDTO.setUsername("newuser");
        validRegisterDTO.setEmail("newuser@example.com");
        validRegisterDTO.setPassword("password123");
        validRegisterDTO.setConfirmPassword("password123");
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .enabled(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.register(validRegisterDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("newuser@example.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.isEnabled()).isTrue();

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.register(validRegisterDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username 'newuser' is already taken");

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.register(validRegisterDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email 'newuser@example.com' is already registered");

        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when passwords do not match")
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        // Given
        validRegisterDTO.setConfirmPassword("differentPassword");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> userService.register(validRegisterDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passwords do not match");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.register(validRegisterDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("$2a$10$encodedHash");
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should set USER role by default")
    void shouldSetUserRoleByDefault() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.register(validRegisterDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Should enable user by default")
    void shouldEnableUserByDefault() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.register(validRegisterDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        assertThat(userCaptor.getValue().isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return empty when user not found by username")
    void shouldReturnEmptyWhenUserNotFoundByUsername() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // Given
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When/Then
        assertThat(userService.existsByUsername("existing")).isTrue();
        assertThat(userService.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Given
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        // When/Then
        assertThat(userService.existsByEmail("existing@example.com")).isTrue();
        assertThat(userService.existsByEmail("new@example.com")).isFalse();
    }
}
