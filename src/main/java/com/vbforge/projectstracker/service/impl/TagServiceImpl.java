package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags(User owner) {
        log.debug("Getting all tags for user: {}", owner.getUsername());
        return tagRepository.findAllByOwner(owner);
    }

    @Override
    public Optional<Tag> getTagById(Long id, User owner) {
        return tagRepository.findByIdAndOwner(id, owner);
    }

    @Override
    public Optional<Tag> getTagByName(String name, User owner) {
        return tagRepository.findByNameAndOwner(name, owner);
    }

    @Override
    public Tag saveTag(Tag tag) {
        if (tag.getOwner() == null) {
            throw new IllegalArgumentException("Tag must have an owner before saving");
        }
        log.info("Saving tag: {} for user: {}", tag.getName(), tag.getOwner().getUsername());
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, Tag updatedTag, User owner) {
        log.info("Updating tag id={} for user: {}", id, owner.getUsername());
        return tagRepository.findByIdAndOwner(id, owner)
                .map(tag -> {
                    tag.setName(updatedTag.getName());
                    tag.setColor(updatedTag.getColor());
                    tag.setDescription(updatedTag.getDescription());
                    return tagRepository.save(tag);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }

    @Override
    public void deleteTag(Long id, User owner) {
        log.info("Deleting tag id={} for user: {}", id, owner.getUsername());
        Tag tag = tagRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tagRepository.delete(tag);
    }

    @Override
    public boolean existsByName(String name, User owner) {
        return tagRepository.existsByNameAndOwner(name, owner);
    }

    @Override
    public List<Tag> getAllTagsOrderedByName(User owner) {
        return tagRepository.findAllByOwnerOrderByNameAsc(owner);
    }

    @Override
    public List<Tag> getAllTagsOrderedByPopularity(User owner) {
        return tagRepository.findAllByOwnerOrderByProjectCountDesc(owner);
    }

    @Override
    public List<Tag> getTagsWithProjects(User owner) {
        return tagRepository.findTagsWithProjectsByOwner(owner);
    }

    @Override
    public List<Tag> getUnusedTags(User owner) {
        return tagRepository.findUnusedTagsByOwner(owner);
    }
}