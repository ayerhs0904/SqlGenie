package com.sqlgenie.controller;

import com.sqlgenie.model.QueryRequest;
import com.sqlgenie.model.QueryResponse;
import com.sqlgenie.model.QueryResult;
import com.sqlgenie.service.LlmService;
import com.sqlgenie.service.QueryExecutorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query")
@CrossOrigin
public class QueryController {

    private final LlmService llmService;
    private final QueryExecutorService queryExecutorService;

    public QueryController(LlmService llmService, QueryExecutorService queryExecutorService) {
        this.llmService = llmService;
        this.queryExecutorService = queryExecutorService;
    }

    @PostMapping
    public QueryResponse executeQuery(@RequestBody @Valid QueryRequest request) {
        String sql = request.getSqlOverride();
        if (sql == null || sql.trim().isEmpty()) {
            sql = llmService.generateSql(request.getNaturalLanguage());
        }
        
        QueryResult result = queryExecutorService.execute(sql);
        
        int rowCount = result.getRows() != null ? result.getRows().size() : 0;
        
        return new QueryResponse(
                result.getExecutedSql(),
                result.getColumns(),
                result.getRows(),
                rowCount,
                "Query executed successfully."
        );
    }
}
