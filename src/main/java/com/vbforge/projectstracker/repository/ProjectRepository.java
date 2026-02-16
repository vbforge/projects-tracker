package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    //find projects by status
    List<Project> findByStatus(ProjectStatus status);

    //find projects by on GitHub status
    List<Project> findByOnGithub(Boolean onGithub);

    //find projects by title containing (case-insensitive search)
    List<Project> findByTitleContainingIgnoreCase(String title);

    //find projects created between dates
    List<Project> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    //find projects by last worked on between dates
    List<Project> findByLastWorkedOnBetween(LocalDateTime start, LocalDateTime end);

    //find projects by tag name
    @Query("SELECT p FROM Project p JOIN p.tags t WHERE t.name = :tagName")
    List<Project> findByTagName(@Param("tagName") String tagName);

    // Find projects by multiple tag names
    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE t.name IN :tagNames")
    List<Project> findByTagNames(@Param("tagNames") List<String> tagNames);

    //find projects by ProjectStatus and GitHub status
    List<Project> findByStatusAndOnGithub(ProjectStatus status, Boolean onGithub);

    //count projects by status
    long countByStatus(ProjectStatus status);

    //count projects on GitHub
    long countByOnGithub(Boolean onGithub);

    //custom query: searching by multi-filter
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.tags t " +
            "WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')))" +
            "AND (:status IS NULL OR p.status = :status)" +
            "AND (:onGithub IS NULL OR p.onGithub = :onGithub)" +
            "AND (:tagName IS NULL OR t.name = :tagName)")
    List<Project> searchProjects(
            @Param("title") String title,
            @Param("status") ProjectStatus status,
            @Param("onGithub") Boolean onGithub,
            @Param("tagName") String tagName
    );

    // Find all projects ordered by last worked on (most recent first)
    List<Project> findAllByOrderByLastWorkedOnDesc();

    // Find all projects ordered by created date (newest first)
    List<Project> findAllByOrderByCreatedDateDesc();



}
