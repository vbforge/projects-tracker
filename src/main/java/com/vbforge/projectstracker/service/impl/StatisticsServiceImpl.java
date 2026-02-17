package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.mapper.ProjectMapper;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public long getTotalProjects() {
        long count = projectRepository.count();
        log.info("Total projects: {}", count);
        return count;
    }

    @Override
    public Map<String, Long> getProjectsByStatus() {
        try {
            List<Project> projects = projectRepository.findAll();
            log.info("Retrieved {} projects for status calculation", projects.size());

            Map<String, Long> statusCounts = new LinkedHashMap<>();
            statusCounts.put("NOT_STARTED", 0L);
            statusCounts.put("IN_PROGRESS", 0L);
            statusCounts.put("DONE", 0L);

            for (Project project : projects) {
                if (project.getStatus() != null) {
                    String status = project.getStatus().name();
                    statusCounts.put(status, statusCounts.get(status) + 1);
                    log.debug("Project '{}' status: {}", project.getTitle(), status);
                } else {
                    log.warn("Project '{}' has null status!", project.getTitle());
                }
            }

            log.info("Projects by status: {}", statusCounts);
            return statusCounts;
        } catch (Exception e) {
            log.error("Error calculating projects by status", e);
            Map<String, Long> empty = new LinkedHashMap<>();
            empty.put("NOT_STARTED", 0L);
            empty.put("IN_PROGRESS", 0L);
            empty.put("DONE", 0L);
            return empty;
        }
    }

    @Override
    public Map<String, Long> getGitHubVsLocal() {
        try {
            List<Project> projects = projectRepository.findAll();
            log.info("Calculating GitHub vs Local for {} projects", projects.size());

            long githubCount = 0;
            long localCount = 0;

            for (Project project : projects) {
                if (project.getOnGithub() != null && project.getOnGithub()) {
                    githubCount++;
                    log.debug("Project '{}' is on GitHub", project.getTitle());
                } else {
                    localCount++;
                    log.debug("Project '{}' is local", project.getTitle());
                }
            }

            Map<String, Long> distribution = new LinkedHashMap<>();
            distribution.put("github", githubCount);
            distribution.put("local", localCount);

            log.info("GitHub vs Local: {} on GitHub, {} local", githubCount, localCount);
            return distribution;
        } catch (Exception e) {
            log.error("Error calculating GitHub vs Local", e);
            Map<String, Long> empty = new LinkedHashMap<>();
            empty.put("github", 0L);
            empty.put("local", 0L);
            return empty;
        }
    }

    @Override
    public Map<String, Long> getProjectsCreatedByMonth() {
        try {
            List<Project> projects = projectRepository.findAll();
            log.info("Calculating projects created by month for {} projects", projects.size());

            // Get last 6 months
            LocalDate now = LocalDate.now();
            Map<String, Long> monthCounts = new LinkedHashMap<>();

            // Initialize last 6 months with 0
            for (int i = 5; i >= 0; i--) {
                YearMonth month = YearMonth.from(now.minusMonths(i));
                monthCounts.put(month.toString(), 0L);
                log.debug("Initialized month: {}", month);
            }

            // Count projects by creation month
            for (Project project : projects) {
                if (project.getCreatedDate() != null) {
                    YearMonth createdMonth = YearMonth.from(project.getCreatedDate());
                    String monthKey = createdMonth.toString();

                    if (monthCounts.containsKey(monthKey)) {
                        monthCounts.put(monthKey, monthCounts.get(monthKey) + 1);
                        log.debug("Project '{}' created in {}", project.getTitle(), monthKey);
                    } else {
                        log.debug("Project '{}' created in {} (outside 6-month window)", project.getTitle(), monthKey);
                    }
                } else {
                    log.warn("Project '{}' has null createdDate!", project.getTitle());
                }
            }

            log.info("Projects created by month: {}", monthCounts);
            return monthCounts;
        } catch (Exception e) {
            log.error("Error calculating projects by month", e);
            Map<String, Long> empty = new LinkedHashMap<>();
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                YearMonth month = YearMonth.from(now.minusMonths(i));
                empty.put(month.toString(), 0L);
            }
            return empty;
        }
    }

    @Override
    public Map<String, Long> getTopTags(int limit) {
        try {
            List<Project> projects = projectRepository.findAll();
            log.info("Calculating top {} tags from {} projects", limit, projects.size());

            // Count tag occurrences
            Map<String, Long> tagCounts = new HashMap<>();

            for (Project project : projects) {
                if (project.getTags() != null && !project.getTags().isEmpty()) {
                    log.debug("Project '{}' has {} tags", project.getTitle(), project.getTags().size());
                    for (Tag tag : project.getTags()) {
                        if (tag != null && tag.getName() != null) {
                            tagCounts.merge(tag.getName(), 1L, Long::sum);
                            log.debug("Counted tag: {}", tag.getName());
                        } else {
                            log.warn("Found null tag or tag with null name in project '{}'", project.getTitle());
                        }
                    }
                } else {
                    log.debug("Project '{}' has no tags", project.getTitle());
                }
            }

            log.info("Total unique tags found: {}", tagCounts.size());

            // If no tags found, return empty map
            if (tagCounts.isEmpty()) {
                log.warn("No tags found in any projects!");
                return new LinkedHashMap<>();
            }

            // Sort by count descending and limit
            Map<String, Long> topTags = tagCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(limit)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            log.info("Top {} tags: {}", limit, topTags);
            return topTags;
        } catch (Exception e) {
            log.error("Error calculating top tags", e);
            return new LinkedHashMap<>();
        }
    }

    @Override
    public List<ProjectDTO> getProjectActivityData() {
        try {
            List<Project> projects = projectRepository.findAll();
            log.info("Calculating activity data for {} projects", projects.size());

            if (projects.isEmpty()) {
                log.warn("No projects found for activity data!");
                return new ArrayList<>();
            }

            LocalDate today = LocalDate.now();

            // Convert to DTOs and calculate days since last worked
            List<ProjectDTO> activityData = new ArrayList<>();

            for (Project project : projects) {
                try {
                    ProjectDTO dto = projectMapper.toDTO(project);
                    if (dto != null) {
                        activityData.add(dto);
                        log.debug("Project '{}': {} days since last worked",
                                dto.getTitle(), dto.getDaysSinceLastWorked());
                    } else {
                        log.warn("ProjectMapper returned null for project '{}'", project.getTitle());
                    }
                } catch (Exception e) {
                    log.error("Error converting project '{}' to DTO", project.getTitle(), e);
                }
            }

            // Sort by days since last worked
            activityData.sort(Comparator.comparingLong(ProjectDTO::getDaysSinceLastWorked));

            log.info("Retrieved activity data for {} projects", activityData.size());
            return activityData;
        } catch (Exception e) {
            log.error("Error calculating activity data", e);
            return new ArrayList<>();
        }
    }

    @Override
    public double getCompletionRate() {
        try {
            long total = projectRepository.count();
            if (total == 0) {
                log.info("No projects, completion rate: 0%");
                return 0.0;
            }

            long completed = projectRepository.findByStatus(ProjectStatus.DONE).size();
            double rate = (completed * 100.0) / total;

            log.info("Completion rate: {}% ({}/{})", String.format("%.1f", rate), completed, total);
            return Math.round(rate * 10.0) / 10.0; // Round to 1 decimal
        } catch (Exception e) {
            log.error("Error calculating completion rate", e);
            return 0.0;
        }
    }

    @Override
    public double getAverageDaysSinceLastWorked() {
        try {
            List<Project> projects = projectRepository.findAll();
            if (projects.isEmpty()) {
                log.info("No projects, average days: 0");
                return 0.0;
            }

            LocalDate today = LocalDate.now();

            double average = projects.stream()
                    .filter(p -> p.getLastWorkedOn() != null)
                    .mapToLong(project -> {
                        LocalDate lastWorked = project.getLastWorkedOn().toLocalDate();
                        return ChronoUnit.DAYS.between(lastWorked, today);
                    })
                    .average()
                    .orElse(0.0);

            log.info("Average days since last worked: {}", String.format("%.1f", average));
            return Math.round(average * 10.0) / 10.0; // Round to 1 decimal
        } catch (Exception e) {
            log.error("Error calculating average days since last worked", e);
            return 0.0;
        }
    }
}