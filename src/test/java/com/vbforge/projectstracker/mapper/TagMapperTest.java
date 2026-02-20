package com.vbforge.projectstracker.mapper;

import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TagMapper Tests")
class TagMapperTest {

    private TagMapper tagMapper;
    private User owner;
    private Tag tag;

    @BeforeEach
    void setUp() {
        tagMapper = new TagMapper();

        owner = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        tag = Tag.builder()
                .id(1L)
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .createdDate(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now())
                .owner(owner)
                .build();
    }

    @Test
    @DisplayName("Should convert entity to DTO")
    void shouldConvertEntityToDTO() {
        // When
        TagDTO dto = tagMapper.toDTO(tag);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Spring Boot");
        assertThat(dto.getColor()).isEqualTo("#28a745");
        assertThat(dto.getDescription()).isEqualTo("Java framework");
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getUpdatedAt()).isNotNull();
        assertThat(dto.getProjectCount()).isZero();
    }

    @Test
    @DisplayName("Should include project count when converting to DTO")
    void shouldIncludeProjectCountWhenConvertingToDTO() {
        // Given
        Project project1 = Project.builder()
                .id(1L)
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(owner)
                .build();
        Project project2 = Project.builder()
                .id(2L)
                .title("Project 2")
                .status(ProjectStatus.DONE)
                .owner(owner)
                .build();
        tag.setProjects(Set.of(project1, project2));

        // When
        TagDTO dto = tagMapper.toDTO(tag);

        // Then
        assertThat(dto.getProjectCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle null entity when converting to DTO")
    void shouldHandleNullEntityWhenConvertingToDTO() {
        // When
        TagDTO dto = tagMapper.toDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should handle null projects when converting to DTO")
    void shouldHandleNullProjectsWhenConvertingToDTO() {
        // Given
        tag.setProjects(null);

        // When
        TagDTO dto = tagMapper.toDTO(tag);

        // Then
        assertThat(dto.getProjectCount()).isZero();
    }

    @Test
    @DisplayName("Should convert DTO to entity")
    void shouldConvertDTOToEntity() {
        // Given
        TagDTO dto = TagDTO.builder()
                .id(1L)
                .name("React")
                .color("#61dafb")
                .description("JS library")
                .build();

        // When
        Tag entity = tagMapper.toEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("React");
        assertThat(entity.getColor()).isEqualTo("#61dafb");
        assertThat(entity.getDescription()).isEqualTo("JS library");
        // Owner NOT set (by design)
        assertThat(entity.getOwner()).isNull();
    }

    @Test
    @DisplayName("Should handle null DTO when converting to entity")
    void shouldHandleNullDTOWhenConvertingToEntity() {
        // When
        Tag entity = tagMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity from DTO")
    void shouldUpdateEntityFromDTO() {
        // Given
        TagDTO dto = TagDTO.builder()
                .name("Updated Name")
                .color("#000000")
                .description("Updated Description")
                .build();

        // When
        tagMapper.updateEntityFromDTO(tag, dto);

        // Then
        assertThat(tag.getName()).isEqualTo("Updated Name");
        assertThat(tag.getColor()).isEqualTo("#000000");
        assertThat(tag.getDescription()).isEqualTo("Updated Description");
        // Owner, timestamps NOT updated (by design)
        assertThat(tag.getOwner()).isEqualTo(owner);
        assertThat(tag.getCreatedDate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null parameters in updateEntityFromDTO")
    void shouldHandleNullParametersInUpdateEntityFromDTO() {
        // When/Then - Should not throw exception
        tagMapper.updateEntityFromDTO(null, new TagDTO());
        tagMapper.updateEntityFromDTO(tag, null);
        tagMapper.updateEntityFromDTO(null, null);

        // Original tag unchanged
        assertThat(tag.getName()).isEqualTo("Spring Boot");
    }
}
























































































































