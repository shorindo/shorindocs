/*
 * Copyright 2018 Shorindo, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shorindo.docs.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.SystemContext;

/**
 * 
 */
public class DatabaseService {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseService.class);
    private static final DatabaseService service = new DatabaseService();
    private DataSource dataSource;

    public static DatabaseService newInstance() {
        return service;
    }

    /**
     * 
     */
    private DatabaseService() {
        try {
            Properties props = new Properties();
            props.setProperty("driverClassName", SystemContext.getProperty("datasource.driverClassName"));
            props.setProperty("url", SystemContext.getProperty("datasource.url"));
            props.setProperty("username", SystemContext.getProperty("datasource.username"));
            props.setProperty("password", SystemContext.getProperty("datasource.password"));
            props.setProperty("validationQuery", SystemContext.getProperty("datasource.validationQuery"));
            props.setProperty("testOnBorrow", SystemContext.getProperty("datasource.testOnBorrow"));
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            LOG.error(DocsMessages.E_1000, e);
        }
    }

    public <T>T provide(DatabaseExecutor<T> callback) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            callback.beginTransaction(conn);
            T result = callback.run(conn);
            callback.commitTransaction(conn);
            return result;
        } catch (Throwable th) {
            LOG.error(DocsMessages.E_1001, th);
            if (conn != null) {
                try {
                    callback.rollbackTransaction(conn);
                } catch (SQLException e) {
                    LOG.error(DocsMessages.E_1005, e);
                }
            }
            throw new RuntimeException(th);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error(DocsMessages.E_1003, e);
                }
            }
        }
    }

    public void loadTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableSet = meta.getTables(null, null, null, null);
            while (tableSet.next()) {
                LOG.info("TABLE[" + tableSet.getString("TABLE_NAME") + "]");
                ResultSet columnSet = meta.getColumns(
                        tableSet.getString("TABLE_CAT"),
                        tableSet.getString("TABLE_SCHEM"),
                        tableSet.getString("TABLE_NAME"),
                        null);
                while (columnSet.next()) {
                    LOG.info("    " +
                            columnSet.getString("COLUMN_NAME") +
                            " " +
                            columnSet.getString("TYPE_NAME"));
                }
            }
        }
    }

    public void createTable(Class<? extends SchemaEntity> entityClass) {
        for (Field field : entityClass.getFields()) {
            
        }
    }
}
