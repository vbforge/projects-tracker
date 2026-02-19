package com.vbforge.projectstracker.mapper;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.Tag;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Project entity and ProjectDTO
 */
@Component
public class ProjectMapper {

    private final TagMapper tagMapper;

    public ProjectMapper(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    /**
     * Convert Project entity to ProjectDTO
     *
     * @param project the entity
     * @return the DTO
     */
    public ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }

        Set<TagDTO> tagDTOs = project.getTags() != null
                ? project.getTags().stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toSet())
                : Set.of();

        Set<Long> tagIds = project.getTags() != null
                ? project.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet())
                : Set.of();

        Long daysSince = project.getLastWorkedOn() != null
                ? ChronoUnit.DAYS.between(project.getLastWorkedOn().toLocalDate(), LocalDate.now())
                : null;

        return ProjectDTO.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .onGithub(project.getOnGithub())
                .githubUrl(project.getGithubUrl())
                .localPath(project.getLocalPath())
                .whatTodo(project.getWhatTodo())
                .createdDate(project.getCreatedDate())
                .lastWorkedOn(project.getLastWorkedOn())
                .updatedAt(project.getUpdatedAt())
                .tags(tagDTOs)
                .tagIds(tagIds)
                .daysSinceLastWorked(daysSince)
                .build();
    }

    /**
     * Convert ProjectDTO to Project entity
     * NOTE: Owner and tags must be handled separately in the service layer!
     * This is by design - the owner should come from SecurityContext, not from DTO.
     *
     * @param dto the DTO
     * @return the entity (without owner or tags set)
     */
    public Project toEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }

        return Project.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .onGithub(dto.getOnGithub())
                .githubUrl(dto.getGithubUrl())
                .localPath(dto.getLocalPath())
                .whatTodo(dto.getWhatTodo())
                // NOTE: owner is NOT set here - it must be set in the service layer
                // NOTE: tags are NOT set here - they're handled separately in the service layer
                .build();
    }

    /**
     * Update existing Project entity from ProjectDTO
     * This method updates only the fields that should be updated,
     * preserving timestamps, relationships, and owner
     *
     * @param project the existing entity
     * @param dto the DTO with new values
     */
    public void updateEntityFromDTO(Project project, ProjectDTO dto) {
        if (project == null || dto == null) {
            return;
        }

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setOnGithub(dto.getOnGithub());
        project.setGithubUrl(dto.getGithubUrl());
        project.setLocalPath(dto.getLocalPath());
        project.setWhatTodo(dto.getWhatTodo());
        // Note: createdDate, lastWorkedOn, updatedAt are managed by @PrePersist/@PreUpdate
        // Note: tags are managed separately in the service layer
        // Note: owner is NOT updated - it's immutable
    }
}
