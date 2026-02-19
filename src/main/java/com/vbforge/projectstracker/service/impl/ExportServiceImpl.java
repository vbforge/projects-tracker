package com.vbforge.projectstracker.service.impl;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
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
    public byte[] exportToCSV(List<Project> projects, User owner) {
        log.debug("Exporting projects to CSV");
        log.info("Generating CSV export for user: {}", owner.getUsername());
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
    public byte[] exportToHTML(List<Project> projects, String filterDescription, User owner) {
        log.debug("Exporting projects to HTML");
        log.info("Generating HTML export for user: {}", owner.getUsername());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            // Calculate statistics
            long totalProjects = projects.size();
            long completedProjects = projects.stream().filter(p -> p.getStatus().name().equals("DONE")).count();
            long inProgressProjects = projects.stream().filter(p -> p.getStatus().name().equals("IN_PROGRESS")).count();
            long onGithubProjects = projects.stream().filter(Project::getOnGithub).count();

            String ownerUsername = owner.getUsername();

            // Write HTML
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='en'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            writer.println("    <title>Projects Tracker Export Report</title>");
            writer.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            writer.println("    <style>");
            writer.println("        :root {");
            writer.println("            --bg: #0f172a;");
            writer.println("            --card: #111827;");
            writer.println("            --muted: #94a3b8;");
            writer.println("            --border: rgba(255,255,255,0.06);");
            writer.println("            --primary: #3b82f6;");
            writer.println("            --success: #22c55e;");
            writer.println("            --warning: #f59e0b;");
            writer.println("            --danger: #ef4444;");
            writer.println("        }");

            writer.println("        * { box-sizing: border-box; }");

            writer.println("        body {");
            writer.println("            margin: 0;");
            writer.println("            padding: 50px;");
            writer.println("            background: var(--bg);");
            writer.println("            color: #e5e7eb;");
            writer.println("            font-family: 'Inter', 'Segoe UI', sans-serif;");
            writer.println("            -webkit-font-smoothing: antialiased;");
            writer.println("        }");

            writer.println("        .container { max-width: 1300px; margin: auto; }");

            writer.println("        .header {");
            writer.println("            display: flex;");
            writer.println("            justify-content: space-between;");
            writer.println("            align-items: center;");
            writer.println("            margin-bottom: 50px;");
            writer.println("        }");

            writer.println("        .header h1 {");
            writer.println("            font-size: 1.8rem;");
            writer.println("            font-weight: 600;");
            writer.println("            margin: 0;");
            writer.println("        }");

            writer.println("        .meta { color: var(--muted); font-size: 0.9rem; }");

            writer.println("        .stats {");
            writer.println("            display: grid;");
            writer.println("            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));");
            writer.println("            gap: 25px;");
            writer.println("            margin-bottom: 50px;");
            writer.println("        }");

            writer.println("        .stat-card {");
            writer.println("            background: var(--card);");
            writer.println("            padding: 28px;");
            writer.println("            border-radius: 18px;");
            writer.println("            border: 1px solid var(--border);");
            writer.println("            transition: 0.2s ease;");
            writer.println("        }");

            writer.println("        .stat-card:hover { transform: translateY(-3px); }");

            writer.println("        .stat-number {");
            writer.println("            font-size: 2.2rem;");
            writer.println("            font-weight: 700;");
            writer.println("            margin-bottom: 8px;");
            writer.println("            color: var(--primary);");
            writer.println("        }");

            writer.println("        .stat-label {");
            writer.println("            color: var(--muted);");
            writer.println("            font-size: 0.9rem;");
            writer.println("        }");

            writer.println("        table {");
            writer.println("            width: 100%;");
            writer.println("            border-collapse: separate;");
            writer.println("            border-spacing: 0;");
            writer.println("            background: var(--card);");
            writer.println("            border-radius: 18px;");
            writer.println("            overflow: hidden;");
            writer.println("            border: 1px solid var(--border);");
            writer.println("        }");

            writer.println("        thead { background: #0b1220; }");

            writer.println("        th {");
            writer.println("            text-align: left;");
            writer.println("            padding: 16px;");
            writer.println("            font-size: 0.75rem;");
            writer.println("            text-transform: uppercase;");
            writer.println("            letter-spacing: 0.08em;");
            writer.println("            color: var(--muted);");
            writer.println("            font-weight: 600;");
            writer.println("        }");

            writer.println("        td {");
            writer.println("            padding: 16px;");
            writer.println("            border-top: 1px solid var(--border);");
            writer.println("            font-size: 0.9rem;");
            writer.println("        }");

            writer.println("        tr:hover { background: rgba(255,255,255,0.03); }");


            writer.println("        .status-done,");
            writer.println("        .status-in-progress,");
            writer.println("        .status-not-started {");
            writer.println("            display: inline-block;");
            writer.println("            padding: 1px 6px;");
            writer.println("            border-radius: 999px;");
            writer.println("            font-size: 0.65rem;");
            writer.println("            font-weight: 500;");
            writer.println("            line-height: 1.2;");
            writer.println("            white-space: nowrap;");
            writer.println("            text-transform: uppercase;");
            writer.println("        }");

            writer.println("        .status-done {");
            writer.println("            background: rgba(34,197,94,0.15);");
            writer.println("            color: var(--success);");
            writer.println("        }");

            writer.println("        .status-in-progress {");
            writer.println("            background: rgba(245,158,11,0.15);");
            writer.println("            color: var(--warning);");
            writer.println("        }");

            writer.println("        .status-not-started {");
            writer.println("            background: rgba(239,68,68,0.15);");
            writer.println("            color: var(--danger);");
            writer.println("        }");

            writer.println("        .footer {");
            writer.println("            margin-top: 60px;");
            writer.println("            text-align: center;");
            writer.println("            color: var(--muted);");
            writer.println("            font-size: 0.85rem;");
            writer.println("        }");

            writer.println("        @media print {");
            writer.println("            body { background: white; color: black; padding: 20px; }");
            writer.println("            .stat-card, table { border: 1px solid #ddd; }");
            writer.println("            thead { background: #f3f4f6; }");
            writer.println("        }");

            writer.println("    </style>");

            writer.println("</head>");
            writer.println("<body>");

            writer.println("<div class='container'>");
            // Header
            writer.println("    <div class='header'>");
            writer.println("        <div>");
            writer.println("            <h1>Projects Tracker Report</h1>");

            writer.println("            <div class='meta'>Owner: <strong>" + ownerUsername + "</strong></div>");

            writer.println("            <div class='meta'>" + (filterDescription != null ? filterDescription : "Full project export") + "</div>");

            writer.println("        </div>");

            writer.println("        <div class='meta'>Generated: " +
                    java.time.LocalDateTime.now().format(DATE_FORMATTER) +
                    "</div>");
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

                int tagIndex = 0;
                for (Tag tag : project.getTags()) {
                    if (tagIndex > 0) {
                        writer.print(", ");
                    }
                    writer.print(escapeHtml(tag.getName()));
                    tagIndex++;
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

            writer.println("</div>");
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
