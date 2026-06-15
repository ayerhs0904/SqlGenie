package com.sqlgenie.model;

import java.util.List;
import java.util.Map;

public class QueryResult {
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private String executedSql;

    public QueryResult(List<String> columns, List<Map<String, Object>> rows, String executedSql) {
        this.columns = columns;
        this.rows = rows;
        this.executedSql = executedSql;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public String getExecutedSql() {
        return executedSql;
    }

    public void setExecutedSql(String executedSql) {
        this.executedSql = executedSql;
    }
}
