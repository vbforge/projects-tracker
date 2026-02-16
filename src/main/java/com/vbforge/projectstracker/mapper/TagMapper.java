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

        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .description(tag.getDescription())
                .createdDate(tag.getCreatedDate())
                .updatedAt(tag.getUpdatedAt())
                .projectCount(projectCount)
                .build();
    }

    /**
     * Convert TagDTO to Tag entity
     *
     * @param dto the DTO
     * @return the entity
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
                .build();
    }

    /**
     * Update existing Tag entity from TagDTO
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
        // Note: createdDate and updatedAt are managed by @PrePersist/@PreUpdate
    }
}
