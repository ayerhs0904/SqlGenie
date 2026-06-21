package com.sqlgenie.service;

import com.sqlgenie.exception.QueryExecutionException;
import com.sqlgenie.model.QueryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryExecutorServiceTest {

    @Mock
    private DataSource readOnlyDataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    private QueryExecutorService queryExecutorService;

    @BeforeEach
    void setUp() {
        queryExecutorService = new QueryExecutorService(readOnlyDataSource);
    }

    @Test
    void testLimitIsAppendedAndTimeoutSet() throws SQLException {
        when(readOnlyDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(0);
        when(resultSet.next()).thenReturn(false);

        queryExecutorService.execute("SELECT * FROM users");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).prepareStatement(sqlCaptor.capture());
        
        String executedSql = sqlCaptor.getValue();
        assertTrue(executedSql.contains("LIMIT 500"));

        verify(preparedStatement).setQueryTimeout(5);
    }

    @Test
    void testQueryExecutionExceptionThrownOnFailure() throws SQLException {
        when(readOnlyDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Mock DB error"));

        assertThrows(QueryExecutionException.class, () -> {
            queryExecutorService.execute("SELECT * FROM users LIMIT 10");
        });
    }
}
