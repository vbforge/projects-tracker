package com.vbforge.projectstracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.NOT_STARTED;

    @Column(name = "on_github")
    @Builder.Default
    private Boolean onGithub = false;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "local_path", length = 500)
    private String localPath;

    @Column(name = "what_todo", columnDefinition = "TEXT")
    private String whatTodo;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_worked_on")
    private LocalDateTime lastWorkedOn;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-Many relationship with Tags
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "project_tags",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        lastWorkedOn = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for managing tags
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getProjects().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getProjects().remove(this);
    }

    //method for thymeleaf to calculate and cast to LocalDate; property need for table performing
    public long getDaysSinceLastWorked() {
        return ChronoUnit.DAYS.between(lastWorkedOn.toLocalDate(), LocalDate.now());
    }

}
