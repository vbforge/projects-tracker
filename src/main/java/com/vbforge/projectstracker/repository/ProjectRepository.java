package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // All queries scoped to owner
    List<Project> findAllByOwner(User owner);

    Optional<Project> findByIdAndOwner(Long id, User owner);

    List<Project> findByStatusAndOwner(ProjectStatus status, User owner);

    List<Project> findByOnGithubAndOwner(Boolean onGithub, User owner);

    List<Project> findByTitleContainingIgnoreCaseAndOwner(String title, User owner);

    List<Project> findByCreatedDateBetweenAndOwner(LocalDateTime start, LocalDateTime end, User owner);

    List<Project> findByLastWorkedOnBetweenAndOwner(LocalDateTime start, LocalDateTime end, User owner);

    List<Project> findAllByOwnerOrderByLastWorkedOnDesc(User owner);

    List<Project> findAllByOwnerOrderByCreatedDateDesc(User owner);

    // Count queries
    long countByOwner(User owner);

    long countByStatusAndOwner(ProjectStatus status, User owner);

    long countByOnGithubAndOwner(Boolean onGithub, User owner);

    // Tag-based queries
    @Query("SELECT p FROM Project p JOIN p.tags t WHERE t.name = :tagName AND p.owner = :owner")
    List<Project> findByTagNameAndOwner(@Param("tagName") String tagName, @Param("owner") User owner);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE t.name IN :tagNames AND p.owner = :owner")
    List<Project> findByTagNamesAndOwner(@Param("tagNames") List<String> tagNames, @Param("owner") User owner);

    // Multi-filter search
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.tags t " +
            "WHERE p.owner = :owner " +
            "AND (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:onGithub IS NULL OR p.onGithub = :onGithub) " +
            "AND (:tagName IS NULL OR t.name = :tagName)")
    List<Project> searchProjects(
            @Param("owner") User owner,
            @Param("title") String title,
            @Param("status") ProjectStatus status,
            @Param("onGithub") Boolean onGithub,
            @Param("tagName") String tagName
    );
}