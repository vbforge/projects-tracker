package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.service.ProjectService;
import com.vbforge.projectstracker.service.TagService;
import com.vbforge.projectstracker.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@DisplayName("ProjectController Tests")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private TagService tagService;

    @MockBean
    private SecurityUtils securityUtils;

    @Test
    @WithMockUser
    @DisplayName("Should show dashboard with projects")
    void shouldShowDashboardWithProjects() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(projectService.getAllProjects(user)).thenReturn(List.of());
        when(projectService.getTotalProjectCount(user)).thenReturn(0L);
        when(tagService.getAllTagsOrderedByPopularity(user)).thenReturn(List.of());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("projects", "totalProjects"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should show new project form")
    void shouldShowNewProjectForm() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(tagService.getAllTagsOrderedByName(user)).thenReturn(List.of());

        mockMvc.perform(get("/projects/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("project-form"))
                .andExpect(model().attributeExists("project", "allTags", "statuses"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should create project successfully")
    void shouldCreateProjectSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        Project savedProject = Project.builder().id(1L).title("Test Project").owner(user).build();
        
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(projectService.saveProject(any())).thenReturn(savedProject);

        mockMvc.perform(post("/projects/add")
                        .with(csrf())
                        .param("title", "Test Project")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"))
                .andExpect(flash().attributeExists("success"));

        verify(projectService).saveProject(any(Project.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should show validation error when title is blank")
    void shouldShowValidationErrorWhenTitleIsBlank() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(tagService.getAllTagsOrderedByName(user)).thenReturn(List.of());

        mockMvc.perform(post("/projects/add")
                        .with(csrf())
                        .param("title", "")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(view().name("project-form"))
                .andExpect(model().attributeHasFieldErrors("project", "title"));

        verify(projectService, never()).saveProject(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(post("/projects/1/update")
                        .with(csrf())
                        .param("title", "Updated Project")
                        .param("status", "DONE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService).updateProject(eq(1L), any(), eq(user));
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete project successfully")
    void shouldDeleteProjectSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(post("/projects/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService).deleteProject(1L, user);
    }

    @Test
    @WithMockUser
    @DisplayName("Should view project details")
    void shouldViewProjectDetails() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        Project project = Project.builder().id(1L).title("Test").owner(user).build();
        
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(projectService.getProjectByIdAndOwner(1L, user)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("project-detail"))
                .andExpect(model().attributeExists("project"));
    }
}
