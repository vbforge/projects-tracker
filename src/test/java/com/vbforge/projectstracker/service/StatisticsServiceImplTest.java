package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.mapper.ProjectMapper;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService Tests")
class StatisticsServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private User owner;
    private Project project1;
    private Project project2;
    private Tag tag1;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("testuser").build();

        tag1 = Tag.builder().id(1L).name("Spring Boot").owner(owner).build();

        project1 = Project.builder()
                .id(1L)
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(true)
                .createdDate(LocalDateTime.now().minusDays(10))
                .lastWorkedOn(LocalDateTime.now().minusDays(5))
                .owner(owner)
                .tags(Set.of(tag1))
                .build();

        project2 = Project.builder()
                .id(2L)
                .title("Project 2")
                .status(ProjectStatus.DONE)
                .onGithub(false)
                .createdDate(LocalDateTime.now().minusDays(20))
                .lastWorkedOn(LocalDateTime.now().minusDays(15))
                .owner(owner)
                .build();
    }

    @Test
    @DisplayName("Should get total projects count")
    void shouldGetTotalProjectsCount() {
        when(projectRepository.countByOwner(owner)).thenReturn(2L);

        long count = statisticsService.getTotalProjects(owner);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should get projects by status")
    void shouldGetProjectsByStatus() {
        when(projectRepository.findAllByOwner(owner)).thenReturn(List.of(project1, project2));

        Map<String, Long> result = statisticsService.getProjectsByStatus(owner);

        assertThat(result).containsEntry("IN_PROGRESS", 1L);
        assertThat(result).containsEntry("DONE", 1L);
        assertThat(result).containsEntry("NOT_STARTED", 0L);
    }

    @Test
    @DisplayName("Should get GitHub vs Local distribution")
    void shouldGetGitHubVsLocal() {
        when(projectRepository.findAllByOwner(owner)).thenReturn(List.of(project1, project2));

        Map<String, Long> result = statisticsService.getGitHubVsLocal(owner);

        assertThat(result).containsEntry("github", 1L);
        assertThat(result).containsEntry("local", 1L);
    }

    @Test
    @DisplayName("Should get top tags")
    void shouldGetTopTags() {
        when(projectRepository.findAllByOwner(owner)).thenReturn(List.of(project1));

        Map<String, Long> result = statisticsService.getTopTags(10, owner);

        assertThat(result).containsEntry("Spring Boot", 1L);
    }

    @Test
    @DisplayName("Should calculate completion rate")
    void shouldCalculateCompletionRate() {
        when(projectRepository.countByOwner(owner)).thenReturn(2L);
        when(projectRepository.countByStatusAndOwner(ProjectStatus.DONE, owner)).thenReturn(1L);

        double rate = statisticsService.getCompletionRate(owner);

        assertThat(rate).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should return zero completion rate when no projects")
    void shouldReturnZeroCompletionRateWhenNoProjects() {
        when(projectRepository.countByOwner(owner)).thenReturn(0L);

        double rate = statisticsService.getCompletionRate(owner);

        assertThat(rate).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should calculate average days since last worked")
    void shouldCalculateAverageDaysSinceLastWorked() {
        when(projectRepository.findAllByOwner(owner)).thenReturn(List.of(project1, project2));

        double average = statisticsService.getAverageDaysSinceLastWorked(owner);

        assertThat(average).isGreaterThan(0.0);
    }
}
