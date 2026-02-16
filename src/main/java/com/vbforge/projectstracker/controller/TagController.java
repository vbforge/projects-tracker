package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.mapper.TagMapper;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for tag management
 */
@Slf4j
@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    /**
     * Show all tags page with create form
     */
    @GetMapping
    public String listTags(Model model) {
        log.debug("Listing all tags");

        List<TagDTO> tags = tagService.getAllTagsOrderedByName().stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());

        // Create empty DTO for the create form
        TagDTO newTag = new TagDTO();
        newTag.setColor("#e7f3ff"); // Default color

        model.addAttribute("tags", tags);
        model.addAttribute("newTag", newTag);

        return "tags";
    }

    /**
     * Create new tag
     */
    @PostMapping("/create")
    public String createTag(
            @Valid @ModelAttribute("newTag") TagDTO tagDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Creating new tag: {}", tagDTO.getName());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors creating tag: {}", bindingResult.getAllErrors());

            // Get all tags for the list
            List<TagDTO> tags = tagService.getAllTagsOrderedByName().stream()
                    .map(tagMapper::toDTO)
                    .collect(Collectors.toList());

            model.addAttribute("tags", tags);
            model.addAttribute("error", "Please correct the errors: " +
                    bindingResult.getFieldError().getDefaultMessage());

            return "tags";
        }

        try {
            // Convert DTO to entity
            Tag tag = tagMapper.toEntity(tagDTO);

            // Create tag
            Tag createdTag = tagService.createTag(tag);

            log.info("Tag created successfully: {}", createdTag.getName());
            redirectAttributes.addFlashAttribute("success",
                    "Tag '" + createdTag.getName() + "' created successfully!");

        } catch (Exception e) {
            log.error("Error creating tag: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error creating tag: " + e.getMessage());
        }

        return "redirect:/tags";
    }

    /**
     * Update existing tag
     */
    @PostMapping("/{id}/update")
    public String updateTag(
            @PathVariable Long id,
            @Valid @ModelAttribute TagDTO tagDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Updating tag ID: {}", id);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors updating tag: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("error",
                    "Validation error: " + bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/tags";
        }

        try {
            // Convert DTO to entity
            Tag tag = tagMapper.toEntity(tagDTO);

            // Update tag
            Tag updatedTag = tagService.updateTag(id, tag);

            log.info("Tag updated successfully: {}", updatedTag.getName());
            redirectAttributes.addFlashAttribute("success",
                    "Tag '" + updatedTag.getName() + "' updated successfully!");

        } catch (Exception e) {
            log.error("Error updating tag: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error updating tag: " + e.getMessage());
        }

        return "redirect:/tags";
    }

    /**
     * Delete tag
     */
    @PostMapping("/{id}/delete")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting tag ID: {}", id);

        try {
            // Get tag name before deletion for success message
            Tag tag = tagService.getTagById(id)
                    .orElseThrow(() -> new RuntimeException("Tag not found"));
            String tagName = tag.getName();

            // Delete tag
            tagService.deleteTag(id);

            log.info("Tag deleted successfully: {}", tagName);
            redirectAttributes.addFlashAttribute("success",
                    "Tag '" + tagName + "' deleted successfully!");

        } catch (Exception e) {
            log.error("Error deleting tag: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error deleting tag: " + e.getMessage());
        }

        return "redirect:/tags";
    }
}
