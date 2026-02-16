package com.vbforge.projectstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectsTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectsTrackerApplication.class, args);
    }

    // Optional: Test bean to verify data
    /*@Bean
    CommandLineRunner test(ProjectService projectService, TagService tagService) {
        return args -> {
            System.out.println("=== DATABASE TEST ===");
            System.out.println("Total projects: " + projectService.getTotalProjectCount());
            System.out.println("Completed projects: " + projectService.getCompletedProjectCount());
            System.out.println("Total tags: " + tagService.getTotalTagCount());
            System.out.println("===================");
        };
    }*/

}
