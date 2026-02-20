package com.vbforge.projectstracker.mapper;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectMapper Tests")
class ProjectMapperTest {

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private ProjectMapper projectMapper;

    private User owner;
    private Tag tag;
    private Project project;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        tag = Tag.builder()
                .id(1L)
                .name("Spring Boot")
                .color("#28a745")
                .owner(owner)
                .build();

        project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(true)
                .githubUrl("https://github.com/user/project")
                .localPath("/path/to/project")
                .whatTodo("Complete features")
                .createdDate(LocalDateTime.now())
                .lastWorkedOn(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .owner(owner)
                .tags(new HashSet<>(Set.of(tag)))
                .build();
    }

    @Test
    @DisplayName("Should convert entity to DTO")
    void shouldConvertEntityToDTO() {
        // Given
        TagDTO tagDTO = TagDTO.builder()
                .id(1L)
                .name("Spring Boot")
                .color("#28a745")
                .build();
        when(tagMapper.toDTO(any(Tag.class))).thenReturn(tagDTO);

        // When
        ProjectDTO dto = projectMapper.toDTO(project);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Project");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(dto.getOnGithub()).isTrue();
        assertThat(dto.getGithubUrl()).isEqualTo("https://github.com/user/project");
        assertThat(dto.getLocalPath()).isEqualTo("/path/to/project");
        assertThat(dto.getWhatTodo()).isEqualTo("Complete features");
        assertThat(dto.getTags()).hasSize(1);
        assertThat(dto.getTagIds()).containsExactly(1L);
        assertThat(dto.getDaysSinceLastWorked()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should handle null entity when converting to DTO")
    void shouldHandleNullEntityWhenConvertingToDTO() {
        // When
        ProjectDTO dto = projectMapper.toDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should handle empty tags when converting to DTO")
    void shouldHandleEmptyTagsWhenConvertingToDTO() {

        // Given
        project.getTags().clear();

        // When
        ProjectDTO dto = projectMapper.toDTO(project);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getTags()).isEmpty();
        assertThat(dto.getTagIds()).isEmpty();
    }

    @Test
    @DisplayName("Should convert DTO to entity")
    void shouldConvertDTOToEntity() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(true)
                .githubUrl("https://github.com/user/project")
                .localPath("/path/to/project")
                .whatTodo("Complete features")
                .build();

        // When
        Project entity = projectMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getTitle()).isEqualTo("Test Project");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(entity.getOnGithub()).isTrue();
        assertThat(entity.getGithubUrl()).isEqualTo("https://github.com/user/project");
        assertThat(entity.getLocalPath()).isEqualTo("/path/to/project");
        assertThat(entity.getWhatTodo()).isEqualTo("Complete features");
        // Owner and tags NOT set (by design)
        assertThat(entity.getOwner()).isNull();
        assertThat(entity.getTags()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null DTO when converting to entity")
    void shouldHandleNullDTOWhenConvertingToEntity() {
        // When
        Project entity = projectMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity from DTO")
    void shouldUpdateEntityFromDTO() {
        // Given
        ProjectDTO dto = ProjectDTO.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(ProjectStatus.DONE)
                .onGithub(false)
                .githubUrl("https://github.com/user/updated")
                .localPath("/new/path")
                .whatTodo("New tasks")
                .build();

        // When
        projectMapper.updateEntityFromDTO(project, dto);

        // Then
        assertThat(project.getTitle()).isEqualTo("Updated Title");
        assertThat(project.getDescription()).isEqualTo("Updated Description");
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.DONE);
        assertThat(project.getOnGithub()).isFalse();
        assertThat(project.getGithubUrl()).isEqualTo("https://github.com/user/updated");
        assertThat(project.getLocalPath()).isEqualTo("/new/path");
        assertThat(project.getWhatTodo()).isEqualTo("New tasks");
        // Owner, tags, timestamps NOT updated (by design)
        assertThat(project.getOwner()).isEqualTo(owner);
        assertThat(project.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle null parameters in updateEntityFromDTO")
    void shouldHandleNullParametersInUpdateEntityFromDTO() {
        // When/Then - Should not throw exception
        projectMapper.updateEntityFromDTO(null, new ProjectDTO());
        projectMapper.updateEntityFromDTO(project, null);
        projectMapper.updateEntityFromDTO(null, null);

        // Original project unchanged
        assertThat(project.getTitle()).isEqualTo("Test Project");
    }
}