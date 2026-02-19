package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    @Override
    public List<Project> getAllProjects(User owner) {
        log.debug("Getting all projects for user: {}", owner.getUsername());
        return projectRepository.findAllByOwner(owner);
    }

    @Override
    public Optional<Project> getProjectByIdAndOwner(Long id, User owner) {
        log.debug("Getting project id={} for user: {}", id, owner.getUsername());
        return projectRepository.findByIdAndOwner(id, owner);
    }

    @Override
    public Project saveProject(Project project) {
        log.info("Saving project: {} for user: {}", project.getTitle(), project.getOwner().getUsername());
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long id, Project updatedProject, User owner) {
        log.info("Updating project id={} for user: {}", id, owner.getUsername());
        return projectRepository.findByIdAndOwner(id, owner)
                .map(project -> {
                    project.setTitle(updatedProject.getTitle());
                    project.setDescription(updatedProject.getDescription());
                    project.setStatus(updatedProject.getStatus());
                    project.setOnGithub(updatedProject.getOnGithub());
                    project.setGithubUrl(updatedProject.getGithubUrl());
                    project.setLocalPath(updatedProject.getLocalPath());
                    project.setWhatTodo(updatedProject.getWhatTodo());
                    project.setLastWorkedOn(LocalDateTime.now());
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    @Override
    public void deleteProject(Long id, User owner) {
        log.info("Deleting project id={} for user: {}", id, owner.getUsername());
        Project project = projectRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        projectRepository.delete(project);
    }

    @Override
    public List<Project> getProjectsByStatus(ProjectStatus status, User owner) {
        return projectRepository.findByStatusAndOwner(status, owner);
    }

    @Override
    public List<Project> getProjectsByGithubStatus(boolean onGithub, User owner) {
        return projectRepository.findByOnGithubAndOwner(onGithub, owner);
    }

    @Override
    public List<Project> searchProjectsByTitle(String title, User owner) {
        return projectRepository.findByTitleContainingIgnoreCaseAndOwner(title, owner);
    }

    @Override
    public List<Project> getProjectsByTag(String tagName, User owner) {
        return projectRepository.findByTagNameAndOwner(tagName, owner);
    }

    @Override
    public List<Project> getProjectsByTags(List<String> tagNames, User owner) {
        return projectRepository.findByTagNamesAndOwner(tagNames, owner);
    }

    @Override
    public List<Project> getProjectsCreatedInMonth(YearMonth yearMonth, User owner) {
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return projectRepository.findByCreatedDateBetweenAndOwner(start, end, owner);
    }

    @Override
    public List<Project> getProjectsLastWorkedInMonth(YearMonth yearMonth, User owner) {
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return projectRepository.findByLastWorkedOnBetweenAndOwner(start, end, owner);
    }

    @Override
    public List<Project> searchProjects(String title, ProjectStatus status, Boolean onGithub, String tagName, User owner) {
        return projectRepository.searchProjects(owner, title, status, onGithub, tagName);
    }

    @Override
    public List<Project> getRecentProjects(User owner) {
        return projectRepository.findAllByOwnerOrderByLastWorkedOnDesc(owner);
    }

    @Override
    public long getTotalProjectCount(User owner) {
        return projectRepository.countByOwner(owner);
    }

    @Override
    public long getCompletedProjectCount(User owner) {
        return projectRepository.countByStatusAndOwner(ProjectStatus.DONE, owner);
    }

    @Override
    public long getInProgressProjectCount(User owner) {
        return projectRepository.countByStatusAndOwner(ProjectStatus.IN_PROGRESS, owner);
    }

    @Override
    public long getNotStartedProjectCount(User owner) {
        return projectRepository.countByStatusAndOwner(ProjectStatus.NOT_STARTED, owner);
    }

    @Override
    public long getGithubProjectCount(User owner) {
        return projectRepository.countByOnGithubAndOwner(true, owner);
    }

    @Override
    public Project addTagToProject(Long projectId, Long tagId, User owner) {
        Project project = projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Tag tag = tagRepository.findByIdAndOwner(tagId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        project.addTag(tag);
        return projectRepository.save(project);
    }

    @Override
    public Project removeTagFromProject(Long projectId, Long tagId, User owner) {
        Project project = projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Tag tag = tagRepository.findByIdAndOwner(tagId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        project.removeTag(tag);
        return projectRepository.save(project);
    }

    @Override
    public Project updateProjectTags(Long projectId, List<Long> tagIds, User owner) {
        Project project = projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // Clear existing tags
        project.getTags().clear();

        // Add new tags (all must belong to owner)
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                Tag tag = tagRepository.findByIdAndOwner(tagId, owner)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
                project.addTag(tag);
            }
        }

        return projectRepository.save(project);
    }
}