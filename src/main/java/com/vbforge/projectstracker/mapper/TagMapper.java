package com.vbforge.projectstracker.mapper;

import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.entity.Tag;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Tag entity and TagDTO
 */
@Component
public class TagMapper {

    /**
     * Convert Tag entity to TagDTO
     *
     * @param tag the entity
     * @return the DTO
     */
    public TagDTO toDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        Integer projectCount = tag.getProjects() != null ? tag.getProjects().size() : 0;

        // Using builder (if TagDTO has Lombok @Builder)
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .description(tag.getDescription())
                .createdDate(tag.getCreatedDate())
                .updatedAt(tag.getUpdatedAt())
                .projectCount(projectCount)
                .build();

        // OR using constructor (if TagDTO doesn't have Lombok @Builder)
        // return new TagDTO(
        //     tag.getId(),
        //     tag.getName(),
        //     tag.getColor(),
        //     tag.getDescription(),
        //     tag.getCreatedDate(),
        //     tag.getUpdatedAt(),
        //     projectCount
        // );
    }

    /**
     * Convert TagDTO to Tag entity
     * NOTE: Owner must be set separately in the service layer!
     * This is by design - the owner should come from SecurityContext, not from DTO.
     *
     * @param dto the DTO
     * @return the entity (without owner set)
     */
    public Tag toEntity(TagDTO dto) {
        if (dto == null) {
            return null;
        }

        return Tag.builder()
                .id(dto.getId())
                .name(dto.getName())
                .color(dto.getColor())
                .description(dto.getDescription())
                // NOTE: owner is NOT set here - it must be set in the service layer
                .build();
    }

    /**
     * Update existing Tag entity from TagDTO
     * Does NOT update owner - owner is immutable after creation
     *
     * @param tag the existing entity
     * @param dto the DTO with new values
     */
    public void updateEntityFromDTO(Tag tag, TagDTO dto) {
        if (tag == null || dto == null) {
            return;
        }

        tag.setName(dto.getName());
        tag.setColor(dto.getColor());
        tag.setDescription(dto.getDescription());
        // Note: createdDate, updatedAt are managed by @PrePersist/@PreUpdate
        // Note: owner is NOT updated - it's immutable
    }
}