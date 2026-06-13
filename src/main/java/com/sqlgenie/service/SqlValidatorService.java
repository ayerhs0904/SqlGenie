package com.sqlgenie.service;

import com.sqlgenie.model.ValidationResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class SqlValidatorService {

    private static final List<String> BLOCKED_KEYWORDS = List.of(
            "DROP", "INSERT", "UPDATE", "DELETE", "ALTER", 
            "TRUNCATE", "CREATE", "GRANT", "REVOKE", "EXEC"
    );

    private static final Pattern BLOCKED_PATTERN = Pattern.compile(
            "(?i)\\b(" + String.join("|", BLOCKED_KEYWORDS) + ")\\b"
    );

    public ValidationResult validate(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return ValidationResult.invalid("SQL query cannot be empty.");
        }

        if (BLOCKED_PATTERN.matcher(sql).find()) {
            return ValidationResult.invalid("SQL contains blocked keywords.");
        }

        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (!(statement instanceof Select)) {
                return ValidationResult.invalid("Only SELECT statements are allowed.");
            }
        } catch (JSQLParserException e) {
            return ValidationResult.invalid("Invalid SQL syntax: " + e.getMessage());
        }

        return ValidationResult.valid();
    }
}
