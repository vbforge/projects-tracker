package com.vbforge.projectstracker.dto;

import com.vbforge.projectstracker.entity.ProjectStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Validation Tests")
class DTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ============ ProjectDTO Tests ============

    @Test
    @DisplayName("ProjectDTO - Should pass validation with valid data")
    void projectDTO_shouldPassValidationWithValidData() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .title("Valid Project Title")
                .description("Valid description")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(false)
                .build();

        // When
        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ProjectDTO - Should fail when title is blank")
    void projectDTO_shouldFailWhenTitleIsBlank() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .title("")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        // When
        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("title is required"));
    }

    @Test
    @DisplayName("ProjectDTO - Should fail when title is too short")
    void projectDTO_shouldFailWhenTitleIsTooShort() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .title("AB") // Less than 3 chars
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        // When
        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("ProjectDTO - Should fail when status is null")
    void projectDTO_shouldFailWhenStatusIsNull() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .title("Valid Title")
                .status(null)
                .build();

        // When
        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("status is required"));
    }

    // ============ TagDTO Tests ============

    @Test
    @DisplayName("TagDTO - Should pass validation with valid data")
    void tagDTO_shouldPassValidationWithValidData() {
        // Given
        TagDTO dto = TagDTO.builder()
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .build();

        // When
        Set<ConstraintViolation<TagDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TagDTO - Should fail when name is blank")
    void tagDTO_shouldFailWhenNameIsBlank() {
        // Given
        TagDTO dto = TagDTO.builder()
                .name("")
                .color("#28a745")
                .build();

        // When
        Set<ConstraintViolation<TagDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("name is required"));
    }

    @Test
    @DisplayName("TagDTO - Should fail when color is invalid format")
    void tagDTO_shouldFailWhenColorIsInvalidFormat() {
        // Given
        TagDTO dto = TagDTO.builder()
                .name("Valid Name")
                .color("invalid-color")
                .build();

        // When
        Set<ConstraintViolation<TagDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("valid hex code"));
    }

    @Test
    @DisplayName("TagDTO - Should pass with valid hex color")
    void tagDTO_shouldPassWithValidHexColor() {
        // Given
        TagDTO dto = TagDTO.builder()
                .name("Valid Name")
                .color("#abc123")
                .build();

        // When
        Set<ConstraintViolation<TagDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("TagDTO - Should fail when description exceeds max length")
    void tagDTO_shouldFailWhenDescriptionExceedsMaxLength() {
        // Given
        String longDescription = "a".repeat(251); // 251 chars
        TagDTO dto = TagDTO.builder()
                .name("Valid Name")
                .color("#28a745")
                .description(longDescription)
                .build();

        // When
        Set<ConstraintViolation<TagDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("must not exceed 250"));
    }

    // ============ RegisterDTO Tests ============

    @Test
    @DisplayName("RegisterDTO - Should pass validation with valid data")
    void registerDTO_shouldPassValidationWithValidData() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // When
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("RegisterDTO - Should fail when username is too short")
    void registerDTO_shouldFailWhenUsernameIsTooShort() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ab"); // Less than 3 chars
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // When
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("between 3 and 50"));
    }

    @Test
    @DisplayName("RegisterDTO - Should fail when email is invalid")
    void registerDTO_shouldFailWhenEmailIsInvalid() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("invalid-email");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // When
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("RegisterDTO - Should fail when email has no domain extension")
    void registerDTO_shouldFailWhenEmailHasNoDomainExtension() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("user@domain"); // No .com, .org, etc.
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // When
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("valid format"));
    }

    @Test
    @DisplayName("RegisterDTO - Should fail when password is too short")
    void registerDTO_shouldFailWhenPasswordIsTooShort() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("12345"); // Less than 6 chars
        dto.setConfirmPassword("12345");

        // When
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("at least 6 characters"));
    }

    @Test
    @DisplayName("RegisterDTO - Should detect password mismatch")
    void registerDTO_shouldDetectPasswordMismatch() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("differentpassword");

        // When
        boolean match = dto.passwordsMatch();

        // Then
        assertThat(match).isFalse();
    }

    @Test
    @DisplayName("RegisterDTO - Should confirm password match")
    void registerDTO_shouldConfirmPasswordMatch() {
        // Given
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // When
        boolean match = dto.passwordsMatch();

        // Then
        assertThat(match).isTrue();
    }
}