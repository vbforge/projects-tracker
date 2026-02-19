package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;

import java.util.List;
import java.util.Optional;

public interface TagService {


    List<Tag> getAllTags(User owner);

    Optional<Tag> getTagById(Long id, User owner);

    Optional<Tag> getTagByName(String name, User owner);

    Tag saveTag(Tag tag);

    Tag updateTag(Long id, Tag updatedTag, User owner);

    void deleteTag(Long id, User owner);

    boolean existsByName(String name, User owner);

    List<Tag> getAllTagsOrderedByName(User owner);

    List<Tag> getAllTagsOrderedByPopularity(User owner);

    List<Tag> getTagsWithProjects(User owner);

    List<Tag> getUnusedTags(User owner);
}
