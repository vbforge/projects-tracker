package com.vbforge.projectstracker.dto;

import com.vbforge.projectstracker.entity.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Project
 * Used for receiving data from forms and sending data to views
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Project title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Project status is required")
    private ProjectStatus status;

    @Builder.Default
    private Boolean onGithub = false;

    @Pattern(
            regexp = "^(https?://)?(www\\.)?(github\\.com)/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/?$",
            message = "Please enter a valid GitHub repository URL (e.g., https://github.com/username/repo)",
            groups = GithubValidation.class
    )
    private String githubUrl;

    @Size(max = 500, message = "Local path cannot exceed 500 characters")
    private String localPath;

    @Size(max = 2000, message = "What to do cannot exceed 2000 characters")
    private String whatTodo;

    private LocalDateTime createdDate;

    private LocalDateTime lastWorkedOn;

    private LocalDateTime updatedAt;

    // Tag IDs for many-to-many relationship
    @Builder.Default
    private Set<Long> tagIds = new HashSet<>();

    // Tag details for display (populated when reading)
    @Builder.Default
    private Set<TagDTO> tags = new HashSet<>();

    // Computed field for display
    private Long daysSinceLastWorked;

    /**
     * Validation group for GitHub URL validation
     * Only validates GitHub URL when onGithub is true
     */
    public interface GithubValidation {}

    /**
     * Custom validation method called before processing
     * Validates GitHub URL only when onGithub is true
     */
    @AssertTrue(message = "GitHub URL is required when project is marked as 'On GitHub'", groups = GithubValidation.class)
    public boolean isGithubUrlValid() {
        if (Boolean.TRUE.equals(onGithub)) {
            return githubUrl != null && !githubUrl.trim().isEmpty();
        }
        return true;
    }
}
