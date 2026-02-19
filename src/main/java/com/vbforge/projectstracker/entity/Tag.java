package com.vbforge.projectstracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "tags",
        // A tag name must be unique PER USER, not globally
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No longer globally unique — unique per user (enforced by uniqueConstraints above)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 7)
    @Builder.Default
    private String color = "#e7f3ff";   //blue

    @Column(length = 250)
    private String description;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Owner — every tag belongs to exactly one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    // Many-to-Many relationship with Projects
    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Project> projects = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && Objects.equals(name, tag.name) && Objects.equals(color, tag.color) && Objects.equals(description, tag.description) && Objects.equals(createdDate, tag.createdDate) && Objects.equals(updatedAt, tag.updatedAt) && Objects.equals(owner, tag.owner) && Objects.equals(projects, tag.projects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, description, createdDate, updatedAt, owner, projects);
    }
}
