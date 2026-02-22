package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.service.ExportService;
import com.vbforge.projectstracker.service.ProjectFilterService;
import com.vbforge.projectstracker.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExportController.class)
@DisplayName("ExportController Tests")
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectFilterService filterService;

    @MockitoBean
    private ExportService exportService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @Test
    @WithMockUser
    @DisplayName("Should export projects to CSV")
    void shouldExportProjectsToCSV() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        Project project = Project.builder().id(1L).title("Test").owner(user).build();
        byte[] csvData = "id,title\n1,Test".getBytes();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(filterService.getFilteredAndSortedProjects(
                any(), any(), any(), any(), any(), any(), any(), eq(user)))
                .thenReturn(List.of(project));
        when(exportService.exportToCSV(any(), eq(user))).thenReturn(csvData);

        mockMvc.perform(get("/export/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", 
                    org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string("Content-Disposition", 
                    org.hamcrest.Matchers.containsString(".csv")));
    }

    @Test
    @WithMockUser
    @DisplayName("Should export projects to HTML")
    void shouldExportProjectsToHTML() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        Project project = Project.builder().id(1L).title("Test").owner(user).build();
        byte[] htmlData = "<html><body>Report</body></html>".getBytes();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(filterService.getFilteredAndSortedProjects(
                any(), any(), any(), any(), any(), any(), any(), eq(user)))
                .thenReturn(List.of(project));
        when(filterService.buildFilterDescription(
                any(), any(), any(), any(), any(), any(), any()))
                .thenReturn("All Projects");
        when(exportService.exportToHTML(any(), any(), eq(user))).thenReturn(htmlData);

        mockMvc.perform(get("/export/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", 
                    org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string("Content-Disposition", 
                    org.hamcrest.Matchers.containsString(".html")));
    }

    @Test
    @WithMockUser
    @DisplayName("Should export with filters applied")
    void shouldExportWithFiltersApplied() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(filterService.getFilteredAndSortedProjects(
                eq("search"), eq(ProjectStatus.IN_PROGRESS), eq(true), 
                any(), any(), any(), any(), eq(user)))
                .thenReturn(List.of());
        when(exportService.exportToCSV(any(), eq(user))).thenReturn(new byte[0]);

        mockMvc.perform(get("/export/csv")
                        .param("search", "search")
                        .param("status", "IN_PROGRESS")
                        .param("onGithub", "true"))
                .andExpect(status().isOk());
    }
}
