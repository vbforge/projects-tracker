package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // All queries scoped to owner
    List<Tag> findAllByOwner(User owner);

    Optional<Tag> findByIdAndOwner(Long id, User owner);

    Optional<Tag> findByNameAndOwner(String name, User owner);

    Optional<Tag> findByNameIgnoreCaseAndOwner(String name, User owner);

    boolean existsByNameAndOwner(String name, User owner);

    List<Tag> findAllByOwnerOrderByNameAsc(User owner);

    // Ordered by popularity (most used tags first)
    @Query("SELECT t FROM Tag t LEFT JOIN t.projects p WHERE t.owner = :owner " +
            "GROUP BY t ORDER BY COUNT(p) DESC")
    List<Tag> findAllByOwnerOrderByProjectCountDesc(@Param("owner") User owner);

    // Tags with at least one project
    @Query("SELECT DISTINCT t FROM Tag t WHERE t.owner = :owner AND SIZE(t.projects) > 0")
    List<Tag> findTagsWithProjectsByOwner(@Param("owner") User owner);

    // Unused tags
    @Query("SELECT t FROM Tag t WHERE t.owner = :owner AND SIZE(t.projects) = 0")
    List<Tag> findUnusedTagsByOwner(@Param("owner") User owner);
}
