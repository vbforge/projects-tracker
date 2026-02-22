package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.RegisterDTO;
import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Approach is kept @WebMvcTest (fast, isolated) and just excluded SecurityAutoConfiguration.
 * Best practice for controller tests:
 *  - Faster - No full context loading
 *  - Isolated - Only web layer
 *  - Clean - Disabled security for tests
 *  - Professional - Standard Spring Boot testing pattern
 * */
@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // mock dependency

    @Test
    @WithAnonymousUser
    @DisplayName("Should show login page when not authenticated")
    void shouldShowLoginPageWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/login")
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN",
                                        "_csrf",
                                        "fake-token"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("Should redirect to projects when already logged in")
    void shouldRedirectToProjectsWhenAlreadyLoggedIn() throws Exception {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        mockMvc.perform(get("/login")
                        .principal(auth)
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "fake-token")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show error message on login page when error parameter present")
    void shouldShowErrorMessageOnLoginPageWhenErrorParamPresent() throws Exception {

        mockMvc.perform(get("/login")
                        .param("error", "true")
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "fake-token")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage",
                        "Invalid username or password. Please try again."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show logout message when logout parameter present")
    void shouldShowLogoutMessageWhenLogoutParamPresent() throws Exception {

        mockMvc.perform(get("/login")
                        .param("logout", "true")
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "fake-token")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("logoutMessage"))
                .andExpect(model().attribute("logoutMessage",
                        "You have been logged out successfully."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show register page when not authenticated")
    void shouldShowRegisterPageWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/register")
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "fake-token")))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerDTO"));
    }

    @Test
    @DisplayName("Should redirect to projects when accessing register while logged in")
    void shouldRedirectToProjectsWhenAccessingRegisterWhileLoggedIn() throws Exception {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        mockMvc.perform(get("/register")
                        .principal(auth)
                        .requestAttr("_csrf",
                                new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "fake-token")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .role(Role.USER)
                .build();
        when(userService.register(any(RegisterDTO.class))).thenReturn(savedUser);

        // When/Then
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "Account created successfully! Please log in."));

        verify(userService).register(any(RegisterDTO.class));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show error when passwords do not match")
    void shouldShowErrorWhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "differentPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Passwords do not match."));

        verify(userService, never()).register(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show error when username already exists")
    void shouldShowErrorWhenUsernameAlreadyExists() throws Exception {
        // Given
        when(userService.register(any(RegisterDTO.class)))
                .thenThrow(new IllegalArgumentException("Username 'existing' is already taken"));

        // When/Then
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "existing")
                        .param("email", "new@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Username 'existing' is already taken"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show error when email already exists")
    void shouldShowErrorWhenEmailAlreadyExists() throws Exception {
        // Given
        when(userService.register(any(RegisterDTO.class)))
                .thenThrow(new IllegalArgumentException("Email 'existing@example.com' is already registered"));

        // When/Then
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "existing@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show validation errors when fields are blank")
    void shouldShowValidationErrorsWhenFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "")
                        .param("email", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "username", "email", "password", "confirmPassword"));

        verify(userService, never()).register(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show validation error when email format is invalid")
    void shouldShowValidationErrorWhenEmailFormatIsInvalid() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "invalid-email")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "email"));

        verify(userService, never()).register(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show validation error when username is too short")
    void shouldShowValidationErrorWhenUsernameIsTooShort() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "ab") // Less than 3 chars
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "username"));

        verify(userService, never()).register(any());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should show validation error when password is too short")
    void shouldShowValidationErrorWhenPasswordIsTooShort() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("email", "test@example.com")
                        .param("password", "12345") // Less than 6 chars
                        .param("confirmPassword", "12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerDTO", "password"));

        verify(userService, never()).register(any());
    }
}
