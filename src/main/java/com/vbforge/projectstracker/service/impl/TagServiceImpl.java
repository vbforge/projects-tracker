package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.exception.DuplicateResourceException;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    public static final String TAG_COLOR_DEFAULT = "#e7f3ff";
    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        log.debug("Getting all tags");
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getAllTagsOrderedByName() {
        log.debug("Getting all tags ordered by name");
        return tagRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Tag> getAllTagsOrderedByPopularity() {
        log.debug("Getting all tags ordered by popularity");
        return tagRepository.findAllOrderByProjectCountDesc(); //most popular first
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        log.debug("Getting tag by id: {}", id);
        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> getTagByName(String name) {
        log.debug("Getting tag by name: {}", name);
        return tagRepository.findByName(name);
    }

    @Override
    public Tag createTag(Tag tag) {
        log.info("Creating new tag: {}", tag.getName());

        //check if tag with same name is already exist
        if(tagRepository.existsByName(tag.getName())) {
            log.error("Tag with name {} already exists", tag.getName());
            throw new DuplicateResourceException("Tag", "name", tag.getName());
        }

        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, Tag updatedTag) {
        log.info("Updating tag by id: {}", id);
        return tagRepository.findById(id)
                .map(tag->{
                    //check if new name conflicts with another tag
                    if(!tag.getName().equals(updatedTag.getName()) && tagRepository.existsByName(updatedTag.getName())) { //with (!) updating only color available
                        log.error("Tag already exists with name: {}", updatedTag.getName());
                        throw new DuplicateResourceException("Tag", "name", updatedTag.getName());
                    }
                    tag.setName(updatedTag.getName());
                    tag.setColor(updatedTag.getColor());
                    tag.setDescription(updatedTag.getDescription());
                    return tagRepository.save(tag);
                })
                .orElseThrow(()-> {
                    log.error("No tag found with id: {}", id);
                    return new ResourceNotFoundException("Tag", "id", id);
                });
    }

    @Override
    public void deleteTag(Long id) {
        log.info("Deleting tag with id: {}", id);

        if (!tagRepository.existsById(id)) {
            log.error("Tag not found with id: {}", id);
            throw new ResourceNotFoundException("Tag", "id", id);
        }

        tagRepository.deleteById(id);

    }

    @Override
    public List<Tag> getTagsWithProjects() {
        log.debug("Getting tags with projects");
        return tagRepository.findTagsWithProjects();
    }

    @Override
    public List<Tag> getUnusedTags() {
        log.debug("Getting unused tags");
        return tagRepository.findUnusedTags();
    }

    @Override
    public boolean tagExists(String name) {
        return tagRepository.existsByName(name);
    }

    @Override
    public long getTotalTagCount() {
        return tagRepository.count();
    }

    @Override
    public Tag findOrCreateTag(String name, String color) {
        log.debug("Getting or creating tag by name: {}", name);

        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    log.info("Tag not found, creating new tag: {}", name);
                    Tag newTag = Tag.builder()
                            .name(name)
                            .color(color != null ? color : TAG_COLOR_DEFAULT)
                            .build();
                    return tagRepository.save(newTag);
                });
    }


}











