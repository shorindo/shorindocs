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
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.DocsMessages;

/**
 * 
 */
public class DatabaseService {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseService.class);
    private static final DatabaseService service = new DatabaseService();
    private DataSource dataSource;

    public static synchronized DatabaseService newInstance() {
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
        } catch (Exception e) {
            LOG.error(DocsMessages.E_5100, e);
        }
    }

    /**
     * 
     * @param is
     */
    public DatabaseSchema loadSchema(InputStream is) {
        DatabaseSchema newSchema = JAXB.unmarshal(is, DatabaseSchema.class);
        for (DatabaseSchema.Entity entity : newSchema.getEntityList()) {
            LOG.info(DocsMessages.I_1101, newSchema.getNamespace(), entity.getName());
            Map<String,DatabaseSchema.Column> columnMap =
                new LinkedHashMap<String,DatabaseSchema.Column>();
            for (DatabaseSchema.Column column : entity.getColumnList()) {
                columnMap.put(column.getName(), column);
            }
        }
        return newSchema;
    }

    /**
     * 
     * @return
     * @throws SQLException
     */
    public List<String> validateSchema(DatabaseSchema schema) throws SQLException {
        return provide(new Transactionless<List<String>>() {
            @Override
            public List<String> run(Connection conn, Object...params) throws SQLException {
                List<String> resultList = new ArrayList<String>();
                DatabaseMetaData meta = conn.getMetaData();
                boolean valid = true;

                for (DatabaseSchema.Entity entity : schema.getEntityList()) {
                    // エンティティ定義あり、実体なしのチェック
                    // (逆のエンティティ定義なし、実体ありのチェックはしない)
                    String entityName = entity.getName();
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
                    for (DatabaseSchema.Column column : entity.getColumnList()) {
                        map.put(column.getName(), column);
                    }

                    // カラム定義なし、実体ありのチェック
                    while (crset.next()) {
                        String columnName = crset.getString("COLUMN_NAME");
                        DatabaseSchema.Column c = map.get(columnName);
                        if (c == null) {
                            String msg = LOG.error(DocsMessages.E_5109, entityName, columnName);
                            resultList.add(msg);
                            valid = false;
                        } else {
                            map.remove(columnName);
                            //TODO attrubute
                        }
                    }

                    // カラム定義あり、実体なしのチェック
                    for (Map.Entry<String,DatabaseSchema.Column> e : map.entrySet()) {
                        String msg = LOG.error(DocsMessages.E_5110, entityName, e.getKey());
                        resultList.add(msg);
                        valid = false;
                    }
                    crset.close();
                    LOG.info(DocsMessages.I_1104, entityName);
                }
                return resultList;
            }
        });
    }

    /**
     * 
     * @param callback
     * @param params
     * @return
     * @throws SQLException
     */
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

    /**
     * 
     * @throws SQLException
     */
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

    /**
     * 
     */
    public String generateDDL(DatabaseSchema.Table table) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Map<Integer,String> primaryMap = new TreeMap<Integer,String>();
        sb.append("CREATE TABLE IF NOT EXISTS " + table.getName() + " (\n");
        String sep = "    ";
        for (DatabaseSchema.Column column : table.getColumnList()) {
            sb.append(sep + column.getName() + " " + column.getType());
            if (column.getSize() > 0) {
                sb.append("(" + column.getSize() + ")");
            }
            if (column.isNotNull()) {
                sb.append(" NOT NULL");
            }
            if (column.isUnique()) {
                sb.append(" UNIQUE");
            }
            if (column.getPrimaryKey() > 0) {
                if (primaryMap.containsKey(column.getPrimaryKey())) {
                    throw new SQLException(DocsMessages.E_5122.getMessage(
                            column.getName(),
                            column.getPrimaryKey()));
                } else {
                    primaryMap.put(column.getPrimaryKey(), column.getName());
                }
            }
            sep = ",\n    ";
        }
        if (primaryMap.size() > 0) {
            sep = ",\n    CONSTRAINT PRIMARY KEY (";
            for (Entry<Integer,String> entry : primaryMap.entrySet()) {
                sb.append(sep + entry.getValue());
                sep = ", ";
            }
            sb.append(")\n");
        }
        sb.append(")");
        return sb.toString();
    }
}
