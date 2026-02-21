package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User owner;
    private Project project1;
    private Tag tag1;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        tag1 = Tag.builder()
                .id(1L)
                .name("Spring Boot")
                .owner(owner)
                .build();

        project1 = Project.builder()
                .id(1L)
                .title("Test Project")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(owner)
                .build();
    }

    @Test
    @DisplayName("Should get all projects for owner")
    void shouldGetAllProjectsForOwner() {
        when(projectRepository.findAllByOwner(owner)).thenReturn(List.of(project1));

        List<Project> result = projectService.getAllProjects(owner);

        assertThat(result).hasSize(1);
        verify(projectRepository).findAllByOwner(owner);
    }

    @Test
    @DisplayName("Should save project successfully")
    void shouldSaveProjectSuccessfully() {
        when(projectRepository.save(project1)).thenReturn(project1);

        Project result = projectService.saveProject(project1);

        assertThat(result).isEqualTo(project1);
        verify(projectRepository).save(project1);
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() {
        Project updatedData = Project.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(ProjectStatus.DONE)
                .build();

        when(projectRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(project1));
        when(projectRepository.save(any())).thenReturn(project1);

        Project result = projectService.updateProject(1L, updatedData, owner);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(projectRepository).save(project1);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent project")
    void shouldThrowExceptionWhenUpdatingNonExistentProject() {
        when(projectRepository.findByIdAndOwner(999L, owner)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(999L, project1, owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete project successfully")
    void shouldDeleteProjectSuccessfully() {
        when(projectRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(project1));

        projectService.deleteProject(1L, owner);

        verify(projectRepository).delete(project1);
    }

    @Test
    @DisplayName("Should add tag to project")
    void shouldAddTagToProject() {
        when(projectRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(project1));
        when(tagRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(tag1));
        when(projectRepository.save(any())).thenReturn(project1);

        Project result = projectService.addTagToProject(1L, 1L, owner);

        assertThat(result).isNotNull();
        verify(projectRepository).save(project1);
    }

    @Test
    @DisplayName("Should update project tags")
    void shouldUpdateProjectTags() {
        when(projectRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(project1));
        when(tagRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(tag1));
        when(projectRepository.save(any())).thenReturn(project1);

        Project result = projectService.updateProjectTags(1L, List.of(1L), owner);

        assertThat(result).isNotNull();
        verify(tagRepository).findByIdAndOwner(1L, owner);
    }

    @Test
    @DisplayName("Should get project count")
    void shouldGetProjectCount() {
        when(projectRepository.countByOwner(owner)).thenReturn(5L);

        long count = projectService.getTotalProjectCount(owner);

        assertThat(count).isEqualTo(5L);
    }
}
