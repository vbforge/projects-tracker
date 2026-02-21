package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.service.StatisticsService;
import com.vbforge.projectstracker.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@DisplayName("StatisticsController Tests")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private SecurityUtils securityUtils;

    @Test
    @WithMockUser
    @DisplayName("Should show statistics dashboard")
    void shouldShowStatisticsDashboard() throws Exception {
        User user = User.builder().id(1L).username("testuser").role(Role.USER).build();
        
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(statisticsService.getTotalProjects(user)).thenReturn(10L);
        when(statisticsService.getCompletionRate(user)).thenReturn(50.0);
        when(statisticsService.getAverageDaysSinceLastWorked(user)).thenReturn(5.5);
        
        Map<String, Long> statusData = new LinkedHashMap<>();
        statusData.put("NOT_STARTED", 2L);
        statusData.put("IN_PROGRESS", 3L);
        statusData.put("DONE", 5L);
        when(statisticsService.getProjectsByStatus(user)).thenReturn(statusData);
        
        Map<String, Long> githubData = new LinkedHashMap<>();
        githubData.put("github", 6L);
        githubData.put("local", 4L);
        when(statisticsService.getGitHubVsLocal(user)).thenReturn(githubData);
        
        when(statisticsService.getProjectsCreatedByMonth(user)).thenReturn(new LinkedHashMap<>());
        when(statisticsService.getTopTags(10, user)).thenReturn(new LinkedHashMap<>());
        when(statisticsService.getProjectActivityData(user)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("statistics"))
                .andExpect(model().attributeExists("totalProjects"))
                .andExpect(model().attribute("totalProjects", 10L))
                .andExpect(model().attribute("completionRate", 50.0))
                .andExpect(model().attribute("avgDaysSinceWorked", 5.5))
                .andExpect(model().attributeExists("statusLabels", "statusData"))
                .andExpect(model().attributeExists("githubLabels", "githubData"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle error gracefully")
    void shouldHandleErrorGracefully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(statisticsService.getTotalProjects(user)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("statistics"))
                .andExpect(model().attributeExists("error"));
    }
}
