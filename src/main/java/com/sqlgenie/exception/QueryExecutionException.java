package com.sqlgenie.exception;

public class QueryExecutionException extends RuntimeException {
    private final String sql;

    public QueryExecutionException(String message, String sql) {
        super(message);
        this.sql = sql;
    }

    public QueryExecutionException(String message, Throwable cause, String sql) {
        super(message, cause);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
