package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.entity.User;

import java.util.*;

/**
 * Service for calculating project statistics and metrics
 */
public interface StatisticsService {


    /**
     * Get total number of projects
     */
    long getTotalProjects(User owner);

    /**
     * Get count of projects by status
     * @return Map with status as key and count as value
     */
    Map<String, Long> getProjectsByStatus(User owner);

    /**
     * Get count of GitHub vs Local projects
     * @return Map with "github" and "local" as keys
     */
    Map<String, Long> getGitHubVsLocal(User owner);

    /**
     * Get projects created per month for the last 6 months
     * @return Map with month (YYYY-MM) as key and count as value
     */
    Map<String, Long> getProjectsCreatedByMonth(User owner);

    /**
     * Get top N most used tags with project counts
     * @param limit Number of tags to return
     * @return Map with tag name as key and count as value
     */
    Map<String, Long> getTopTags(int limit, User owner);

    /**
     * Get all projects with days since last worked (for activity heatmap)
     * @return List of ProjectDTOs sorted by days since last worked
     */
    List<ProjectDTO> getProjectActivityData(User owner);

    /**
     * Get completion rate percentage
     * @return Percentage of completed projects
     */
    double getCompletionRate(User owner);

    /**
     * Get average days since last worked
     * @return Average days across all projects
     */
    double getAverageDaysSinceLastWorked(User owner);


}