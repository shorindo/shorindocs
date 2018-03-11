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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.database.DatabaseSchema.Column;
import com.shorindo.docs.database.DatabaseSchema.Entity;

/**
 * 
 */
public class DatabaseService {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseService.class);
    private static final DatabaseService service = new DatabaseService();
    private DataSource dataSource;
    private Map<String,Map<String,DatabaseSchema.Column>> schema;
    private Map<Class<? extends SchemaEntity>,DatabaseSchema> entityMap;

    public static DatabaseService newInstance() {
        return service;
    }

    /**
     * 
     */
    private DatabaseService() {
        try {
            Properties props = new Properties();
            props.setProperty("driverClassName", ApplicationContext.getProperty("datasource.driverClassName"));
            props.setProperty("url", ApplicationContext.getProperty("datasource.url"));
            props.setProperty("username", ApplicationContext.getProperty("datasource.username"));
            props.setProperty("password", ApplicationContext.getProperty("datasource.password"));
            props.setProperty("validationQuery", ApplicationContext.getProperty("datasource.validationQuery"));
            props.setProperty("testOnBorrow", ApplicationContext.getProperty("datasource.testOnBorrow"));
            dataSource = BasicDataSourceFactory.createDataSource(props);
            schema = new HashMap<String,Map<String,DatabaseSchema.Column>>();
        } catch (Exception e) {
            LOG.error(DocsMessages.E_5100, e);
        }
    }

    public void loadSchema(InputStream is) {
        DatabaseSchema newSchema = JAXB.unmarshal(is, DatabaseSchema.class);
        for (Entity entity : newSchema.getEntityList()) {
            LOG.info(DocsMessages.I_1101, newSchema.getNamespace(), entity.getName());
            Map<String,DatabaseSchema.Column> columnMap =
                new LinkedHashMap<String,DatabaseSchema.Column>();
            for (DatabaseSchema.Column column : entity.getColumnList()) {
                columnMap.put(column.getName(), column);
            }
            schema.put(entity.getName(), columnMap);
        }
    }

    public List<String> validateSchema() throws SQLException {
        return provide(new Transactional<List<String>>() {
            @Override
            public List<String> run(Connection conn, Object...params) throws SQLException {
                List<String> resultList = new ArrayList<String>();
                DatabaseMetaData meta = conn.getMetaData();

                for (Entry<String, Map<String, Column>> entry : schema.entrySet()) {
                    // エンティティ定義あり、実体なしのチェック
                    // (逆のエンティティ定義なし、実体ありのチェックはしない)
                    String entityName = entry.getKey();
                    Map<String, Column> entity = entry.getValue();
                    ResultSet trset = meta.getTables(null, null, entityName, null);
                    if (!trset.next()) {
                        String msg = LOG.error(DocsMessages.E_5108, entityName);
                        resultList.add(msg);
                        trset.close();
                        continue;
                    }
                    trset.close();

                    ResultSet crset = meta.getColumns(null, null, entityName, null);
                    Map<String,DatabaseSchema.Column> map
                        = new HashMap<String,DatabaseSchema.Column>();
                    for (DatabaseSchema.Column column : entity.values()) {
                        map.put(column.getName(), column);
                    }

                    // カラム定義なし、実体ありのチェック
                    while (crset.next()) {
                        String columnName = crset.getString("COLUMN_NAME");
                        DatabaseSchema.Column c = map.get(columnName);
                        if (c == null) {
                            String msg = LOG.error(DocsMessages.E_5109, entityName, columnName);
                            resultList.add(msg);
                            continue;
                        }
                        map.remove(columnName);
                        //TODO attrubute
                    }

                    // カラム定義あり、実体なしのチェック
                    for (Map.Entry<String,DatabaseSchema.Column> e : map.entrySet()) {
                        String msg = LOG.error(DocsMessages.E_5110, entityName, e.getKey());
                        resultList.add(msg);
                    }
                    crset.close();
                }
                return resultList;
            }
        });
    }

    public <T>T provide(DatabaseExecutor<T> callback, Object...params) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            callback.setConnection(conn);
            callback.beginTransaction(conn);
            T result = callback.run(conn, params);
            callback.commitTransaction(conn);
            return result;
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    callback.rollbackTransaction(conn);
                } catch (SQLException e) {
                    LOG.error(DocsMessages.E_5105, e);
                }
            }
            throw new SQLException(th);
        } finally {
            callback.removeConnection();
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error(DocsMessages.E_5103, e);
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

    public void createTable() {
    }
}
