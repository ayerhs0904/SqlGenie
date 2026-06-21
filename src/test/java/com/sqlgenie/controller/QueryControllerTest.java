package com.sqlgenie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqlgenie.config.SecurityConfig;
import com.sqlgenie.model.QueryRequest;
import com.sqlgenie.model.QueryResult;
import com.sqlgenie.security.JwtFilter;
import com.sqlgenie.security.JwtUtil;
import com.sqlgenie.service.LlmService;
import com.sqlgenie.service.QueryExecutorService;
import com.sqlgenie.service.QueryHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueryController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class QueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LlmService llmService;

    @MockBean
    private QueryExecutorService queryExecutorService;

    @MockBean
    private QueryHistoryService queryHistoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testUnauthenticatedReturns401() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setNaturalLanguage("Show me all users");

        mockMvc.perform(post("/api/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidRequestWithJwtReturns200() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setNaturalLanguage("Show me all users");

        String token = "mock-token";
        when(jwtUtil.extractUsername(token)).thenReturn("testUser");
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(llmService.generateSql(anyString())).thenReturn("SELECT * FROM users");
        when(queryExecutorService.execute(anyString())).thenReturn(
                new QueryResult(Collections.emptyList(), Collections.emptyList(), "SELECT * FROM users")
        );

        mockMvc.perform(post("/api/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testBlankNaturalLanguageReturns400() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setNaturalLanguage(""); // Blank

        String token = "mock-token";
        when(jwtUtil.extractUsername(token)).thenReturn("testUser");
        when(jwtUtil.validateToken(token)).thenReturn(true);

        mockMvc.perform(post("/api/query")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
