package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.dto.ProjectDTO;

import java.util.List;
import java.util.Map;

/**
 * Service for calculating project statistics and metrics
 */
public interface StatisticsService {

    /**
     * Get total number of projects
     */
    long getTotalProjects();

    /**
     * Get count of projects by status
     * @return Map with status as key and count as value
     */
    Map<String, Long> getProjectsByStatus();

    /**
     * Get count of GitHub vs Local projects
     * @return Map with "github" and "local" as keys
     */
    Map<String, Long> getGitHubVsLocal();

    /**
     * Get projects created per month for the last 6 months
     * @return Map with month (YYYY-MM) as key and count as value
     */
    Map<String, Long> getProjectsCreatedByMonth();

    /**
     * Get top N most used tags with project counts
     * @param limit Number of tags to return
     * @return Map with tag name as key and count as value
     */
    Map<String, Long> getTopTags(int limit);

    /**
     * Get all projects with days since last worked (for activity heatmap)
     * @return List of ProjectDTOs sorted by days since last worked
     */
    List<ProjectDTO> getProjectActivityData();

    /**
     * Get completion rate percentage
     * @return Percentage of completed projects
     */
    double getCompletionRate();

    /**
     * Get average days since last worked
     * @return Average days across all projects
     */
    double getAverageDaysSinceLastWorked();
}