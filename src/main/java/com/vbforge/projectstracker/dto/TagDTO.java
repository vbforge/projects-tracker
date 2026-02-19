package com.vbforge.projectstracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {

    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(min = 1, max = 100, message = "Tag name must be between 1 and 100 characters")
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code (e.g., #e7f3ff)")
    @Builder.Default
    private String color = "#e7f3ff";

    @Size(max = 250, message = "Description must not exceed 250 characters")
    private String description;

    // Read-only fields from entity (used in tags list view)
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;
    private Integer projectCount;

    public Integer getProjectCount() {
        return projectCount;
    }
}
