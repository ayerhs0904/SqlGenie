package com.sqlgenie.service;

import com.sqlgenie.exception.SqlGenerationException;
import com.sqlgenie.model.ValidationResult;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LlmService {

    private final PromptBuilderService promptBuilderService;
    private final SqlValidatorService sqlValidatorService;
    private final ChatLanguageModel chatLanguageModel;

    public LlmService(PromptBuilderService promptBuilderService, SqlValidatorService sqlValidatorService) {
        this.promptBuilderService = promptBuilderService;
        this.sqlValidatorService = sqlValidatorService;
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = "dummy";
        }
        this.chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(apiKey)
                .modelName("llama-3.3-70b-versatile")
                .temperature(0.0)
                .build();
    }

    public String generateSql(String naturalLanguageQuery) {
        String systemPrompt = promptBuilderService.buildSystemPrompt();
        
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from(systemPrompt));
        messages.add(UserMessage.from(naturalLanguageQuery));
        
        String lastAttemptedSql = null;
        int maxAttempts = 3;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String responseText = chatLanguageModel.generate(messages).content().text();
            lastAttemptedSql = cleanSql(responseText);
            
            ValidationResult validationResult = sqlValidatorService.validate(lastAttemptedSql);
            if (validationResult.isValid()) {
                return lastAttemptedSql;
            }
            
            if (attempt < maxAttempts) {
                messages.add(AiMessage.from(responseText));
                String errorMessage = "Your previous SQL was invalid: " + lastAttemptedSql + ". Error: " + validationResult.getError() + ". Please fix and return only raw SQL.";
                messages.add(UserMessage.from(errorMessage));
            }
        }
        
        throw new SqlGenerationException("Failed to generate valid SQL after " + maxAttempts + " attempts.", lastAttemptedSql);
    }

    private String cleanSql(String sql) {
        if (sql == null) {
            return null;
        }
        String clean = sql.trim();
        if (clean.startsWith("```sql")) {
            clean = clean.substring(6);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        return clean.trim();
    }
}
