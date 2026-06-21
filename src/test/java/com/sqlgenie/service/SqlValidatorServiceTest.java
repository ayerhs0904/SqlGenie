package com.sqlgenie.service;

import com.sqlgenie.model.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlValidatorServiceTest {

    private SqlValidatorService sqlValidatorService;

    @BeforeEach
    void setUp() {
        sqlValidatorService = new SqlValidatorService();
    }

    @Test
    void testValidSelectPasses() {
        ValidationResult result = sqlValidatorService.validate("SELECT * FROM users;");
        assertTrue(result.isValid());
    }

    @Test
    void testSelectWithDropIsRejected() {
        ValidationResult result = sqlValidatorService.validate("SELECT * FROM users WHERE name = 'DROP';");
        assertFalse(result.isValid());
    }

    @Test
    void testInsertIsRejected() {
        ValidationResult result = sqlValidatorService.validate("INSERT INTO users (name) VALUES ('John');");
        assertFalse(result.isValid());
    }

    @Test
    void testEmptySqlIsRejected() {
        ValidationResult result = sqlValidatorService.validate("   ");
        assertFalse(result.isValid());
    }

    @Test
    void testSubqueryWithDeleteIsRejected() {
        ValidationResult result = sqlValidatorService.validate("SELECT * FROM (DELETE FROM users);");
        assertFalse(result.isValid());
    }
}
