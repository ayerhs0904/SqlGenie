package com.sqlgenie.service;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService {

    private final SchemaService schemaService;

    public PromptBuilderService(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public String buildSystemPrompt() {
        String schemaContext = schemaService.getSchemaContext();
        
        return "You are an expert SQL generator. Convert natural language requests into SQL queries.\n" +
               "Follow these strict rules:\n" +
               "1. Only SELECT statements are allowed.\n" +
               "2. Never use SELECT *.\n" +
               "3. Always add LIMIT 500 if not specified.\n" +
               "4. Always alias aggregated columns.\n" +
               "5. Only use tables and columns from the provided schema.\n" +
               "6. Output raw SQL only with no explanation, no markdown, and no backticks.\n\n" +
               "Database Schema:\n" + schemaContext;
    }
}
