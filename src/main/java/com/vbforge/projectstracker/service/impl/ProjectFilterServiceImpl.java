package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.service.ProjectFilterService;
import com.vbforge.projectstracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProjectFilterService
 * Centralizes all project filtering and sorting logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFilterServiceImpl implements ProjectFilterService {

    private final ProjectService projectService;

    @Override
    public List<Project> getFilteredAndSortedProjects(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth,
            String sortBy) {

        log.debug("Filtering projects - search: {}, status: {}, onGithub: {}, tags: {}, createdMonth: {}, lastWorkedMonth: {}, sortBy: {}",
                search, status, onGithub, tags, createdMonth, lastWorkedMonth, sortBy);

        // Remove duplicate tags if present
        if (tags != null && !tags.isEmpty()) {
            tags = tags.stream().distinct().collect(Collectors.toList());
        }

        // STEP 1: Apply filters to get projects
        List<Project> projects = applyFilters(search, status, onGithub, tags, createdMonth, lastWorkedMonth);

        // STEP 2: Apply sorting
        projects = applySorting(projects, sortBy);

        log.debug("Filtered and sorted {} projects", projects.size());

        return projects;
    }

    @Override
    public boolean hasActiveFilters(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth) {

        return (search != null && !search.isEmpty()) ||
                status != null ||
                onGithub != null ||
                (tags != null && !tags.isEmpty()) ||
                (createdMonth != null && !createdMonth.isEmpty()) ||
                (lastWorkedMonth != null && !lastWorkedMonth.isEmpty());
    }

    @Override
    public String buildFilterDescription(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth,
            String sortBy) {

        StringBuilder description = new StringBuilder();

        if (search != null && !search.isEmpty()) {
            description.append("Search: '").append(search).append("' | ");
        }

        if (status != null) {
            description.append("Status: ").append(status.name().replace("_", " ")).append(" | ");
        }

        if (onGithub != null) {
            description.append("GitHub: ").append(onGithub ? "Yes" : "No").append(" | ");
        }

        if (tags != null && !tags.isEmpty()) {
            description.append("Tags: ").append(String.join(", ", tags)).append(" | ");
        }

        if (createdMonth != null && !createdMonth.isEmpty()) {
            description.append("Created: ").append(createdMonth).append(" | ");
        }

        if (lastWorkedMonth != null && !lastWorkedMonth.isEmpty()) {
            description.append("Last Worked: ").append(lastWorkedMonth).append(" | ");
        }

        // Add sort description
        description.append("Sorted by: ");
        description.append(switch (sortBy != null ? sortBy : "lastWorked") {
            case "created" -> "Created Date";
            case "title" -> "Title (A-Z)";
            default -> "Last Worked";
        });

        return description.toString();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Apply all filters to get the base list of projects
     */
    private List<Project> applyFilters(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth) {

        // PRIORITY 1: Month filters (these take absolute priority)
        if (createdMonth != null && !createdMonth.isEmpty()) {
            log.debug("Filtering by created month: {}", createdMonth);
            YearMonth yearMonth = YearMonth.parse(createdMonth);
            return projectService.getProjectsCreatedInMonth(yearMonth);
        }

        if (lastWorkedMonth != null && !lastWorkedMonth.isEmpty()) {
            log.debug("Filtering by last worked month: {}", lastWorkedMonth);
            YearMonth yearMonth = YearMonth.parse(lastWorkedMonth);
            return projectService.getProjectsLastWorkedInMonth(yearMonth);
        }

        // PRIORITY 2: Tag filters with other filters
        if (tags != null && !tags.isEmpty()) {
            log.debug("Filtering by tags: {}", tags);
            List<Project> taggedProjects = projectService.getProjectsByTags(tags);

            // Apply additional filters on top of tag results
            return taggedProjects.stream()
                    .filter(p -> matchesSearch(p, search))
                    .filter(p -> matchesStatus(p, status))
                    .filter(p -> matchesGithubStatus(p, onGithub))
                    .toList();
        }

        // PRIORITY 3: Regular filters without tags
        if (hasBasicFilters(search, status, onGithub)) {
            log.debug("Filtering with basic filters");
            return projectService.searchProjects(search, status, onGithub, null);
        }

        // NO FILTERS: Return all projects
        log.debug("No filters applied, returning all projects");
        return projectService.getAllProjects();
    }

    /**
     * Apply sorting to the filtered project list
     */
    private List<Project> applySorting(List<Project> projects, String sortBy) {
        if (projects == null || projects.isEmpty()) {
            return projects != null ? projects : Collections.emptyList();
        }

        String sortOrder = sortBy != null ? sortBy : "lastWorked";
        log.debug("Applying sort order: {}", sortOrder);

        return switch (sortOrder) {
            case "created" -> projects.stream()
                    .sorted((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()))
                    .toList();
            case "title" -> projects.stream()
                    .sorted((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()))
                    .toList();
            default -> projects.stream() // "lastWorked"
                    .sorted((p1, p2) -> p2.getLastWorkedOn().compareTo(p1.getLastWorkedOn()))
                    .toList();
        };
    }

    /**
     * Check if project matches search term
     */
    private boolean matchesSearch(Project project, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }
        return project.getTitle().toLowerCase().contains(search.toLowerCase());
    }

    /**
     * Check if project matches status filter
     */
    private boolean matchesStatus(Project project, ProjectStatus status) {
        if (status == null) {
            return true;
        }
        return project.getStatus() == status;
    }

    /**
     * Check if project matches GitHub status filter
     */
    private boolean matchesGithubStatus(Project project, Boolean onGithub) {
        if (onGithub == null) {
            return true;
        }
        return project.getOnGithub().equals(onGithub);
    }

    /**
     * Check if any basic filters are applied
     */
    private boolean hasBasicFilters(String search, ProjectStatus status, Boolean onGithub) {
        return (search != null && !search.isEmpty()) || status != null || onGithub != null;
    }
}