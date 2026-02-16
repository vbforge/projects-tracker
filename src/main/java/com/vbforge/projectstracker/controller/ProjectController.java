package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.dto.ProjectDTO;
import com.vbforge.projectstracker.dto.TagDTO;
import com.vbforge.projectstracker.mapper.ProjectMapper;
import com.vbforge.projectstracker.mapper.TagMapper;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.service.ProjectFilterService;
import com.vbforge.projectstracker.service.ProjectService;
import com.vbforge.projectstracker.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Project CRUD operations and dashboard
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final TagService tagService;
    private final ProjectFilterService filterService;
    private final ProjectMapper projectMapper;
    private final TagMapper tagMapper;

    /**
     * Dashboard - Main page with project listing, filters, and statistics
     */
    @GetMapping({"/", "/projects"})
    public String dashboard(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Boolean onGithub,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String createdMonth,
            @RequestParam(required = false) String lastWorkedMonth,
            @RequestParam(required = false, defaultValue = "lastWorked") String sortBy,
            Model model) {

        log.debug("Dashboard accessed with filters - search: {}, status: {}, tags: {}", search, status, tags);

        // Get filtered and sorted projects using FilterService
        List<Project> projects = filterService.getFilteredAndSortedProjects(
                search, status, onGithub, tags, createdMonth, lastWorkedMonth, sortBy
        );

        // Convert to DTOs
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());

        // Calculate statistics
        long totalProjects = projectService.getTotalProjectCount();
        long completedProjects = projectService.getCompletedProjectCount();
        long inProgressProjects = projectService.getInProgressProjectCount();
        long notStartedProjects = projectService.getNotStartedProjectCount();
        long githubProjects = projectService.getGithubProjectCount();

        // Calculate rates
        int completionRate = totalProjects == 0 ? 0 : (int) ((completedProjects * 100.0) / totalProjects);
        int githubRate = totalProjects == 0 ? 0 : (int) ((githubProjects * 100.0) / totalProjects);
        int inProgressRate = totalProjects == 0 ? 0 : (int) ((inProgressProjects * 100.0) / totalProjects);
        int notStartedRate = totalProjects == 0 ? 0 : (int) ((notStartedProjects * 100.0) / totalProjects);

        // Get all tags for filter section
        List<TagDTO> allTags = tagService.getAllTagsOrderedByPopularity().stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());

        // Check if filters are active
        boolean hasActiveFilters = filterService.hasActiveFilters(
                search, status, onGithub, tags, createdMonth, lastWorkedMonth
        );

        // Add data to model
        model.addAttribute("projects", projectDTOs);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("completedProjects", completedProjects);
        model.addAttribute("inProgressProjects", inProgressProjects);
        model.addAttribute("notStartedProjects", notStartedProjects);
        model.addAttribute("githubProjects", githubProjects);
        model.addAttribute("allTags", allTags);
        model.addAttribute("completionRate", completionRate);
        model.addAttribute("inProgressRate", inProgressRate);
        model.addAttribute("notStartedRate", notStartedRate);
        model.addAttribute("githubRate", githubRate);

        // Add filter values back to model
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedOnGithub", onGithub);
        model.addAttribute("selectedTags", tags != null ? tags : new ArrayList<>());
        model.addAttribute("createdMonth", createdMonth);
        model.addAttribute("lastWorkedMonth", lastWorkedMonth);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("hasActiveFilters", hasActiveFilters);

        return "dashboard";
    }

    /**
     * Show form for creating new project
     */
    @GetMapping("/projects/new")
    public String showNewProjectForm(Model model) {
        log.debug("Showing new project form");

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setStatus(ProjectStatus.NOT_STARTED);
        projectDTO.setOnGithub(false);

        List<TagDTO> allTags = tagService.getAllTagsOrderedByName().stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("project", projectDTO);
        model.addAttribute("allTags", allTags);
        model.addAttribute("statuses", ProjectStatus.values());

        return "project-form";
    }

    /**
     * Create new project
     */
    @PostMapping("/projects/add")
    public String addProject(
            @Valid @ModelAttribute("project") ProjectDTO projectDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Creating new project: {}", projectDTO.getTitle());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors creating project: {}", bindingResult.getAllErrors());

            // Re-populate form data
            List<TagDTO> allTags = tagService.getAllTagsOrderedByName().stream()
                    .map(tagMapper::toDTO)
                    .collect(Collectors.toList());

            model.addAttribute("allTags", allTags);
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("error", "Please correct the errors below");

            return "project-form";
        }

        try {
            // Convert DTO to entity
            Project project = projectMapper.toEntity(projectDTO);

            // Save project
            Project savedProject = projectService.saveProject(project);

            // Update tags if provided
            if (tagIds != null && !tagIds.isEmpty()) {
                projectService.updateProjectTags(savedProject.getId(), tagIds);
            }

            log.info("Project created successfully with ID: {}", savedProject.getId());
            redirectAttributes.addFlashAttribute("success", "Project '" + savedProject.getTitle() + "' created successfully!");
            return "redirect:/projects";

        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error creating project: " + e.getMessage());
            return "redirect:/projects/new";
        }
    }

    /**
     * Show form for editing existing project
     */
    @GetMapping("/projects/{id}/edit")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
        log.debug("Showing edit form for project ID: {}", id);

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        ProjectDTO projectDTO = projectMapper.toDTO(project);

        List<TagDTO> allTags = tagService.getAllTagsOrderedByName().stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("project", projectDTO);
        model.addAttribute("allTags", allTags);
        model.addAttribute("statuses", ProjectStatus.values());

        return "project-form";
    }

    /**
     * Update existing project
     */
    @PostMapping("/projects/{id}/update")
    public String updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute("project") ProjectDTO projectDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("Updating project ID: {}", id);

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors updating project: {}", bindingResult.getAllErrors());

            // Re-populate form data
            projectDTO.setId(id); // Ensure ID is set
            List<TagDTO> allTags = tagService.getAllTagsOrderedByName().stream()
                    .map(tagMapper::toDTO)
                    .collect(Collectors.toList());

            model.addAttribute("allTags", allTags);
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("error", "Please correct the errors below");

            return "project-form";
        }

        try {
            // Convert DTO to entity and update
            Project updatedProject = projectMapper.toEntity(projectDTO);
            projectService.updateProject(id, updatedProject);

            // Update tags
            if (tagIds != null) {
                projectService.updateProjectTags(id, tagIds);
            } else {
                // Clear all tags if none selected
                projectService.updateProjectTags(id, List.of());
            }

            log.info("Project updated successfully: {}", id);
            redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
            return "redirect:/projects";

        } catch (ResourceNotFoundException e) {
            log.error("Project not found: {}", id);
            redirectAttributes.addFlashAttribute("error", "Project not found");
            return "redirect:/projects";
        } catch (Exception e) {
            log.error("Error updating project: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating project: " + e.getMessage());
            return "redirect:/projects/" + id + "/edit";
        }
    }

    /**
     * Delete project
     */
    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting project ID: {}", id);

        try {
            Project project = projectService.getProjectById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

            String projectTitle = project.getTitle();
            projectService.deleteProject(id);

            log.info("Project deleted successfully: {}", id);
            redirectAttributes.addFlashAttribute("success", "Project '" + projectTitle + "' deleted successfully!");
        } catch (ResourceNotFoundException e) {
            log.error("Project not found: {}", id);
            redirectAttributes.addFlashAttribute("error", "Project not found");
        } catch (Exception e) {
            log.error("Error deleting project: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error deleting project: " + e.getMessage());
        }

        return "redirect:/projects";
    }

    /**
     * View project details
     */
    @GetMapping("/projects/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        log.debug("Viewing project ID: {}", id);

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        ProjectDTO projectDTO = projectMapper.toDTO(project);

        model.addAttribute("project", projectDTO);
        return "project-detail";
    }
}
