package com.vbforge.projectstracker.loadschema;

import com.vbforge.projectstracker.config.TestSecurityConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify database schema is created correctly
 */
@SpringBootTest
@ActiveProfiles("test")
//@Import(TestSecurityConfig.class)
@Transactional
class SchemaLoadTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void contextLoads() {
        assertThat(em).isNotNull();
    }

    @Test
    void testProjectsTableExists() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE UPPER(TABLE_NAME) = 'PROJECTS'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testUsersTableExists() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE UPPER(TABLE_NAME) = 'USERS' " +
                        "AND TABLE_SCHEMA = 'PUBLIC'"
        ).getSingleResult();

//        List<Object[]> resultLog = em.createNativeQuery(
//                "SELECT TABLE_SCHEMA, TABLE_NAME " +
//                        "FROM INFORMATION_SCHEMA.TABLES " +
//                        "WHERE UPPER(TABLE_NAME) = 'USERS'"
//        ).getResultList();
//
//        System.out.println("========================");
//        resultLog.forEach(row ->
//                System.out.println(row[0] + " -> " + row[1])
//        );
//        System.out.println("========================");

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testTagsTableExists() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE UPPER(TABLE_NAME) = 'TAGS'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testProjectTagsJunctionTableExists() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE UPPER(TABLE_NAME) = 'PROJECT_TAGS'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testPersistentLoginsTableExists() {
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE UPPER(TABLE_NAME) = 'PERSISTENT_LOGINS'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testUsersTableHasRequiredColumns() {
        // Verify key columns exist
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE UPPER(TABLE_NAME) = 'USERS' " +
                        "AND UPPER(COLUMN_NAME) IN ('ID', 'USERNAME', 'EMAIL', 'PASSWORD', 'ROLE')"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isGreaterThanOrEqualTo(5L);
    }

    @Test
    void testProjectsTableHasUserIdColumn() {
        // Verify user_id foreign key exists (for data isolation)
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE UPPER(TABLE_NAME) = 'PROJECTS' " +
                        "AND UPPER(COLUMN_NAME) = 'USER_ID'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void testTagsTableHasUserIdColumn() {
        // Verify user_id foreign key exists (for per-user tags)
        Object result = em.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE UPPER(TABLE_NAME) = 'TAGS' " +
                        "AND UPPER(COLUMN_NAME) = 'USER_ID'"
        ).getSingleResult();

        long count = ((Number) result).longValue();
        assertThat(count).isEqualTo(1L);
    }
}
