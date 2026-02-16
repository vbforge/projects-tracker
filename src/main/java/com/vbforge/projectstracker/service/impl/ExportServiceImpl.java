package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public byte[] exportToCSV(List<Project> projects) {
        log.debug("Exporting projects to CSV");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            // Write CSV header
            writer.println("ID,Title,Description,Status,On GitHub,GitHub URL,Local Path,Tags,What To Do,Created Date,Last Worked On");

            // Write project data
            for (Project project : projects) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        project.getId(),
                        escapeCsv(project.getTitle()),
                        escapeCsv(project.getDescription()),
                        project.getStatus().name(),
                        project.getOnGithub(),
                        escapeCsv(project.getGithubUrl()),
                        escapeCsv(project.getLocalPath()),
                        escapeCsv(getTagNames(project)),
                        escapeCsv(project.getWhatTodo()),
                        project.getCreatedDate().format(DATE_FORMATTER),
                        project.getLastWorkedOn().format(DATE_FORMATTER)
                );
            }

            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error exporting to CSV", e);
        }
    }

    @Override
    public byte[] exportToHTML(List<Project> projects, String filterDescription) {
        log.debug("Exporting projects to HTML");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            // Calculate statistics
            long totalProjects = projects.size();
            long completedProjects = projects.stream().filter(p -> p.getStatus().name().equals("DONE")).count();
            long inProgressProjects = projects.stream().filter(p -> p.getStatus().name().equals("IN_PROGRESS")).count();
            long onGithubProjects = projects.stream().filter(Project::getOnGithub).count();

            // Write HTML
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='en'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            writer.println("    <title>Project Tracker Export Report</title>");
            writer.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            writer.println("    <style>");
            writer.println("        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; }");
            writer.println("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }");
            writer.println("        .stats { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px; }");
            writer.println("        .stat-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; }");
            writer.println("        .stat-number { font-size: 2rem; font-weight: bold; color: #667eea; }");
            writer.println("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            writer.println("        th { background-color: #667eea; color: white; padding: 12px; text-align: left; }");
            writer.println("        td { padding: 10px; border-bottom: 1px solid #ddd; }");
            writer.println("        tr:hover { background-color: #f5f5f5; }");
            writer.println("        .tag-badge { background-color: #e7f3ff; color: #0066cc; padding: 4px 10px; border-radius: 15px; font-size: 0.85rem; margin-right: 5px; }");
            writer.println("        .status-done { background-color: #d4edda; color: #155724; padding: 4px 10px; border-radius: 15px; }");
            writer.println("        .status-in-progress { background-color: #fff3cd; color: #856404; padding: 4px 10px; border-radius: 15px; }");
            writer.println("        .status-not-started { background-color: #f8d7da; color: #721c24; padding: 4px 10px; border-radius: 15px; }");
            writer.println("        .footer { margin-top: 30px; text-align: center; color: #6c757d; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");

            // Header
            writer.println("    <div class='header'>");
            writer.println("        <h1>📊 Project Tracker - Export Report</h1>");
            if (filterDescription != null && !filterDescription.isEmpty()) {
                writer.println("        <p>" + filterDescription + "</p>");
            }
            writer.println("        <p>Generated: " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "</p>");
            writer.println("    </div>");

            // Statistics
            writer.println("    <div class='stats'>");
            writer.println("        <div class='stat-card'>");
            writer.println("            <div class='stat-number'>" + totalProjects + "</div>");
            writer.println("            <div>Total Projects</div>");
            writer.println("        </div>");
            writer.println("        <div class='stat-card'>");
            writer.println("            <div class='stat-number'>" + completedProjects + "</div>");
            writer.println("            <div>Completed</div>");
            writer.println("        </div>");
            writer.println("        <div class='stat-card'>");
            writer.println("            <div class='stat-number'>" + inProgressProjects + "</div>");
            writer.println("            <div>In Progress</div>");
            writer.println("        </div>");
            writer.println("        <div class='stat-card'>");
            writer.println("            <div class='stat-number'>" + onGithubProjects + "</div>");
            writer.println("            <div>On GitHub</div>");
            writer.println("        </div>");
            writer.println("    </div>");

            // Projects Table
            writer.println("    <table>");
            writer.println("        <thead>");
            writer.println("            <tr>");
            writer.println("                <th>Title</th>");
            writer.println("                <th>Description</th>");
            writer.println("                <th>Status</th>");
            writer.println("                <th>Tags</th>");
            writer.println("                <th>GitHub</th>");
            writer.println("                <th>Created</th>");
            writer.println("                <th>Last Worked</th>");
            writer.println("            </tr>");
            writer.println("        </thead>");
            writer.println("        <tbody>");

            for (Project project : projects) {
                writer.println("            <tr>");
                writer.println("                <td><strong>" + escapeHtml(project.getTitle()) + "</strong></td>");
                writer.println("                <td>" + escapeHtml(truncate(project.getDescription(), 100)) + "</td>");

                // Status with color
                String statusClass = switch (project.getStatus().name()) {
                    case "DONE" -> "status-done";
                    case "IN_PROGRESS" -> "status-in-progress";
                    default -> "status-not-started";
                };
                writer.println("                <td><span class='" + statusClass + "'>" + project.getStatus().name().replace("_", " ") + "</span></td>");

                // Tags
                writer.print("                <td>");
                for (Tag tag : project.getTags()) {
                    writer.print("<span class='tag-badge'>" + escapeHtml(tag.getName()) + "</span>");
                }
                writer.println("</td>");

                // GitHub
                writer.println("                <td>" + (project.getOnGithub() ? "✓ Yes" : "✗ No") + "</td>");
                writer.println("                <td>" + project.getCreatedDate().format(DATE_FORMATTER) + "</td>");
                writer.println("                <td>" + project.getLastWorkedOn().format(DATE_FORMATTER) + "</td>");
                writer.println("            </tr>");
            }

            writer.println("        </tbody>");
            writer.println("    </table>");

            // Footer
            writer.println("    <div class='footer'>");
            writer.println("        <p>Project Tracker - Generated on " + java.time.LocalDate.now() + "</p>");
            writer.println("    </div>");

            writer.println("</body>");
            writer.println("</html>");

            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error exporting to HTML", e);
        }
    }


    //---helpers methods---

    private String getTagNames(Project project) {
        return project.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String truncate(String value, int length) {
        if (value == null) return "";
        if (value.length() <= length) return value;
        return value.substring(0, length) + "...";
    }


}
