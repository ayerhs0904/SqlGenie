package com.sqlgenie.service;

import com.sqlgenie.exception.QueryExecutionException;
import com.sqlgenie.model.QueryResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class QueryExecutorService {

    private final DataSource readOnlyDataSource;

    public QueryExecutorService(@Qualifier("readOnlyDataSource") DataSource readOnlyDataSource) {
        this.readOnlyDataSource = readOnlyDataSource;
    }

    public QueryResult execute(String sql) {
        String executedSql = enforceLimit(sql);
        
        try (Connection connection = readOnlyDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(executedSql)) {
             
            statement.setQueryTimeout(5);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                List<String> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(metaData.getColumnLabel(i));
                }
                
                List<Map<String, Object>> rows = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(columns.get(i - 1), resultSet.getObject(i));
                    }
                    rows.add(row);
                }
                
                return new QueryResult(columns, rows, executedSql);
            }
        } catch (SQLException e) {
            throw new QueryExecutionException("Error executing query: " + e.getMessage(), e, executedSql);
        }
    }

    private String enforceLimit(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        String upperSql = sql.toUpperCase();
        if (!Pattern.compile("\\bLIMIT\\b").matcher(upperSql).find()) {
            if (sql.trim().endsWith(";")) {
                sql = sql.trim().substring(0, sql.trim().length() - 1);
            }
            return sql + " LIMIT 500";
        }
        return sql;
    }
}
