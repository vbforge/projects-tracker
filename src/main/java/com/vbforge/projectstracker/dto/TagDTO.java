package com.vbforge.projectstracker.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Tag
 * Used for receiving data from forms and sending data to views
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {

    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 100, message = "Tag name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9\\s\\-\\.#+]+$",
            message = "Tag name can only contain letters, numbers, spaces, hyphens, dots, hash, and plus signs"
    )
    private String name;

    @NotBlank(message = "Color is required")
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Color must be a valid hex color code (e.g., #FF5733 or #F57)"
    )
    @Builder.Default
    private String color = "#e7f3ff";

    @Size(max = 250, message = "Description cannot exceed 250 characters")
    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

    // Number of projects using this tag (for display purposes)
    private Integer projectCount;
}
