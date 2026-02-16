package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    //find tag by name
    Optional<Tag> findByName(String name);

    //find tag by name (case-insensitive)
    Optional<Tag> findByNameIgnoreCase(String name);

    //check if tag exists by name
    boolean existsByName(String name);

    //find all tags ordered by name asc
    List<Tag> findAllByOrderByNameAsc();

    //get tags with project count (using native query for better performance)
    @Query("SELECT t FROM Tag t LEFT JOIN t.projects p GROUP BY t ORDER BY COUNT (p)")
    List<Tag> findAllOrderByProjectCountDesc();

    //find tags that have at least one project
    @Query("SELECT DISTINCT t FROM Tag t WHERE SIZE(t.projects) > 0")
    List<Tag> findTagsWithProjects();

    //find unused tags (no projects assigned)
    @Query("SELECT t FROM Tag t WHERE SIZE(t.projects) = 0")
    List<Tag> findUnusedTags();

}
