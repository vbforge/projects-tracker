package com.vbforge.projectstracker.integration;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full Integration Test - Complete User Journey
 * Tests the entire application stack working together:
 * - Database (H2 in-memory)
 * - JPA entities and relationships
 * - Repositories
 * - Services
 * - Security (password encoding, data isolation)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Complete User Journey Integration Test")
class CompleteUserJourneyIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Clean slate
        projectRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Journey 1: User Registration and Login")
    void journey1_UserRegistrationAndLogin() {
        // STEP 1: Register user
        User newUser = User.builder()
                .username("john.doe")
                .email("john@example.com")
                .password(passwordEncoder.encode("securePassword123"))
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("john.doe");
        assertThat(savedUser.getCreatedAt()).isNotNull();

        // STEP 2: Verify password is encoded
        assertThat(savedUser.getPassword()).isNotEqualTo("securePassword123");
        assertThat(passwordEncoder.matches("securePassword123", savedUser.getPassword())).isTrue();

        // STEP 3: Find user by username (simulating login)
        Optional<User> foundUser = userRepository.findByUsername("john.doe");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @Order(2)
    @DisplayName("Journey 2: Create Tags and Projects")
    void journey2_CreateTagsAndProjects() {
        // GIVEN: User is registered
        User user = createAndSaveUser("alice", "alice@example.com");

        // STEP 1: Create tags
        Tag springTag = Tag.builder()
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .owner(user)
                .build();
        Tag reactTag = Tag.builder()
                .name("React")
                .color("#61dafb")
                .description("JS library")
                .owner(user)
                .build();

        Tag savedSpring = tagRepository.save(springTag);
        Tag savedReact = tagRepository.save(reactTag);

        assertThat(savedSpring.getId()).isNotNull();
        assertThat(savedReact.getId()).isNotNull();

        // STEP 2: Create project with tags
        Project project = Project.builder()
                .title("My Full Stack App")
                .description("Learning Spring Boot + React")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(true)
                .githubUrl("https://github.com/alice/fullstack-app")
                .owner(user)
                .build();

        project.addTag(savedSpring);
        project.addTag(savedReact);

        Project savedProject = projectRepository.save(project);

        // VERIFY: Project saved with tags
        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getTags()).hasSize(2);
        assertThat(savedProject.getCreatedDate()).isNotNull();
        assertThat(savedProject.getLastWorkedOn()).isNotNull();

        // VERIFY: Tags know about project (bidirectional relationship)
        Tag foundSpring = tagRepository.findById(savedSpring.getId()).orElseThrow();
        assertThat(foundSpring.getProjects()).hasSize(1);
        assertThat(foundSpring.getProjects()).contains(savedProject);
    }

    @Test
    @Order(3)
    @DisplayName("Journey 3: Data Isolation Between Users")
    void journey3_DataIsolationBetweenUsers() {
        // GIVEN: Two users with their own data
        user1 = createAndSaveUser("bob", "bob@example.com");
        user2 = createAndSaveUser("charlie", "charlie@example.com");

        // User 1 creates tags and projects
        Tag user1Tag = createAndSaveTag("Docker", "#2496ed", user1);
        Project user1Project = createAndSaveProject("Bob's Project", user1);
        user1Project.addTag(user1Tag);
        projectRepository.save(user1Project);

        // User 2 creates tags and projects
        Tag user2Tag = createAndSaveTag("Kubernetes", "#326ce5", user2);
        Project user2Project = createAndSaveProject("Charlie's Project", user2);
        user2Project.addTag(user2Tag);
        projectRepository.save(user2Project);

        // VERIFY: User 1 can only see their data
        List<Project> user1Projects = projectRepository.findAllByOwner(user1);
        assertThat(user1Projects).hasSize(1);
        assertThat(user1Projects.get(0).getTitle()).isEqualTo("Bob's Project");

        List<Tag> user1Tags = tagRepository.findAllByOwner(user1);
        assertThat(user1Tags).hasSize(1);
        assertThat(user1Tags.get(0).getName()).isEqualTo("Docker");

        // VERIFY: User 2 can only see their data
        List<Project> user2Projects = projectRepository.findAllByOwner(user2);
        assertThat(user2Projects).hasSize(1);
        assertThat(user2Projects.get(0).getTitle()).isEqualTo("Charlie's Project");

        List<Tag> user2Tags = tagRepository.findAllByOwner(user2);
        assertThat(user2Tags).hasSize(1);
        assertThat(user2Tags.get(0).getName()).isEqualTo("Kubernetes");

        // CRITICAL: User 1 cannot access User 2's project by ID
        Optional<Project> user2ProjectAsUser1 = projectRepository.findByIdAndOwner(
                user2Project.getId(), user1);
        assertThat(user2ProjectAsUser1).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Journey 4: Per-User Tag Uniqueness")
    void journey4_PerUserTagUniqueness() {
        // GIVEN: Two users
        user1 = createAndSaveUser("dave", "dave@example.com");
        user2 = createAndSaveUser("eve", "eve@example.com");

        // WHEN: Both users create a tag with the same name
        Tag dave_Java = createAndSaveTag("Java", "#007396", user1);
        Tag eve_Java = createAndSaveTag("Java", "#007396", user2);

        // THEN: Both tags should exist (different owners)
        assertThat(dave_Java.getId()).isNotNull();
        assertThat(eve_Java.getId()).isNotNull();
        assertThat(dave_Java.getId()).isNotEqualTo(eve_Java.getId());

        // VERIFY: Each user can find their own "Java" tag
        Optional<Tag> daveJavaTag = tagRepository.findByNameAndOwner("Java", user1);
        Optional<Tag> eveJavaTag = tagRepository.findByNameAndOwner("Java", user2);

        assertThat(daveJavaTag).isPresent();
        assertThat(eveJavaTag).isPresent();
        assertThat(daveJavaTag.get().getOwner()).isEqualTo(user1);
        assertThat(eveJavaTag.get().getOwner()).isEqualTo(user2);
    }

    @Test
    @Order(5)
    @DisplayName("Journey 5: Update Project and Tags")
    void journey5_UpdateProjectAndTags() {
        // GIVEN: User with project
        User user = createAndSaveUser("frank", "frank@example.com");
        Tag oldTag = createAndSaveTag("Old Tech", "#ff0000", user);
        Tag newTag = createAndSaveTag("New Tech", "#00ff00", user);

        Project project = createAndSaveProject("My App", user);
        project.addTag(oldTag);
        project = projectRepository.save(project);

        assertThat(project.getTags()).hasSize(1);
        assertThat(project.getTags()).contains(oldTag);

        // WHEN: Update project - replace old tag with new tag
        project.removeTag(oldTag);
        project.addTag(newTag);
        project.setStatus(ProjectStatus.DONE);
        project = projectRepository.save(project);

        // THEN: Project has new tag
        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        assertThat(updatedProject.getTags()).hasSize(1);
        assertThat(updatedProject.getTags()).contains(newTag);
        assertThat(updatedProject.getTags()).doesNotContain(oldTag);
        assertThat(updatedProject.getStatus()).isEqualTo(ProjectStatus.DONE);
    }

    @Test
    @Order(6)
    @DisplayName("Journey 6: Delete Tag Does Not Delete Projects")
    void journey6_DeleteTagDoesNotDeleteProjects() {
        // GIVEN: User with project and tag
        User user = createAndSaveUser("grace", "grace@example.com");
        Tag tag = createAndSaveTag("Temporary", "#123456", user);
        Project project = createAndSaveProject("My Project", user);
        
        project.addTag(tag);
        project = projectRepository.save(project);

        Long projectId = project.getId();
        Long tagId = tag.getId();

        assertThat(project.getTags()).hasSize(1);

        // WHEN: Delete tag
        project.removeTag(tag);
        tagRepository.delete(tag);
        tagRepository.flush();

        // THEN: Project still exists but tag is removed
        Optional<Project> foundProject = projectRepository.findById(projectId);
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getTags()).hasSize(0);

        // Tag is deleted
        tagRepository.delete(tag); //delete tag after reassign tag from project
        Optional<Tag> foundTag = tagRepository.findById(tagId);
        assertThat(foundTag).isEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("Journey 7: Statistics and Aggregations")
    void journey7_StatisticsAndAggregations() {
        // GIVEN: User with multiple projects in different states
        User user = createAndSaveUser("henry", "henry@example.com");

        createAndSaveProject("Not Started 1", user, ProjectStatus.NOT_STARTED);
        createAndSaveProject("Not Started 2", user, ProjectStatus.NOT_STARTED);
        createAndSaveProject("In Progress 1", user, ProjectStatus.IN_PROGRESS);
        createAndSaveProject("In Progress 2", user, ProjectStatus.IN_PROGRESS);
        createAndSaveProject("In Progress 3", user, ProjectStatus.IN_PROGRESS);
        createAndSaveProject("Done 1", user, ProjectStatus.DONE);
        createAndSaveProject("Done 2", user, ProjectStatus.DONE);

        // WHEN: Query statistics
        long total = projectRepository.countByOwner(user);
        long notStarted = projectRepository.countByStatusAndOwner(ProjectStatus.NOT_STARTED, user);
        long inProgress = projectRepository.countByStatusAndOwner(ProjectStatus.IN_PROGRESS, user);
        long done = projectRepository.countByStatusAndOwner(ProjectStatus.DONE, user);

        // THEN: Counts are correct
        assertThat(total).isEqualTo(7);
        assertThat(notStarted).isEqualTo(2);
        assertThat(inProgress).isEqualTo(3);
        assertThat(done).isEqualTo(2);

        // Completion rate
        double completionRate = (done * 100.0) / total;
        assertThat(completionRate).isEqualTo(28.57, org.assertj.core.data.Offset.offset(0.01));
    }

    // ========== HELPER METHODS ==========

    private User createAndSaveUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private Tag createAndSaveTag(String name, String color, User owner) {
        Tag tag = Tag.builder()
                .name(name)
                .color(color)
                .owner(owner)
                .build();
        return tagRepository.save(tag);
    }

    private Project createAndSaveProject(String title, User owner) {
        return createAndSaveProject(title, owner, ProjectStatus.IN_PROGRESS);
    }

    private Project createAndSaveProject(String title, User owner, ProjectStatus status) {
        Project project = Project.builder()
                .title(title)
                .description("Test project")
                .status(status)
                .onGithub(false)
                .owner(owner)
                .build();
        return projectRepository.save(project);
    }
}
