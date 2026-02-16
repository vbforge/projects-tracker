package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

    //---CRUD---
    List<Project> getAllProjects();
    Optional<Project> getProjectById(Long id);
    Project saveProject(Project project);
    Project updateProject(Long id, Project updatedProject);
    void deleteProject(Long id);

    //---FILTERING---
    List<Project> getProjectsByStatus(ProjectStatus status);
    List<Project> getProjectsByGithubStatus(boolean onGithub);
    List<Project> searchProjectsByTitle(String title);
    List<Project> getProjectsByTag(String tagName);
    List<Project> getProjectsByTags(List<String> tagNames);
    List<Project> getProjectsCreatedInMonth(YearMonth yearMonth);
    List<Project> getProjectsLastWorkedInMonth(YearMonth yearMonth);
    List<Project> searchProjects(String title, ProjectStatus status, Boolean onGithub, String tagName);
    List<Project> getRecentProjects();

    //---STATISTICS---
    long getTotalProjectCount();
    long getCompletedProjectCount();
    long getInProgressProjectCount();
    long getNotStartedProjectCount();
    long getGithubProjectCount();

    //---TAG MANAGEMENT FOR PROJECTS---
    Project addTagToProject(Long projectId, Long tagId);
    Project removeTagFromProject(Long projectId, Long tagId);
    Project updateProjectTags(Long projectId, List<Long> tagIds);







}










