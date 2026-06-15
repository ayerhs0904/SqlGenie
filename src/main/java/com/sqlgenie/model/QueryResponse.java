package com.sqlgenie.model;

import java.util.List;
import java.util.Map;

public class QueryResponse {
    private String generatedSql;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private int rowCount;
    private String message;

    public QueryResponse(String generatedSql, List<String> columns, List<Map<String, Object>> rows, int rowCount, String message) {
        this.generatedSql = generatedSql;
        this.columns = columns;
        this.rows = rows;
        this.rowCount = rowCount;
        this.message = message;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getMessage() {
        return message;
    }
}
