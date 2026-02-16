package com.vbforge.projectstracker.loadschema;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SchemaLoadTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void contextLoads() {
        assertThat(em).isNotNull();
    }

    @Test
    void testDatabaseTablesCreated() {
        boolean projectTableExists = em.createNativeQuery(
                        "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PROJECTS'")
                .getSingleResult().toString().equals("1");
        assertThat(projectTableExists).isTrue();
    }
}
