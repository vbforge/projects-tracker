package com.vbforge.projectstracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic smoke test to verify application context loads successfully
 */
@SpringBootTest
@ActiveProfiles("test")
class ProjectsTrackerApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify application context is loaded
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void verifyRepositoryBeansAreLoaded() {
        // Verify repository beans are loaded
        assertThat(applicationContext.containsBean("projectRepository")).isTrue();
        assertThat(applicationContext.containsBean("tagRepository")).isTrue();
        assertThat(applicationContext.containsBean("userRepository")).isTrue();
    }

    @Test
    void verifyServiceBeansAreLoaded() {
        // Verify service beans are loaded
        assertThat(applicationContext.containsBean("projectServiceImpl")).isTrue();
        assertThat(applicationContext.containsBean("tagServiceImpl")).isTrue();
        assertThat(applicationContext.containsBean("userServiceImpl")).isTrue();
    }

}
