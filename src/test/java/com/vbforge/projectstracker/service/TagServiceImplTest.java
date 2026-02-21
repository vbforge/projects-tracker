package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagService Tests")
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private User owner;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(Role.USER)
                .build();

        tag1 = Tag.builder()
                .id(1L)
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .owner(owner)
                .build();

        tag2 = Tag.builder()
                .id(2L)
                .name("React")
                .color("#61dafb")
                .description("JS library")
                .owner(owner)
                .build();
    }

    @Test
    @DisplayName("Should get all tags for owner")
    void shouldGetAllTagsForOwner() {
        // Given
        when(tagRepository.findAllByOwner(owner)).thenReturn(List.of(tag1, tag2));

        // When
        List<Tag> result = tagService.getAllTags(owner);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(tag1, tag2);
        verify(tagRepository).findAllByOwner(owner);
    }

    @Test
    @DisplayName("Should get tag by ID and owner")
    void shouldGetTagByIdAndOwner() {
        // Given
        when(tagRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(tag1));

        // When
        Optional<Tag> result = tagService.getTagById(1L, owner);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tag1);
        verify(tagRepository).findByIdAndOwner(1L, owner);
    }

    @Test
    @DisplayName("Should return empty when tag not found")
    void shouldReturnEmptyWhenTagNotFound() {
        // Given
        when(tagRepository.findByIdAndOwner(999L, owner)).thenReturn(Optional.empty());

        // When
        Optional<Tag> result = tagService.getTagById(999L, owner);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get tag by name and owner")
    void shouldGetTagByNameAndOwner() {
        // Given
        when(tagRepository.findByNameAndOwner("Spring Boot", owner)).thenReturn(Optional.of(tag1));

        // When
        Optional<Tag> result = tagService.getTagByName("Spring Boot", owner);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Should save tag successfully")
    void shouldSaveTagSuccessfully() {
        // Given
        Tag newTag = Tag.builder()
                .name("Docker")
                .color("#2496ed")
                .owner(owner)
                .build();
        when(tagRepository.save(newTag)).thenReturn(newTag);

        // When
        Tag result = tagService.saveTag(newTag);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Docker");
        verify(tagRepository).save(newTag);
    }

    @Test
    @DisplayName("Should throw exception when saving tag without owner")
    void shouldThrowExceptionWhenSavingTagWithoutOwner() {
        // Given
        Tag tagWithoutOwner = Tag.builder()
                .name("NoOwner")
                .color("#000000")
                .build();

        // When/Then
        assertThatThrownBy(() -> tagService.saveTag(tagWithoutOwner))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tag must have an owner before saving");

        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update tag successfully")
    void shouldUpdateTagSuccessfully() {
        // Given
        Tag updatedData = Tag.builder()
                .name("Spring Boot 3")
                .color("#007bff")
                .description("Updated description")
                .build();
        
        when(tagRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(tag1));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag1);

        // When
        Tag result = tagService.updateTag(1L, updatedData, owner);

        // Then
        assertThat(result.getName()).isEqualTo("Spring Boot 3");
        assertThat(result.getColor()).isEqualTo("#007bff");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        verify(tagRepository).findByIdAndOwner(1L, owner);
        verify(tagRepository).save(tag1);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent tag")
    void shouldThrowExceptionWhenUpdatingNonExistentTag() {
        // Given
        Tag updatedData = Tag.builder()
                .name("Updated")
                .build();
        when(tagRepository.findByIdAndOwner(999L, owner)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> tagService.updateTag(999L, updatedData, owner))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag")
                .hasMessageContaining("id")
                .hasMessageContaining("999");

        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete tag successfully")
    void shouldDeleteTagSuccessfully() {
        // Given
        when(tagRepository.findByIdAndOwner(1L, owner)).thenReturn(Optional.of(tag1));

        // When
        tagService.deleteTag(1L, owner);

        // Then
        verify(tagRepository).findByIdAndOwner(1L, owner);
        verify(tagRepository).delete(tag1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent tag")
    void shouldThrowExceptionWhenDeletingNonExistentTag() {
        // Given
        when(tagRepository.findByIdAndOwner(999L, owner)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> tagService.deleteTag(999L, owner))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(tagRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should check if tag exists by name")
    void shouldCheckIfTagExistsByName() {
        // Given
        when(tagRepository.existsByNameAndOwner("Spring Boot", owner)).thenReturn(true);
        when(tagRepository.existsByNameAndOwner("Angular", owner)).thenReturn(false);

        // When/Then
        assertThat(tagService.existsByName("Spring Boot", owner)).isTrue();
        assertThat(tagService.existsByName("Angular", owner)).isFalse();
    }

    @Test
    @DisplayName("Should get tags ordered by name")
    void shouldGetTagsOrderedByName() {
        // Given
        when(tagRepository.findAllByOwnerOrderByNameAsc(owner))
                .thenReturn(List.of(tag2, tag1)); // Alphabetical: React, Spring Boot

        // When
        List<Tag> result = tagService.getAllTagsOrderedByName(owner);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("React");
        assertThat(result.get(1).getName()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Should get tags ordered by popularity")
    void shouldGetTagsOrderedByPopularity() {
        // Given
        when(tagRepository.findAllByOwnerOrderByProjectCountDesc(owner))
                .thenReturn(List.of(tag1, tag2)); // Most popular first

        // When
        List<Tag> result = tagService.getAllTagsOrderedByPopularity(owner);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(tag1);
        verify(tagRepository).findAllByOwnerOrderByProjectCountDesc(owner);
    }

    @Test
    @DisplayName("Should get tags with projects")
    void shouldGetTagsWithProjects() {
        // Given
        when(tagRepository.findTagsWithProjectsByOwner(owner))
                .thenReturn(List.of(tag1)); // Only tag1 has projects

        // When
        List<Tag> result = tagService.getTagsWithProjects(owner);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(tag1);
    }

    @Test
    @DisplayName("Should get unused tags")
    void shouldGetUnusedTags() {
        // Given
        when(tagRepository.findUnusedTagsByOwner(owner))
                .thenReturn(List.of(tag2)); // Only tag2 has no projects

        // When
        List<Tag> result = tagService.getUnusedTags(owner);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(tag2);
    }
}
