package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    //---CRUD---
    List<Tag> getAllTags();
    List<Tag> getAllTagsOrderedByName();
    List<Tag> getAllTagsOrderedByPopularity();
    Optional<Tag> getTagById(Long id);
    Optional<Tag> getTagByName(String name);
    Tag createTag(Tag tag);
    Tag updateTag(Long id, Tag updatedTag);
    void deleteTag(Long id);

    //---HELPERS---
    List<Tag> getTagsWithProjects();
    List<Tag> getUnusedTags();
    boolean tagExists(String name);
    long getTotalTagCount();
    Tag findOrCreateTag(String name, String color);

}
