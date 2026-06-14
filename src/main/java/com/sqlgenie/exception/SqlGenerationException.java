package com.sqlgenie.exception;

public class SqlGenerationException extends RuntimeException {

    private final String lastAttemptedSql;

    public SqlGenerationException(String message, String lastAttemptedSql) {
        super(message);
        this.lastAttemptedSql = lastAttemptedSql;
    }

    public String getLastAttemptedSql() {
        return lastAttemptedSql;
    }
}
