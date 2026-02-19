package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.User;

import java.util.List;

/**
 * Service for filtering and sorting projects
 * Centralizes all filtering logic to avoid duplication across controllers
 */
public interface ProjectFilterService {

    /**
     * Get filtered and sorted projects based on multiple criteria
     *
     * @param search search term for title (case-insensitive)
     * @param status filter by project status
     * @param onGithub filter by GitHub status
     * @param tags filter by tag names (matches projects with ANY of these tags)
     * @param createdMonth filter by creation month (format: yyyy-MM)
     * @param lastWorkedMonth filter by last worked month (format: yyyy-MM)
     * @param sortBy sort order: "lastWorked" (default), "created", or "title"
     * @return filtered and sorted list of projects
     */
    List<Project> getFilteredAndSortedProjects(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth,
            String sortBy,
            User owner
    );

    /**
     * Check if any filters are currently active
     *
     * @param search search term
     * @param status status filter
     * @param onGithub GitHub filter
     * @param tags tag filters
     * @param createdMonth created month filter
     * @param lastWorkedMonth last worked month filter
     * @return true if any filter is active
     */
    boolean hasActiveFilters(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth
    );

    /**
     * Build a human-readable description of active filters
     * Useful for export reports and UI feedback
     *
     * @param search search term
     * @param status status filter
     * @param onGithub GitHub filter
     * @param tags tag filters
     * @param createdMonth created month filter
     * @param lastWorkedMonth last worked month filter
     * @param sortBy sort order
     * @return description string (e.g., "Search: 'test' | Status: IN_PROGRESS | Sorted by: Title")
     */
    String buildFilterDescription(
            String search,
            ProjectStatus status,
            Boolean onGithub,
            List<String> tags,
            String createdMonth,
            String lastWorkedMonth,
            String sortBy
    );
}