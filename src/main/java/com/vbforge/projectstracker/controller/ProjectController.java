package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.mapper.ProjectMapper;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.service.ProjectService;
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

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final TagService tagService;
    private final SecurityUtils securityUtils;

    // Valid page sizes
    private static final List<Integer> PAGE_SIZES = List.of(10, 25, 50, 100);
    private static final int DEFAULT_PAGE_SIZE = 10;

    @GetMapping({"/", "/projects"})
    public String dashboard(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Boolean onGithub,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String createdMonth,
            @RequestParam(required = false) String lastWorkedMonth,
            @RequestParam(required = false, defaultValue = "lastWorked") String sortBy,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        User currentUser = securityUtils.getCurrentUser();


        if (!PAGE_SIZES.contains(size)) size = DEFAULT_PAGE_SIZE;
        if (page < 0) page = 0;
        if (tags != null && !tags.isEmpty()) {
            tags = tags.stream().distinct().collect(Collectors.toList());
        }

        // Filter + sort (scoped to current user)
        List<Project> allFiltered = getFilteredProjects(
                currentUser, search, status, onGithub, tags, createdMonth, lastWorkedMonth);
        allFiltered = applySorting(allFiltered, sortBy);

        // Pagination math
        int totalElements = allFiltered.size();
        int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / size);
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;
        int fromIndex = page * size;
        int toIndex   = Math.min(fromIndex + size, totalElements);
        List<Project> pagedProjects = allFiltered.subList(fromIndex, toIndex);

        // Stats (scoped to current user)
        long totalProjects      = projectService.getTotalProjectCount(currentUser);
        long completedProjects  = projectService.getCompletedProjectCount(currentUser);
        long inProgressProjects = projectService.getInProgressProjectCount(currentUser);
        long notStartedProjects = projectService.getNotStartedProjectCount(currentUser);
        long githubProjects     = projectService.getGithubProjectCount(currentUser);
        int completionRate = totalProjects == 0 ? 0 : (int) ((completedProjects  * 100.0) / totalProjects);
        int githubRate     = totalProjects == 0 ? 0 : (int) ((githubProjects     * 100.0) / totalProjects);
        int inProgressRate = totalProjects == 0 ? 0 : (int) ((inProgressProjects * 100.0) / totalProjects);
        int notStartedRate = totalProjects == 0 ? 0 : (int) ((notStartedProjects * 100.0) / totalProjects);

        // Project list
        model.addAttribute("projects",           pagedProjects);

        // Stats cards
        model.addAttribute("totalProjects",      totalProjects);
        model.addAttribute("completedProjects",  completedProjects);
        model.addAttribute("inProgressProjects", inProgressProjects);
        model.addAttribute("notStartedProjects", notStartedProjects);
        model.addAttribute("githubProjects",     githubProjects);
        model.addAttribute("completionRate",     completionRate);
        model.addAttribute("githubRate",         githubRate);
        model.addAttribute("inProgressRate",     inProgressRate);
        model.addAttribute("notStartedRate",     notStartedRate);

        // Tags (scoped to current user)
        model.addAttribute("allTags", tagService.getAllTagsOrderedByPopularity(currentUser));

        // Filter state
        model.addAttribute("searchTerm",       search);
        model.addAttribute("selectedStatus",   status);
        model.addAttribute("selectedOnGithub", onGithub);
        model.addAttribute("selectedTags",     tags != null ? tags : new ArrayList<>());
        model.addAttribute("selectedTag",      tags != null && !tags.isEmpty() ? tags.get(0) : null);
        model.addAttribute("createdMonth",     createdMonth);
        model.addAttribute("lastWorkedMonth",  lastWorkedMonth);
        model.addAttribute("sortBy",           sortBy);
        model.addAttribute("hasActiveFilters",
                hasFilters(search, status, onGithub, tags, createdMonth, lastWorkedMonth));

        // Pagination state
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    totalPages);
        model.addAttribute("pageSize",      size);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("displayFrom",   totalElements == 0 ? 0 : fromIndex + 1);
        model.addAttribute("displayTo",     toIndex);
        model.addAttribute("pageSizes",     PAGE_SIZES);
        model.addAttribute("hasPrevious",   page > 0);
        model.addAttribute("hasNext",       page < totalPages - 1);

        return "dashboard";
    }

    @GetMapping("/projects/new")
    public String showNewProjectForm(Model model) {
        User currentUser = securityUtils.getCurrentUser();
        model.addAttribute("project", new Project());
        model.addAttribute("allTags", tagService.getAllTagsOrderedByName(currentUser));
        model.addAttribute("statuses", ProjectStatus.values());
        return "project-form";
    }

    @PostMapping("/projects/add")
    public String addProject(
            @Valid @ModelAttribute Project project,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
            RedirectAttributes redirectAttributes,
            Model model) {
        User currentUser = securityUtils.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("allTags", tagService.getAllTagsOrderedByName(currentUser));
            model.addAttribute("statuses", ProjectStatus.values());
            return "project-form";
        }

        try {
            project.setOwner(currentUser);
            Project saved = projectService.saveProject(project);
            if (tagIds != null && !tagIds.isEmpty()) {
                projectService.updateProjectTags(saved.getId(), tagIds, currentUser);
            }
            redirectAttributes.addFlashAttribute("success", "Project created successfully!");
            return "redirect:/projects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating project: " + e.getMessage());
            return "redirect:/projects/new";
        }
    }

    @GetMapping("/projects/{id}/edit")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
        User currentUser = securityUtils.getCurrentUser();
        Project project = projectService.getProjectByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        model.addAttribute("project", project);
        model.addAttribute("allTags", tagService.getAllTagsOrderedByName(currentUser));
        model.addAttribute("statuses", ProjectStatus.values());
        return "project-form";
    }

    @PostMapping("/projects/{id}/update")
    public String updateProject(
            @PathVariable Long id,
            @Valid @ModelAttribute Project project,
            BindingResult bindingResult,
            @RequestParam(required = false) List<Long> tagIds,
            RedirectAttributes redirectAttributes,
            Model model) {
        User currentUser = securityUtils.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("allTags", tagService.getAllTagsOrderedByName(currentUser));
            model.addAttribute("statuses", ProjectStatus.values());
            return "project-form";
        }

        try {
            projectService.updateProject(id, project, currentUser);
            projectService.updateProjectTags(id, tagIds != null ? tagIds : List.of(), currentUser);
            redirectAttributes.addFlashAttribute("success", "Project updated successfully!");
            return "redirect:/projects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating project: " + e.getMessage());
            return "redirect:/projects/{id}/edit";
        }
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = securityUtils.getCurrentUser();
        try {
            projectService.deleteProject(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "Project deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting project: " + e.getMessage());
        }
        return "redirect:/projects";
    }

    @GetMapping("/projects/{id}")
    public String viewProject(@PathVariable Long id, Model model) {
        User currentUser = securityUtils.getCurrentUser();
        Project project = projectService.getProjectByIdAndOwner(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        model.addAttribute("project", project);
        return "project-detail";
    }

    // === Helpers ===

    private List<Project> getFilteredProjects(User owner, String search, ProjectStatus status,
                                              Boolean onGithub, List<String> tags,
                                              String createdMonth, String lastWorkedMonth) {
        if (createdMonth != null && !createdMonth.isEmpty()) {
            return projectService.getProjectsCreatedInMonth(YearMonth.parse(createdMonth), owner);
        }
        if (lastWorkedMonth != null && !lastWorkedMonth.isEmpty()) {
            return projectService.getProjectsLastWorkedInMonth(YearMonth.parse(lastWorkedMonth), owner);
        }
        if (tags != null && !tags.isEmpty()) {
            return projectService.getProjectsByTags(tags, owner).stream()
                    .filter(p -> search == null || search.isEmpty() ||
                            p.getTitle().toLowerCase().contains(search.toLowerCase()))
                    .filter(p -> status == null || p.getStatus() == status)
                    .filter(p -> onGithub == null || p.getOnGithub().equals(onGithub))
                    .toList();
        }
        if ((search != null && !search.isEmpty()) || status != null || onGithub != null) {
            return projectService.searchProjects(search, status, onGithub, null, owner);
        }
        return projectService.getAllProjects(owner);
    }

    private List<Project> applySorting(List<Project> projects, String sortBy) {
        if (projects == null || projects.isEmpty()) return projects;
        return switch (sortBy) {
            case "created" -> projects.stream()
                    .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())).toList();
            case "title" -> projects.stream()
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle())).toList();
            default -> projects.stream()
                    .sorted((a, b) -> b.getLastWorkedOn().compareTo(a.getLastWorkedOn())).toList();
        };
    }

    private boolean hasFilters(String search, ProjectStatus status, Boolean onGithub,
                               List<String> tags, String createdMonth, String lastWorkedMonth) {
        return (search != null && !search.isEmpty()) || status != null || onGithub != null ||
                (tags != null && !tags.isEmpty()) ||
                (createdMonth != null && !createdMonth.isEmpty()) ||
                (lastWorkedMonth != null && !lastWorkedMonth.isEmpty());
    }
}