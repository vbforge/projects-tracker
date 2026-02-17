package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public String showStatistics(Model model) {
        log.info("Loading statistics dashboard");

        try {
            // Quick Stats
            long totalProjects = statisticsService.getTotalProjects();
            double completionRate = statisticsService.getCompletionRate();
            double avgDaysSinceWorked = statisticsService.getAverageDaysSinceLastWorked();

            model.addAttribute("totalProjects", totalProjects);
            model.addAttribute("completionRate", completionRate);
            model.addAttribute("avgDaysSinceWorked", avgDaysSinceWorked);

            // Chart 1: Projects by Status
            Map<String, Long> statusData = statisticsService.getProjectsByStatus();
            List<String> statusLabels = new ArrayList<>(statusData.keySet());
            List<Long> statusValues = new ArrayList<>(statusData.values());

            log.info("Status data - Labels: {}, Values: {}", statusLabels, statusValues);

            model.addAttribute("statusLabels", statusLabels);
            model.addAttribute("statusData", statusValues);

            // Chart 2: GitHub vs Local
            Map<String, Long> githubData = statisticsService.getGitHubVsLocal();
            List<String> githubLabels = new ArrayList<>(githubData.keySet());
            List<Long> githubValues = new ArrayList<>(githubData.values());

            log.info("GitHub data - Labels: {}, Values: {}", githubLabels, githubValues);

            model.addAttribute("githubLabels", githubLabels);
            model.addAttribute("githubData", githubValues);

            // Chart 3: Projects Created Over Time
            Map<String, Long> timelineData = statisticsService.getProjectsCreatedByMonth();

            // Convert YYYY-MM to readable month names
            List<String> timelineLabels = new ArrayList<>();
            List<Long> timelineValues = new ArrayList<>();

            for (Map.Entry<String, Long> entry : timelineData.entrySet()) {
                try {
                    YearMonth month = YearMonth.parse(entry.getKey());
                    String label = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    timelineLabels.add(label);
                    timelineValues.add(entry.getValue());
                } catch (Exception e) {
                    log.error("Error parsing month: {}", entry.getKey(), e);
                }
            }

            log.info("Timeline data - Labels: {}, Values: {}", timelineLabels, timelineValues);

            model.addAttribute("timelineLabels", timelineLabels);
            model.addAttribute("timelineData", timelineValues);

            // Chart 4: Top Tags
            Map<String, Long> topTags = statisticsService.getTopTags(10);
            List<String> tagLabels = new ArrayList<>(topTags.keySet());
            List<Long> tagValues = new ArrayList<>(topTags.values());

            log.info("Top tags - Labels: {}, Values: {}", tagLabels, tagValues);

            model.addAttribute("tagLabels", tagLabels);
            model.addAttribute("tagData", tagValues);

            // Chart 5: Activity Heatmap (Projects by days since last worked)
            List<ProjectDTO> activityData = statisticsService.getProjectActivityData();

            log.info("Activity data - {} projects found", activityData != null ? activityData.size() : 0);

            // Limit to top 12 projects for readability
            List<ProjectDTO> limitedActivity = activityData != null && !activityData.isEmpty()
                    ? activityData.stream().limit(12).toList()
                    : new ArrayList<>();

            List<String> activityLabels = new ArrayList<>();
            List<Long> activityDays = new ArrayList<>();

            for (ProjectDTO project : limitedActivity) {
                if (project != null && project.getTitle() != null) {
                    activityLabels.add(project.getTitle());
                    activityDays.add(project.getDaysSinceLastWorked());
                }
            }

            log.info("Activity heatmap - Labels: {}, Days: {}", activityLabels, activityDays);

            model.addAttribute("activityLabels", activityLabels);
            model.addAttribute("activityData", activityDays);

            // Calculate additional stats for cards
            long notStarted = statusData.getOrDefault("NOT_STARTED", 0L);
            long inProgress = statusData.getOrDefault("IN_PROGRESS", 0L);
            long completed = statusData.getOrDefault("DONE", 0L);

            model.addAttribute("notStartedCount", notStarted);
            model.addAttribute("inProgressCount", inProgress);
            model.addAttribute("completedCount", completed);

            log.info("Statistics loaded successfully: {} projects, {}% completion",
                    totalProjects, completionRate);

            // Add flags to indicate if we have data for each chart
            model.addAttribute("hasStatusData", !statusValues.isEmpty() && statusValues.stream().anyMatch(v -> v > 0));
            model.addAttribute("hasGithubData", !githubValues.isEmpty() && githubValues.stream().anyMatch(v -> v > 0));
            model.addAttribute("hasTimelineData", !timelineValues.isEmpty() && timelineValues.stream().anyMatch(v -> v > 0));
            model.addAttribute("hasTagData", !tagLabels.isEmpty());
            model.addAttribute("hasActivityData", !activityLabels.isEmpty());

        } catch (Exception e) {
            log.error("Error loading statistics", e);
            model.addAttribute("error", "Unable to load statistics: " + e.getMessage());

            // Set empty defaults
            model.addAttribute("statusLabels", Arrays.asList("NOT_STARTED", "IN_PROGRESS", "DONE"));
            model.addAttribute("statusData", Arrays.asList(0L, 0L, 0L));
            model.addAttribute("githubLabels", Arrays.asList("github", "local"));
            model.addAttribute("githubData", Arrays.asList(0L, 0L));
            model.addAttribute("timelineLabels", new ArrayList<>());
            model.addAttribute("timelineData", new ArrayList<>());
            model.addAttribute("tagLabels", new ArrayList<>());
            model.addAttribute("tagData", new ArrayList<>());
            model.addAttribute("activityLabels", new ArrayList<>());
            model.addAttribute("activityData", new ArrayList<>());
        }

        return "statistics";
    }


}