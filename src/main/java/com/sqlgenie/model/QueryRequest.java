package com.sqlgenie.model;

import jakarta.validation.constraints.NotBlank;

public class QueryRequest {
    
    @NotBlank(message = "Natural language query cannot be blank")
    private String naturalLanguage;
    
    private String sqlOverride;

    public String getNaturalLanguage() {
        return naturalLanguage;
    }

    public void setNaturalLanguage(String naturalLanguage) {
        this.naturalLanguage = naturalLanguage;
    }

    public String getSqlOverride() {
        return sqlOverride;
    }

    public void setSqlOverride(String sqlOverride) {
        this.sqlOverride = sqlOverride;
    }
}
