package com.sqlgenie.repository;

import com.sqlgenie.model.QueryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, Long> {
    List<QueryHistory> findByUsernameOrderByExecutedAtDesc(String username);
}
