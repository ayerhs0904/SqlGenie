package com.sqlgenie.service;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final PromptBuilderService promptBuilderService;
    private final ChatLanguageModel chatLanguageModel;

    public LlmService(PromptBuilderService promptBuilderService) {
        this.promptBuilderService = promptBuilderService;
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
        
        SystemMessage sysMessage = SystemMessage.from(systemPrompt);
        UserMessage usrMessage = UserMessage.from(naturalLanguageQuery);
        
        String responseText = chatLanguageModel.generate(sysMessage, usrMessage).content().text();
        
        return cleanSql(responseText);
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
