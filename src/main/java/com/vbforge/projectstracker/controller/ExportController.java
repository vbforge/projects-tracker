package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.service.ExportService;
import com.vbforge.projectstracker.service.ProjectFilterService;
import com.vbforge.projectstracker.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for exporting projects to various formats
 */
@Slf4j
@Controller
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ProjectFilterService filterService;
    private final ExportService exportService;
    private final SecurityUtils securityUtils;

    /**
     * Export filtered projects to CSV format
     */
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCSV(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Boolean onGithub,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String createdMonth,
            @RequestParam(required = false) String lastWorkedMonth,
            @RequestParam(required = false, defaultValue = "lastWorked") String sortBy) {

        User currentUser = securityUtils.getCurrentUser();
        log.info("Exporting projects to CSV for user: {}", currentUser.getUsername());

        // Get filtered and sorted projects using FilterService
        List<Project> projects = filterService.getFilteredAndSortedProjects(
                search, status, onGithub, tags, createdMonth, lastWorkedMonth, sortBy, currentUser
        );

        // Export to CSV
        byte[] csvData = exportService.exportToCSV(projects, currentUser);

        // Generate filename with timestamp
        String filename = "projects_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + currentUser.getUsername() + "_.csv";

        log.info("Exported {} projects to CSV: {} for user {}", projects.size(), filename, currentUser.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    /**
     * Export filtered projects to HTML report format
     */
    @GetMapping("/html")
    public ResponseEntity<byte[]> exportHTML(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Boolean onGithub,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String createdMonth,
            @RequestParam(required = false) String lastWorkedMonth,
            @RequestParam(required = false, defaultValue = "lastWorked") String sortBy) {

        User currentUser = securityUtils.getCurrentUser();
        log.info("Exporting projects to HTML for user: {}", currentUser.getUsername());

        // Get filtered and sorted projects using FilterService
        List<Project> projects = filterService.getFilteredAndSortedProjects(
                search, status, onGithub, tags, createdMonth, lastWorkedMonth, sortBy, currentUser
        );

        // Build filter description for the report
        String filterDescription = filterService.buildFilterDescription(
                search, status, onGithub, tags, createdMonth, lastWorkedMonth, sortBy
        );

        // Export to HTML
        byte[] htmlData = exportService.exportToHTML(projects, filterDescription,  currentUser);

        // Generate filename with timestamp
        String filename = "projects_report_[" + currentUser.getUsername() + "]_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";

        log.info("Exported {} projects to HTML: {}", projects.size(), filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_HTML)
                .body(htmlData);
    }
}
