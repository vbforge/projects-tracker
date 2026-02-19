package com.vbforge.projectstracker.service;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.User;

import java.util.List;

public interface ExportService {

    byte[] exportToCSV(List<Project> projects, User owner);
    byte[] exportToHTML(List<Project> projects, String filterDescription, User owner);

}
