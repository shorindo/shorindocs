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

import static com.shorindo.docs.database.DatabaseMessages.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.BeanUtil;

/**
 * 
 */
public class DatabaseService {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseService.class);
    private static final Locale LANG = ApplicationContext.getLang();
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
            LOG.error(DTBS_5100, e);
        }
    }

    /**
     * 
     * @param is
     */
    public DatabaseSchema loadSchema(InputStream is) {
        DatabaseSchema newSchema = JAXB.unmarshal(is, DatabaseSchema.class);
        for (DatabaseSchema.Entity entity : newSchema.getEntityList()) {
            LOG.info(DTBS_1101, newSchema.getNamespace(), entity.getName());
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
    public List<String> validateSchema(DatabaseSchema schema) throws DatabaseException {
        return provide(new Transactionless<List<String>>() {
            @Override
            public List<String> run(Connection conn, Object...params) throws DatabaseException {
                try {
                    List<String> resultList = new ArrayList<String>();
                    DatabaseMetaData meta = conn.getMetaData();

                    for (DatabaseSchema.Entity entity : schema.getEntityList()) {
                        // エンティティ定義あり、実体なしのチェック
                        // (逆のエンティティ定義なし、実体ありのチェックはしない)
                        String entityName = entity.getName();
                        ResultSet trset = meta.getTables(null, null, entityName, null);
                        if (!trset.next()) {
                            String msg = LOG.error(DTBS_5108, entityName);
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
                                String msg = LOG.error(DTBS_5109, entityName, columnName);
                                resultList.add(msg);
                            } else {
                                map.remove(columnName);
                                //TODO attrubute
                            }
                        }

                        // カラム定義あり、実体なしのチェック
                        for (Map.Entry<String,DatabaseSchema.Column> e : map.entrySet()) {
                            String msg = LOG.error(DTBS_5110, entityName, e.getKey());
                            resultList.add(msg);
                        }
                        crset.close();
                        LOG.info(DTBS_1104, entityName);
                    }
                    return resultList;
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
            }
        });
    }

    /**
     * 
     * @param executor
     * @param params
     * @return
     * @throws SQLException
     */
    public <T>T provide(DatabaseExecutor<T> executor, Object...params) throws DatabaseException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            executor.setConnection(conn);
            executor.beginTransaction(conn);
            T result = executor.run(conn, params);
            executor.commitTransaction(conn);
            return result;
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    executor.rollbackTransaction(conn);
                } catch (DatabaseException e) {
                    LOG.error(DTBS_5105, e);
                }
            }
            throw new DatabaseException(th);
        } finally {
            executor.removeConnection();
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error(DTBS_5103, e);
                }
            }
        }
    }

    /**
     * 
     */
    public void loadTables() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tableSet = meta.getTables(null, null, null, null);
            while (tableSet.next()) {
                //LOG.info("TABLE[" + tableSet.getString("TABLE_NAME") + "]");
                ResultSet columnSet = meta.getColumns(
                        tableSet.getString("TABLE_CAT"),
                        tableSet.getString("TABLE_SCHEM"),
                        tableSet.getString("TABLE_NAME"),
                        null);
//                while (columnSet.next()) {
//                    LOG.info("    " +
//                            columnSet.getString("COLUMN_NAME") +
//                            " " +
//                            columnSet.getString("TYPE_NAME"));
//                }
            }
        }
    }

    /**
     * 
     */
    public int createTableFromSchema(DatabaseSchema.Table table) throws DatabaseException {
        return provide(new Transactionless<Integer>() {
            @Override
            public Integer run(Connection conn, Object... params)
                    throws DatabaseException {
                String ddl = generateDDL(table);
                return exec(ddl);
            }
        });
    }

    /**
     * 
     */
    public String generateDDL(DatabaseSchema.Table table) throws DatabaseException {
        StringBuilder sb = new StringBuilder();
        Map<Integer,String> primaryMap = new TreeMap<Integer,String>();
        sb.append("CREATE TABLE " + table.getName() + " (\n");
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
                    throw new DatabaseException(DTBS_5122.getMessage(
                            LANG,
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

    /*
     * 
     */
    public void generateSchemaEntity(DatabaseSchema schema) throws IOException {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("SchemaEntity.mustache"));
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(reader, "SchemaEntity.mustache");
        for (DatabaseSchema.Entity entity : schema.getEntityList()) {
            if (entity instanceof DatabaseSchema.Table) {
                Map<String,Object> entityMap = new HashMap<String,Object>();
                entityMap.put("packageName", schema.getNamespace());
                entityMap.put("className", BeanUtil.snake2camel(entity.getName(), true));
                entityMap.put("entityName", entity.getName());
                List<Map<String,Object>> columnList = new ArrayList<Map<String,Object>>();
                for (DatabaseSchema.Column column : entity.getColumnList()) {
                    Map<String,Object> columnMap = new HashMap<String,Object>();
                    columnMap.put("columnName", column.getName());
                    columnMap.put("fieldName", BeanUtil.snake2camel(column.getName(), false));
                    columnMap.put("FieldName", BeanUtil.snake2camel(column.getName(), true));
                    columnMap.put("type", column.getType());
                    columnMap.put("javaType", column.getJavaType());
                    columnMap.put("size", column.getSize());
                    columnMap.put("precision", column.getPrecision());
                    columnMap.put("primaryKey", column.getPrimaryKey());
                    columnMap.put("notNull", column.isNotNull());
                    columnMap.put("unique", column.isUnique());
                    columnMap.put("defaultValue", "null");
                    columnList.add(columnMap);
                }
                entityMap.put("columnList", columnList);
                mustache.execute(new PrintWriter(System.out), entityMap).flush();
            }
        }
    }
}
