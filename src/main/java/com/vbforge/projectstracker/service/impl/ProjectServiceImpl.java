package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.exception.ResourceNotFoundException;
import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;

    @Override
    public List<Project> getAllProjects() {
        log.debug("Getting all projects");
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> getProjectById(Long id) {
        log.debug("Getting project by id: {}", id);
        return projectRepository.findById(id);
    }

    @Override
    public Project saveProject(Project project) {
        log.info("Saving new project: {}", project.getTitle());
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long id, Project updatedProject) {
        log.info("Updating project with id: {}", id);
        return projectRepository.findById(id)
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
                .orElseThrow(()->{
                    log.error("Project not found with id: {}", id);
                    return new ResourceNotFoundException("Project", "id", id);
                });
    }

    @Override
    public void deleteProject(Long id) {
        log.info("Deleting project with id: {}", id);
        if(!projectRepository.existsById(id)) {
            log.error("Project not found with id: {}", id);
            throw new ResourceNotFoundException("Project", "id", id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public List<Project> getProjectsByStatus(ProjectStatus status) {
        log.debug("Getting projects by status: {}", status);
        return projectRepository.findByStatus(status);
    }

    @Override
    public List<Project> getProjectsByGithubStatus(boolean onGithub) {
        log.debug("Getting projects by github status: {}", onGithub);
        return projectRepository.findByOnGithub(onGithub);
    }

    @Override
    public List<Project> searchProjectsByTitle(String title) {
        log.debug("Searching projects with title containing: {}", title);
        return projectRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Project> getProjectsByTag(String tagName) {
        log.debug("Getting projects by tag: {}", tagName);
        return projectRepository.findByTagName(tagName);
    }

    @Override
    public List<Project> getProjectsByTags(List<String> tagNames) {
        log.debug("Getting projects by tags: {}", tagNames);
        if(tagNames == null || tagNames.isEmpty()) {
            return getAllProjects();
        }
        return projectRepository.findByTagNames(tagNames);
    }

    @Override
    public List<Project> getProjectsCreatedInMonth(YearMonth yearMonth) {
        log.debug("Getting projects created in month: {}", yearMonth);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return projectRepository.findByCreatedDateBetween(start, end);
    }

    @Override
    public List<Project> getProjectsLastWorkedInMonth(YearMonth yearMonth) {
        log.debug("Getting projects last worked in month: {}", yearMonth);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return projectRepository.findByLastWorkedOnBetween(start, end);
    }

    @Override
    public List<Project> searchProjects(String title, ProjectStatus status, Boolean onGithub, String tagName) {
        log.debug("Searching projects with filters - title: {}, status: {}, onGithub: {}, tag: {}",
                title, status, onGithub, tagName);
        return projectRepository.searchProjects(title, status, onGithub, tagName);
    }

    @Override
    public List<Project> getRecentProjects() {
        log.debug("Getting recent projects");
        return projectRepository.findAllByOrderByLastWorkedOnDesc(); //return projects by last worked on
    }

    @Override
    public long getTotalProjectCount() {
        return projectRepository.count();
    }

    @Override
    public long getCompletedProjectCount() {
        return projectRepository.countByStatus(ProjectStatus.DONE);
    }

    @Override
    public long getInProgressProjectCount() {
        return projectRepository.countByStatus(ProjectStatus.IN_PROGRESS);
    }

    @Override
    public long getNotStartedProjectCount() {
        return projectRepository.countByStatus(ProjectStatus.NOT_STARTED);
    }

    @Override
    public long getGithubProjectCount() {
        return projectRepository.countByOnGithub(true);
    }

    @Override
    public Project addTagToProject(Long projectId, Long tagId) {
        log.info("Adding tag id: {} to project with id: {}", tagId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new  ResourceNotFoundException("Project", "id", projectId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()-> new ResourceNotFoundException("Tag", "id", tagId));

        project.addTag(tag);

        return projectRepository.save(project);
    }

    @Override
    public Project removeTagFromProject(Long projectId, Long tagId) {
        log.info("Removing tag id: {} from project with id: {}", tagId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new  ResourceNotFoundException("Project", "id", projectId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(()-> new ResourceNotFoundException("Tag", "id", tagId));

        project.removeTag(tag);

        return projectRepository.save(project);
    }

    @Override
    public Project updateProjectTags(Long projectId, List<Long> tagIds) {
        log.info("Updating tags for project with id: {}", projectId);

        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ResourceNotFoundException("Project", "id", projectId));

        //clear all tags
        project.getTags().clear();

        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId).orElseThrow(()-> new  ResourceNotFoundException("Tag", "id", tagId));
            project.addTag(tag);
        }

        return projectRepository.save(project);
    }
}











