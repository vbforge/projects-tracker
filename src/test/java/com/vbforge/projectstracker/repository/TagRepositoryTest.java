package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TagRepository Tests")
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User user1;
    private User user2;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        // Create users
        user1 = User.builder()
                .username("testuser1")
                .email("test1@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        user2 = userRepository.save(user2);

        // Create tags for user1
        tag1 = Tag.builder()
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .owner(user1)
                .build();
        tag1 = tagRepository.save(tag1);

        tag2 = Tag.builder()
                .name("React")
                .color("#61dafb")
                .description("JS library")
                .owner(user1)
                .build();
        tag2 = tagRepository.save(tag2);

        tag3 = Tag.builder()
                .name("Docker")
                .color("#2496ed")
                .description("Containerization")
                .owner(user1)
                .build();
        tag3 = tagRepository.save(tag3);
    }

    @Test
    @DisplayName("Should find all tags by owner")
    void shouldFindAllByOwner() {
        // When
        List<Tag> tags = tagRepository.findAllByOwner(user1);

        // Then
        assertThat(tags).hasSize(3);
        assertThat(tags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("Spring Boot", "React", "Docker");
    }

    @Test
    @DisplayName("Should find tag by ID and owner")
    void shouldFindByIdAndOwner() {
        // When
        Optional<Tag> found = tagRepository.findByIdAndOwner(tag1.getId(), user1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Should not find tag of different owner")
    void shouldNotFindTagOfDifferentOwner() {
        // Given - User2 creates a tag with same name
        Tag user2Tag = Tag.builder()
                .name("Spring Boot")
                .color("#000000")
                .owner(user2)
                .build();
        user2Tag = tagRepository.save(user2Tag);

        // When - User1 tries to find user2's tag
        Optional<Tag> found = tagRepository.findByIdAndOwner(user2Tag.getId(), user1);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find tag by name and owner")
    void shouldFindByNameAndOwner() {
        // When
        Optional<Tag> found = tagRepository.findByNameAndOwner("Spring Boot", user1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Java framework");
    }

    @Test
    @DisplayName("Should find tag by name ignore case")
    void shouldFindByNameIgnoreCaseAndOwner() {
        // When
        Optional<Tag> found = tagRepository.findByNameIgnoreCaseAndOwner("spring boot", user1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Should check if tag exists by name and owner")
    void shouldCheckExistsByNameAndOwner() {
        // When
        boolean exists = tagRepository.existsByNameAndOwner("Spring Boot", user1);
        boolean notExists = tagRepository.existsByNameAndOwner("Angular", user1);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should allow same tag name for different users")
    void shouldAllowSameTagNameForDifferentUsers() {
        // Given - User2 creates a tag with same name as user1
        Tag user2Tag = Tag.builder()
                .name("Spring Boot")
                .color("#000000")
                .description("Different description")
                .owner(user2)
                .build();

        // When
        Tag saved = tagRepository.save(user2Tag);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(tagRepository.findByNameAndOwner("Spring Boot", user1)).isPresent();
        assertThat(tagRepository.findByNameAndOwner("Spring Boot", user2)).isPresent();
    }

    @Test
    @DisplayName("Should find all tags ordered by name ascending")
    void shouldFindAllByOwnerOrderByNameAsc() {
        // When
        List<Tag> tags = tagRepository.findAllByOwnerOrderByNameAsc(user1);

        // Then
        assertThat(tags).hasSize(3);
        assertThat(tags).extracting(Tag::getName)
                .containsExactly("Docker", "React", "Spring Boot"); // Alphabetical
    }

    @Test
    @DisplayName("Should find tags ordered by project count descending")
    void shouldFindAllByOwnerOrderByProjectCountDesc() {
        // Given - Create projects with tags
        Project p1 = Project.builder()
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(user1)
                .build();
        p1.addTag(tag1); // Spring Boot
        p1.addTag(tag2); // React
        projectRepository.save(p1);

        Project p2 = Project.builder()
                .title("Project 2")
                .status(ProjectStatus.DONE)
                .owner(user1)
                .build();
        p2.addTag(tag1); // Spring Boot (again)
        projectRepository.save(p2);

        // When
        List<Tag> tags = tagRepository.findAllByOwnerOrderByProjectCountDesc(user1);

        // Then
        assertThat(tags).hasSize(3);
        assertThat(tags.get(0).getName()).isEqualTo("Spring Boot"); // 2 projects
        assertThat(tags.get(1).getName()).isEqualTo("React");       // 1 project
        assertThat(tags.get(2).getName()).isEqualTo("Docker");      // 0 projects
    }

    @Test
    @DisplayName("Should find tags with at least one project")
    void shouldFindTagsWithProjectsByOwner() {
        // Given
        Project p1 = Project.builder()
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(user1)
                .build();
        p1.addTag(tag1);
        p1.addTag(tag2);
        projectRepository.save(p1);
        // tag3 has no projects

        // When
        List<Tag> tagsWithProjects = tagRepository.findTagsWithProjectsByOwner(user1);

        // Then
        assertThat(tagsWithProjects).hasSize(2);
        assertThat(tagsWithProjects).extracting(Tag::getName)
                .containsExactlyInAnyOrder("Spring Boot", "React");
    }

    @Test
    @DisplayName("Should find unused tags")
    void shouldFindUnusedTagsByOwner() {
        // Given
        Project p1 = Project.builder()
                .title("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(user1)
                .build();
        p1.addTag(tag1);
        projectRepository.save(p1);
        // tag2 and tag3 have no projects

        // When
        List<Tag> unusedTags = tagRepository.findUnusedTagsByOwner(user1);

        // Then
        assertThat(unusedTags).hasSize(2);
        assertThat(unusedTags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("React", "Docker");
    }

    @Test
    @DisplayName("Should enforce data isolation between users")
    void shouldEnforceDataIsolation() {
        // When
        List<Tag> user1Tags = tagRepository.findAllByOwner(user1);
        List<Tag> user2Tags = tagRepository.findAllByOwner(user2);

        // Then
        assertThat(user1Tags).hasSize(3);
        assertThat(user2Tags).isEmpty();
    }

    @Test
    @DisplayName("Should delete tag without affecting other user's tags")
    void shouldDeleteTagWithoutAffectingOtherUsers() {
        // Given - User2 creates tag with same name
        Tag user2Tag = Tag.builder()
                .name("Spring Boot")
                .color("#000000")
                .owner(user2)
                .build();
        user2Tag = tagRepository.save(user2Tag);

        // When - Delete user1's tag
        tagRepository.delete(tag1);

        // Then
        assertThat(tagRepository.findByIdAndOwner(tag1.getId(), user1)).isEmpty();
        assertThat(tagRepository.findByIdAndOwner(user2Tag.getId(), user2)).isPresent();
    }
}