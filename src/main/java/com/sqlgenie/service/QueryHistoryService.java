package com.sqlgenie.service;

import com.sqlgenie.model.QueryHistory;
import com.sqlgenie.repository.QueryHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryHistoryService {

    private final QueryHistoryRepository queryHistoryRepository;

    public QueryHistoryService(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

    public void saveHistory(String naturalLanguage, String generatedSql, int rowCount, String username, boolean successful) {
        QueryHistory history = new QueryHistory(naturalLanguage, generatedSql, rowCount, username, successful);
        queryHistoryRepository.save(history);
    }

    public List<QueryHistory> getHistory(String username) {
        return queryHistoryRepository.findByUsernameOrderByExecutedAtDesc(username);
    }
}
