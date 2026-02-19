package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.User;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ProjectService {

    List<Project> getAllProjects(User owner);

    Optional<Project> getProjectByIdAndOwner(Long id, User owner);

    Project saveProject(Project project);

    Project updateProject(Long id, Project updatedProject, User owner);

    void deleteProject(Long id, User owner);

    List<Project> getProjectsByStatus(ProjectStatus status, User owner);

    List<Project> getProjectsByGithubStatus(boolean onGithub, User owner);

    List<Project> searchProjectsByTitle(String title, User owner);

    List<Project> getProjectsByTag(String tagName, User owner);

    List<Project> getProjectsByTags(List<String> tagNames, User owner);

    List<Project> getProjectsCreatedInMonth(YearMonth yearMonth, User owner);

    List<Project> getProjectsLastWorkedInMonth(YearMonth yearMonth, User owner);

    List<Project> searchProjects(String title, ProjectStatus status, Boolean onGithub, String tagName, User owner);

    List<Project> getRecentProjects(User owner);

    long getTotalProjectCount(User owner);

    long getCompletedProjectCount(User owner);

    long getInProgressProjectCount(User owner);

    long getNotStartedProjectCount(User owner);

    long getGithubProjectCount(User owner);

    Project addTagToProject(Long projectId, Long tagId, User owner);

    Project removeTagFromProject(Long projectId, Long tagId, User owner);

    Project updateProjectTags(Long projectId, List<Long> tagIds, User owner);

}