package com.sqlgenie.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SchemaService {

    private final DataSource dataSource;
    private String schemaContext = "";

    @Autowired
    public SchemaService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        StringBuilder sb = new StringBuilder();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            try (ResultSet tables = metaData.getTables(null, "public", "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    sb.append("Table: ").append(tableName).append("\n");
                    
                    try (ResultSet columns = metaData.getColumns(null, "public", tableName, "%")) {
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String typeName = columns.getString("TYPE_NAME");
                            sb.append("  - ").append(columnName).append(" (").append(typeName).append(")\n");
                        }
                    }
                }
            }
            this.schemaContext = sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSchemaContext() {
        return schemaContext;
    }
}
