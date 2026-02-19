package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.mapper.ProjectMapper;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public long getTotalProjects(User owner) {
        return projectRepository.countByOwner(owner);
    }

    @Override
    public Map<String, Long> getProjectsByStatus(User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        statusCounts.put("NOT_STARTED", 0L);
        statusCounts.put("IN_PROGRESS", 0L);
        statusCounts.put("DONE", 0L);

        for (Project project : projects) {
            String status = project.getStatus().name();
            statusCounts.put(status, statusCounts.get(status) + 1);
        }

        log.debug("Projects by status for {}: {}", owner.getUsername(), statusCounts);
        return statusCounts;
    }

    @Override
    public Map<String, Long> getGitHubVsLocal(User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);

        long githubCount = projects.stream().filter(Project::getOnGithub).count();
        long localCount = projects.size() - githubCount;

        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("github", githubCount);
        distribution.put("local", localCount);

        log.debug("GitHub vs Local for {}: {}", owner.getUsername(), distribution);
        return distribution;
    }

    @Override
    public Map<String, Long> getProjectsCreatedByMonth(User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);
        LocalDate now = LocalDate.now();
        Map<String, Long> monthCounts = new LinkedHashMap<>();

        // Initialize last 6 months with 0
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            monthCounts.put(month.toString(), 0L);
        }

        // Count projects by creation month
        for (Project project : projects) {
            YearMonth createdMonth = YearMonth.from(project.getCreatedDate());
            String monthKey = createdMonth.toString();
            if (monthCounts.containsKey(monthKey)) {
                monthCounts.put(monthKey, monthCounts.get(monthKey) + 1);
            }
        }

        log.debug("Projects created by month for {}: {}", owner.getUsername(), monthCounts);
        return monthCounts;
    }

    @Override
    public Map<String, Long> getTopTags(int limit, User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);

        Map<String, Long> tagCounts = new HashMap<>();
        for (Project project : projects) {
            if (project.getTags() != null) {
                for (Tag tag : project.getTags()) {
                    tagCounts.merge(tag.getName(), 1L, Long::sum);
                }
            }
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

        log.debug("Top {} tags for {}: {}", limit, owner.getUsername(), topTags);
        return topTags;
    }

    @Override
    public List<ProjectDTO> getProjectActivityData(User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);

        return projects.stream()
                .map(projectMapper::toDTO)
                .sorted(Comparator.comparingLong(ProjectDTO::getDaysSinceLastWorked))
                .collect(Collectors.toList());
    }

    @Override
    public double getCompletionRate(User owner) {
        long total = projectRepository.countByOwner(owner);
        if (total == 0) return 0.0;

        long completed = projectRepository.countByStatusAndOwner(ProjectStatus.DONE, owner);
        double rate = (completed * 100.0) / total;

        log.debug("Completion rate for {}: {}%", owner.getUsername(), String.format("%.1f", rate));
        return Math.round(rate * 10.0) / 10.0;
    }

    @Override
    public double getAverageDaysSinceLastWorked(User owner) {
        List<Project> projects = projectRepository.findAllByOwner(owner);
        if (projects.isEmpty()) return 0.0;

        LocalDate today = LocalDate.now();
        double average = projects.stream()
                .mapToLong(project -> ChronoUnit.DAYS.between(project.getLastWorkedOn().toLocalDate(), today))
                .average()
                .orElse(0.0);

        log.debug("Average days since last worked for {}: {}", owner.getUsername(), String.format("%.1f", average));
        return Math.round(average * 10.0) / 10.0;
    }
}