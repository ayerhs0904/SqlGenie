package com.sqlgenie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_history")
public class QueryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naturalLanguage;
    private String generatedSql;
    private int rowCount;
    private String username;

    @CreationTimestamp
    private LocalDateTime executedAt;

    private boolean successful;

    public QueryHistory() {
    }

    public QueryHistory(String naturalLanguage, String generatedSql, int rowCount, String username, boolean successful) {
        this.naturalLanguage = naturalLanguage;
        this.generatedSql = generatedSql;
        this.rowCount = rowCount;
        this.username = username;
        this.successful = successful;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaturalLanguage() {
        return naturalLanguage;
    }

    public void setNaturalLanguage(String naturalLanguage) {
        this.naturalLanguage = naturalLanguage;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public void setGeneratedSql(String generatedSql) {
        this.generatedSql = generatedSql;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
