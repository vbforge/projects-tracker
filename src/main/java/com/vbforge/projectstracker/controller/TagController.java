package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.service.TagService;
import com.vbforge.projectstracker.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String listTags(Model model) {
        User currentUser = securityUtils.getCurrentUser();

        var allTags = tagService.getAllTagsOrderedByPopularity(currentUser);

        model.addAttribute("tags", allTags);
        model.addAttribute("totalTags", allTags.size());

        model.addAttribute("tagDTO", new TagDTO());

        return "tags";
    }

    @GetMapping("/new")
    public String showNewTagForm(Model model) {
        model.addAttribute("tagDTO", new TagDTO());
        return "tags";
    }

    @PostMapping("/add")
    public String addTag(
            @Valid @ModelAttribute("tagDTO") TagDTO tagDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        User currentUser = securityUtils.getCurrentUser();

        if (bindingResult.hasErrors()) {
            return "tags";
        }

        try {
            // Check if tag name already exists for this user
            if (tagService.existsByName(tagDTO.getName(), currentUser)) {
                model.addAttribute("errorMessage", "Tag '" + tagDTO.getName() + "' already exists!");
                return "tags";
            }

            // Create Tag entity from DTO
            Tag tag = Tag.builder()
                    .name(tagDTO.getName())
                    .color(tagDTO.getColor())
                    .description(tagDTO.getDescription())
                    .owner(currentUser)
                    .build();

            tagService.saveTag(tag);
            redirectAttributes.addFlashAttribute("success", "Tag created successfully!");
            return "redirect:/tags";

        } catch (Exception e) {
            log.error("Error creating tag for user {}", currentUser.getUsername(), e);
            model.addAttribute("errorMessage", "Error creating tag: " + e.getMessage());
            return "tags";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditTagForm(@PathVariable Long id, Model model) {
        User currentUser = securityUtils.getCurrentUser();

        Tag tag = tagService.getTagById(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        // Use builder instead of non-existent constructor
        TagDTO tagDTO = TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .description(tag.getDescription())
                .createdDate(tag.getCreatedDate())
                .updatedAt(tag.getUpdatedAt())
                .projectCount(tag.getProjects() != null ? tag.getProjects().size() : 0)
                .build();

        model.addAttribute("tagDTO", tagDTO);
        model.addAttribute("tagId", id);

        return "tags";
    }

    @PostMapping("/{id}/update")
    public String updateTag(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String color,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {

        User currentUser = securityUtils.getCurrentUser();

        try {
            // Create updated Tag entity
            Tag updatedTag = Tag.builder()
                    .name(name)
                    .color(color)
                    .description(description)
                    .build();

            tagService.updateTag(id, updatedTag, currentUser);
            redirectAttributes.addFlashAttribute("success", "Tag updated successfully!");
            return "redirect:/tags";

        } catch (Exception e) {
            log.error("Error updating tag for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating tag: " + e.getMessage());
            return "redirect:/tags";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = securityUtils.getCurrentUser();

        try {
            tagService.deleteTag(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "Tag deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting tag for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("error", "Error deleting tag: " + e.getMessage());
        }

        return "redirect:/tags";
    }
}
