package com.sqlgenie.controller;

import com.sqlgenie.model.QueryHistory;
import com.sqlgenie.model.QueryRequest;
import com.sqlgenie.model.QueryResponse;
import com.sqlgenie.model.QueryResult;
import com.sqlgenie.security.JwtUtil;
import com.sqlgenie.service.LlmService;
import com.sqlgenie.service.QueryExecutorService;
import com.sqlgenie.service.QueryHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/query")
@CrossOrigin
public class QueryController {

    private final LlmService llmService;
    private final QueryExecutorService queryExecutorService;
    private final QueryHistoryService queryHistoryService;
    private final JwtUtil jwtUtil;

    public QueryController(LlmService llmService, QueryExecutorService queryExecutorService, 
                           QueryHistoryService queryHistoryService, JwtUtil jwtUtil) {
        this.llmService = llmService;
        this.queryExecutorService = queryExecutorService;
        this.queryHistoryService = queryHistoryService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public QueryResponse executeQuery(@RequestBody @Valid QueryRequest request, HttpServletRequest httpRequest) {
        String sql = request.getSqlOverride();
        if (sql == null || sql.trim().isEmpty()) {
            sql = llmService.generateSql(request.getNaturalLanguage());
        }
        
        String username = extractUsername(httpRequest);
        QueryResult result = null;
        boolean successful = false;
        int rowCount = 0;
        
        try {
            result = queryExecutorService.execute(sql);
            successful = true;
            rowCount = result.getRows() != null ? result.getRows().size() : 0;
            
            return new QueryResponse(
                    result.getExecutedSql(),
                    result.getColumns(),
                    result.getRows(),
                    rowCount,
                    "Query executed successfully."
            );
        } finally {
            if (username != null) {
                queryHistoryService.saveHistory(request.getNaturalLanguage(), sql, rowCount, username, successful);
            }
        }
    }

    @GetMapping("/history")
    public List<QueryHistory> getHistory(HttpServletRequest httpRequest) {
        String username = extractUsername(httpRequest);
        if (username != null) {
            return queryHistoryService.getHistory(username);
        }
        return List.of();
    }

    private String extractUsername(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUsername(token);
        }
        return null;
    }
}
