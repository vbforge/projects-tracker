package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Project;

import java.util.List;

public interface ExportService {

    byte[] exportToCSV(List<Project> projects);
    byte[] exportToHTML(List<Project> projects, String filterDescription);

}
