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
package com.shorindo.docs.repository;

import static com.shorindo.docs.repository.DatabaseMessages.*;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.IdentityProvider;

/**
 * 
 */
public class RepositoryService {
    private static final ActionLogger LOG = ActionLogger.getLogger(RepositoryService.class);
    private static final Locale LANG = ApplicationContext.getLang();
    private DataSource dataSource;
    private ThreadLocal<Connection> threadConnection = new ThreadLocal<Connection>();

    /**
     * 
     */
    protected RepositoryService() {
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
            LOG.error(DBMS_5100, e);
        }
    }

    /**
     * 
     * @param is
     */
    public DatabaseSchema loadSchema(InputStream is) {
        DatabaseSchema newSchema = JAXB.unmarshal(is, DatabaseSchema.class);
        for (DatabaseSchema.Entity entity : newSchema.getEntityList()) {
            LOG.info(DBMS_1101, newSchema.getNamespace(), entity.getName());
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
//        return provide(new Transactionless<List<String>>() {
//            @Override
//            public List<String> run(Connection conn, Object...params) throws DatabaseException {
                try {
                    Connection conn = getConnection();
                    List<String> resultList = new ArrayList<String>();
                    DatabaseMetaData meta = conn.getMetaData();

                    for (DatabaseSchema.Entity entity : schema.getEntityList()) {
                        // エンティティ定義あり、実体なしのチェック
                        // (逆のエンティティ定義なし、実体ありのチェックはしない)
                        String entityName = entity.getName();
                        ResultSet trset = meta.getTables(null, null, entityName, null);
                        if (!trset.next()) {
                            String msg = LOG.error(DBMS_5108, entityName);
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
                                String msg = LOG.error(DBMS_5109, entityName, columnName);
                                resultList.add(msg);
                            } else {
                                map.remove(columnName);
                                //TODO attrubute
                            }
                        }

                        // カラム定義あり、実体なしのチェック
                        for (Map.Entry<String,DatabaseSchema.Column> e : map.entrySet()) {
                            String msg = LOG.error(DBMS_5110, entityName, e.getKey());
                            resultList.add(msg);
                        }
                        crset.close();
                        LOG.info(DBMS_1104, entityName);
                    }
                    return resultList;
                } catch (SQLException e) {
                    throw new DatabaseException(e);
                }
//            }
//        });
    }

    /**
     * 
     * @param executor
     * @param params
     * @return
     * @throws SQLException
     */
//    public <T>T provide(DatabaseExecutor<T> executor, Object...params) throws DatabaseException {
//        Connection conn = null;
//        try {
//            conn = dataSource.getConnection();
//            executor.setConnection(conn);
//            executor.beginTransaction(conn);
//            T result = executor.run(conn, params);
//            executor.commitTransaction(conn);
//            return result;
//        } catch (Throwable th) {
//            if (conn != null) {
//                try {
//                    executor.rollbackTransaction(conn);
//                } catch (DatabaseException e) {
//                    LOG.error(DBMS_5105, e);
//                }
//            }
//            throw new DatabaseException(th);
//        } finally {
//            executor.removeConnection();
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    LOG.error(DBMS_5103, e);
//                }
//            }
//        }
//    }

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
//        return provide(new Transactionless<Integer>() {
//            @Override
//            public Integer run(Connection conn, Object... params)
//                    throws DatabaseException {
                String ddl = generateDDL(table);
                return execute(ddl);
//            }
//        });
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
                    throw new DatabaseException(DBMS_5122.getMessage(
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

    /**
     * トランザクションを実行する。
     * @param r
     * @throws SQLException 
     */
    public void transaction(Runnable runnable) throws DatabaseException {
        try {
            Connection conn = threadConnection.get();
            if (conn == null) {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);
                threadConnection.set(conn);

                try {
                    runnable.run();
                    conn.commit();
                } catch (Throwable th) {
                    conn.rollback();
                    throw th;
                } finally {
                    threadConnection.remove();
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } else {
                runnable.run();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /*
     * TODO クローズ処理
     */
    private synchronized Connection getConnection() throws DatabaseException {
        try {
            Connection conn = threadConnection.get();
            if (conn == null) {
                conn = dataSource.getConnection();
                threadConnection.set(conn);
            }
            return conn;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private void dispose(Statement stmt) {
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error(DBMS_5106, e);
            }
    }

    private void dispose(ResultSet rset) {
        if (rset != null)
            try {
                rset.close();
            } catch (Exception e) {
                LOG.error(DBMS_5107, e);
            }
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public final int execute(String sql, Object...params) throws DatabaseException {
        long st = System.currentTimeMillis();
        String hash = IdentityProvider.hash(sql);
        LOG.debug(DBMS_0001, hash, sql);
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            //FIXME
            int i = 1;
            for (Object param : params) {
                stmt.setObject(i++, param);
            }
            int result = stmt.executeUpdate();
            LOG.debug(DBMS_0002, hash, (System.currentTimeMillis() - st));
            return result;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * 
     * @param sql
     * @param clazz
     * @param params
     * @return
     * @throws SQLException
     */
    public final <E> List<E> query(String sql, Class<E> clazz, Object...params) throws DatabaseException {
        LOG.debug(DBMS_0003, sql);
        long st = System.currentTimeMillis();
        List<E> resultList = new ArrayList<E>();
        int index = 1;
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            stmt = conn.prepareStatement(sql);
            for (Object param : params) {
                setColumnByClass(stmt, index, param);
                index++;
            }

            rset = stmt.executeQuery();
            ResultSetMapper[] mappers = null;
            while (rset.next()) {
                mappers = generateMappers(mappers, rset.getMetaData(), clazz);
                E bean = applyMappers(mappers, rset, clazz);
                resultList.add(bean);
            }
        } catch (InstantiationException e) {
            throw new DatabaseException(e);
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e);
        } catch (InvocationTargetException e) {
            throw new DatabaseException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(stmt);
            dispose(rset);
        }

        LOG.debug(DBMS_0004, (System.currentTimeMillis() - st) + " ms");
        return resultList;
    }

    /**
     * DatabaseExecutorからコピー
     * @param entity
     * @return
     * @throws SQLException
     */
    public final <E extends SchemaEntity> E get(E entity) throws DatabaseException {
        Connection conn = getConnection();
        EntityMapping mapping = bind(conn, entity);
        LOG.debug(DBMS_0003, mapping.getSelectSql());
        long st = System.currentTimeMillis();
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            stmt = conn.prepareStatement(mapping.getSelectSql());
            int index = 1;
            for (ColumnMapping columnMapping : mapping.getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    index = applySetMethod(stmt, entity, columnMapping, index);
                }
            }
            rset = stmt.executeQuery();
            if (rset.next()) {
                index = 1;
                for (ColumnMapping columnMapping : mapping.getColumns()) {
                    //LOG.debug("カラム[" + index + ", " + columnMapping.getColumnName() + "]");
                    applyGetMethod(rset, entity, columnMapping, index);
                    index++;
                }
            } else {
                entity = null;
            }
            LOG.debug(DBMS_0004, (System.currentTimeMillis() - st) + " ms");
            return entity;
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e);
        } catch (InvocationTargetException e) {
            throw new DatabaseException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(stmt);
            dispose(rset);
        }
    }

    /**
     * DatabaseExecutorからコピー
     * @param entity
     * @return
     * @throws SQLException
     */
    public final int put(SchemaEntity entity) throws DatabaseException {
        int result = 0;
        result = update(entity);
        if (result > 0) {
            return result;
        } else {
            return insert(entity);
        }
    }

    /**
     * DatabaseExecutorからコピー
     * @param document
     * @return
     * @throws SQLException
     */
    public final int remove(SchemaEntity document) throws DatabaseException {
        Connection conn = getConnection();
        EntityMapping mapping = bind(conn, document);
        LOG.debug(DBMS_0005, mapping.getUpdateSql());
        PreparedStatement stmt = null;
        int index = 1;

        try {
            stmt = conn.prepareStatement(mapping.getDeleteSql());
            for (ColumnMapping columnMapping : mapping.getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    index = applySetMethod(stmt, document, columnMapping, index);
                }
            }
            return stmt.executeUpdate();
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e);
        } catch (InvocationTargetException e) {
            throw new DatabaseException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(stmt);
        }
    }

    /**
     * 
     * @param entity
     * @return
     * @throws SQLException
     */
    protected int insert(SchemaEntity entity) throws DatabaseException {
        Connection conn = getConnection();
        EntityMapping mapping = bind(conn, entity);
        LOG.debug(DBMS_0007, mapping.getInsertSql());
        PreparedStatement stmt = null;
        int index = 1;
        try {
            stmt = conn.prepareStatement(mapping.getInsertSql());
            for (ColumnMapping columnMapping : mapping.getColumns()) {
                index = applySetMethod(stmt, entity, columnMapping, index);
            }
            return stmt.executeUpdate();
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e);
        } catch (InvocationTargetException e) {
            throw new DatabaseException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(stmt);
        }
    }

    /**
     * 
     * @param entity
     * @return
     * @throws SQLException
     */
    protected int update(SchemaEntity entity) throws DatabaseException {
        Connection conn = getConnection();
        EntityMapping mapping = bind(conn, entity);
        LOG.debug(DBMS_0009, mapping.getUpdateSql());
        PreparedStatement stmt = null;
        int index = 1;
        try {
            stmt = conn.prepareStatement(mapping.getUpdateSql());
            for (ColumnMapping columnMapping : mapping.getColumns()) {
                if (columnMapping.getPrimaryKey() <= 0) {
                    index = applySetMethod(stmt, entity, columnMapping, index);
                }
            }
            for (ColumnMapping columnMapping : mapping.getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    index = applySetMethod(stmt, entity, columnMapping, index);
                }
            }
            return stmt.executeUpdate();
        } catch (IllegalAccessException e) {
            throw new DatabaseException(e);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e);
        } catch (InvocationTargetException e) {
            throw new DatabaseException(e);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(stmt);
        }
    }

    private void setColumnByClass(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value == null) {
            stmt.setObject(index, value);
        } else if (String.class.isAssignableFrom(value.getClass())) {
            stmt.setString(index, (String)value);
        } else if (short.class.isAssignableFrom(value.getClass())) {
            stmt.setShort(index, (short)value);
        } else if (Short.class.isAssignableFrom(value.getClass())) {
            stmt.setShort(index, (Short)value);
        } else if (int.class.isAssignableFrom(value.getClass())) {
            stmt.setInt(index, (int)value);
        } else if (Integer.class.isAssignableFrom(value.getClass())) {
            stmt.setInt(index, (Integer)value);
        } else if (long.class.isAssignableFrom(value.getClass())) {
            stmt.setLong(index, (long)value);
        } else if (Long.class.isAssignableFrom(value.getClass())) {
            stmt.setLong(index, (Long)value);
        } else if (float.class.isAssignableFrom(value.getClass())) {
            stmt.setFloat(index, (float)value);
        } else if (Float.class.isAssignableFrom(value.getClass())) {
            stmt.setFloat(index, (Float)value);
        } else if (double.class.isAssignableFrom(value.getClass())) {
            stmt.setDouble(index, (double)value);
        } else if (Double.class.isAssignableFrom(value.getClass())) {
            stmt.setDouble(index, (Double)value);
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            stmt.setDate(index, (java.sql.Date)value);
        } else if (Time.class.isAssignableFrom(value.getClass())) {
            stmt.setTime(index, (Time)value);
        } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
            stmt.setTimestamp(index, (Timestamp)value);
        } else {
            stmt.setObject(index, value);
        }
    }
    private Method getResultSetGetter(Class<?> returnType) throws NoSuchMethodException, SecurityException {
        if (String.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getString", int.class);
        } else if (short.class.isAssignableFrom(returnType) ||
                Short.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getShort", int.class);
        } else if (int.class.isAssignableFrom(returnType) ||
                Integer.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getInt", int.class);
        } else if (long.class.isAssignableFrom(returnType) ||
                Long.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getLong", int.class);
        } else if (float.class.isAssignableFrom(returnType) ||
                Float.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getFloat", int.class);
        } else if (double.class.isAssignableFrom(returnType) ||
                Double.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getDouble", int.class);
        } else if (Date.class.isAssignableFrom(returnType)) {
            return ResultSet.class.getMethod("getTimestamp", int.class);
        } else {
            return ResultSet.class.getMethod("getObject", int.class);
        }
        
    }


    private boolean isPrimitive(Class<?> target) {
        return target.isPrimitive() ||
                target.isAssignableFrom(Short.class) ||
                target.isAssignableFrom(Integer.class) ||
                target.isAssignableFrom(Long.class) ||
                target.isAssignableFrom(Float.class) ||
                target.isAssignableFrom(Double.class) ||
                target.isAssignableFrom(Date.class) ||
                target.isAssignableFrom(String.class);
    }
    
    private static Pattern SNAKE_PATTERN = Pattern.compile("_*([^_])([^_]*)");
    private static String snake2camel(String prefix, String name) {
        Matcher m = SNAKE_PATTERN.matcher(name);
        StringBuilder sb = new StringBuilder(prefix);
        int start = 0;
        while (m.find(start)) {
            sb.append(m.group(1).toUpperCase());
            String rest = m.group(2);
            if (rest != null) {
                sb.append(rest.toLowerCase());
            }
            start = m.end();
        }
        return sb.toString();
    }

    private ResultSetMapper[] generateMappers(ResultSetMapper[] mappers, ResultSetMetaData meta, Class<?> target) throws SQLException {
        if (mappers != null) {
            return mappers;
        }

        int count = meta.getColumnCount();
        mappers = new ResultSetMapper[count];

        // targetがprimitiveかStringで、戻り値が１つのときの扱い
        if (count == 1 && isPrimitive(target)) {
            try {
                ResultSetMapper mapper = new ResultSetMapper();
                mapper.setGetter(getResultSetGetter(target));
                mappers[0] = mapper;
            } catch (NoSuchMethodException e) {
                throw new SQLException(e);
            } catch (SecurityException e) {
                throw new SQLException(e);
            }
            return mappers;
        }

        for (int index = 1; index <= count; index++) {
            try {
                ResultSetMapper mapper = new ResultSetMapper();
                String columnName = meta.getColumnName(index);
                String getterName = snake2camel("get", columnName);
                Method getter = target.getMethod(getterName);
                Class<?> returnType = getter.getReturnType();
                String setterName = snake2camel("set", columnName);
                Method setter = target.getMethod(setterName, returnType);
                mapper.setSetter(setter);
                mapper.setGetter(getResultSetGetter(returnType));
                mappers[index - 1] = mapper;
            } catch (NoSuchMethodException e) {
                throw new SQLException(e);
            } catch (SecurityException e) {
                throw new SQLException(e);
            }
        }

        return mappers;
    }

    @SuppressWarnings("unchecked")
    protected <E> E applyMappers(ResultSetMapper[] mappers, ResultSet rset, Class<E> beanClass)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
        if (mappers.length == 1 && mappers[0].getSetter() == null) {
            return (E)mappers[0].getGetter().invoke(rset, 1);
        }

        E bean = beanClass.newInstance();
        for (int i = 0; i < mappers.length; i++) {
            Method setter = mappers[i].getSetter();
            Method getter = mappers[i].getGetter();
            setter.invoke(bean, getter.invoke(rset, i + 1));
        }
        return bean;
    }

    private int applySetMethod(Statement stmt, SchemaEntity entity, ColumnMapping mapping, int index)
            throws DatabaseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //LOG.debug("applySetMethod(" + mapping.getStatementSetMethod() + "," + mapping.getField() + ")");
        Method setMethod = mapping.getStatementSetMethod();
        setMethod.invoke(stmt, index, entity.getByName(mapping.getColumnName()));
        return index + 1;
    }

    private void applyGetMethod(ResultSet rset, SchemaEntity entity, ColumnMapping mapping, int index)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getMethod = mapping.getResultSetGetMethod();
        Field field = mapping.getField();
        field.setAccessible(true);
        field.set(entity, getMethod.invoke(rset, index));
    }

    @SuppressWarnings("resource")
    private EntityMapping bind(Connection conn, SchemaEntity entity) throws DatabaseException {
        EntityMapping entityMapping = new EntityMapping(entity.getEntityName());
        ResultSet primarySet = null;
        ResultSet columnSet = null;

        try {
            DatabaseMetaData meta = conn.getMetaData();

            // 対象フィールドの一覧取得
            Map<String,Field> fieldMap = new TreeMap<String,Field>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    fieldMap.put(column.name(), field);
                }
            }

            primarySet = meta.getPrimaryKeys(null, null, entity.getEntityName());
            Map<String,Integer> primaryOrder = new HashMap<String,Integer>();
            while (primarySet.next()) {
                String columnName = primarySet.getString("COLUMN_NAME");
                int seq = primarySet.getInt("KEY_SEQ");
                primaryOrder.put(columnName, seq);
            }

            columnSet = meta.getColumns(null,  null, entity.getEntityName(),  null);
            while (columnSet.next()) {
                String columnName = columnSet.getString("COLUMN_NAME");
                ColumnMapping columnCache = new ColumnMapping(columnName);

                // PKのセット
                Integer pk = primaryOrder.get(columnName);
                if (pk != null && pk > 0) {
                    columnCache.setPrimaryKey(pk);
                }

                // フィールドのセット
                Field field = fieldMap.get(columnName);
                if (field != null) {
                    columnCache.setField(field);
                } else {
                    throw new SQLException(DBMS_5119.getMessage(LANG, columnName));
                }

                // PreparedStatement/ResultSetのsetter/getter
                Method setter[] = getStatementSetMethod(field.getType());
                if (setter != null) {
                    columnCache.setStatementSetMethod(setter[0]);
                    columnCache.setResultSetGetMethod(setter[1]);
                } else {
                    throw new SQLException(DBMS_5119.getMessage(LANG, field.getName()));
                }

                entityMapping.putColumn(columnName, columnCache);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            dispose(primarySet);
            dispose(columnSet);
        }

        entityMapping.build();
//        LOG.debug("bind(" + entity.getClass().getSimpleName() + ") : elapsed " +
//                (System.currentTimeMillis() - st) + " ms");
        return entityMapping;
    }

    private Method[] getStatementSetMethod(Class<?> clazz) {
        //LOG.debug("getStatementSetMethod(" + clazz + ")");
        try {
            switch (SqlType.assignable(clazz)) {
            case SHORT:
            case SHORT_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setShort", int.class, short.class),
                        ResultSet.class.getMethod("getShort", int.class)
                };
            case INT:
            case INT_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setInt", int.class, int.class),
                        ResultSet.class.getMethod("getInt", int.class)
                };
            case LONG:
            case LONG_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setLong", int.class, long.class),
                        ResultSet.class.getMethod("getLong", int.class)
                };
            case FLOAT:
            case FLOAT_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setFloat", int.class, float.class),
                        ResultSet.class.getMethod("getFloat", int.class)
                };
            case DOUBLE:
            case DOUBLE_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setDouble", int.class, double.class),
                        ResultSet.class.getMethod("getDouble", int.class)
                };
            case STRING:
                return new Method[] {
                        PreparedStatement.class.getMethod("setString", int.class, String.class),
                        ResultSet.class.getMethod("getString", int.class)
                };
            case DATE:
                return new Method[] {
                        PreparedStatement.class.getMethod("setDate", int.class, java.sql.Date.class),
                        ResultSet.class.getMethod("getDate", int.class)
                };
            case TIMESTAMP:
                return new Method[] {
                        PreparedStatement.class.getMethod("setTimestamp", int.class, Timestamp.class),
                        ResultSet.class.getMethod("getTimestamp", int.class)
                };
            default:
                return new Method[] {
                        PreparedStatement.class.getMethod("setObject", int.class, Object.class),
                        ResultSet.class.getMethod("getObject", int.class)
                };
            }
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

    protected enum SqlType {
        SHORT       (short.class),
        SHORT_OBJECT(Short.class),
        INT         (int.class),
        INT_OBJECT  (Integer.class),
        LONG        (long.class),
        LONG_OBJECT (Long.class),
        FLOAT       (float.class),
        FLOAT_OBJECT(Float.class),
        DOUBLE      (double.class),
        DOUBLE_OBJECT(Double.class),
        STRING      (String.class),
        TIMESTAMP   (Timestamp.class),
        DATE        (Date.class)
        ;
        
        private Class<?> type;
        private SqlType(Class<?> type) {
            this.type = type;
        }
        public static SqlType assignable(Class<?> typeClass) {
            for (SqlType type : values()) {
                if (type.getType().isAssignableFrom(typeClass)) {
                    return type;
                }
            }
            return null;
        }
        public Class<?> getType() {
            return type;
        }
    }

    /**
     * 
     */
    protected static class EntityMapping {
        private String entityName;
        private String selectSql;
        private String insertSql;
        private String updateSql;
        private String deleteSql;
        private Map<String,ColumnMapping> columnMap;

        public EntityMapping(String entityName) {
            this.entityName = entityName;
            this.columnMap = new LinkedHashMap<String,ColumnMapping>();
        }
        public void build() {
            buildSelect();
            buildInsert();
            buildUpdate();
            buildDelete();
        }
        private void buildSelect() {
            StringBuilder sb = new StringBuilder("SELECT ");
            String sep = "";
            for (ColumnMapping columnMapping : getColumns()) {
                sb.append(sep + columnMapping.getColumnName());
                sep = ",";
            }
            sb.append(" FROM " + getEntityName() + " WHERE ");
            sep = "";
            for (ColumnMapping columnMapping : getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    sb.append(sep + columnMapping.getColumnName() + "=?");
                    sep = " AND ";
                }
            }
            selectSql = sb.toString();
        }
        private void buildInsert() {
            StringBuilder sb = new StringBuilder("INSERT INTO " + getEntityName() + " (");
            String sep = "";
            for (ColumnMapping columnMapping : getColumns()) {
                sb.append(sep + columnMapping.getColumnName());
                sep = ",";
            }
            sb.append(") VALUES (");
            sep = "";
            for (ColumnMapping columnMapping : getColumns()) {
                sb.append(sep + "?");
                sep = ",";
            }
            sb.append(")");
            insertSql = sb.toString();
        }
        private void buildUpdate() {
            StringBuilder sb = new StringBuilder("UPDATE " + getEntityName() + " SET ");
            String sep = "";

            for (ColumnMapping columnMapping : getColumns()) {
                if (columnMapping.getPrimaryKey() <= 0) {
                    sb.append(sep + columnMapping.getColumnName() + "=?");
                    sep = ",";
                }
            }
            sb.append(" WHERE ");
            sep = "";
            for (ColumnMapping columnMapping : getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    sb.append(sep + columnMapping.getColumnName() + "=?");
                    sep = " AND ";
                }
            }
            updateSql = sb.toString();
        }
        private void buildDelete() {
            StringBuilder sb = new StringBuilder("DELETE FROM " + getEntityName() + " WHERE ");
            String sep = "";

            for (ColumnMapping columnMapping : getColumns()) {
                if (columnMapping.getPrimaryKey() > 0) {
                    sb.append(sep + columnMapping.getColumnName() + "=?");
                    sep = " AND ";
                }
            }
            deleteSql = sb.toString();
        }
        public String getEntityName() {
            return entityName;
        }
        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }
        public String getSelectSql() {
            return selectSql;
        }
        public String getInsertSql() {
            return insertSql;
        }
        public String getUpdateSql() {
            return updateSql;
        }
        public String getDeleteSql() {
            return deleteSql;
        }
        public ColumnMapping[] getColumns() {
            ColumnMapping[] result = new ColumnMapping[columnMap.size()];
            int i = 0;
            for (Entry<String,ColumnMapping> entry : columnMap.entrySet()) {
                result[i] = entry.getValue();
                i++;
            }
            return result;
        }
        public void putColumn(String columnName, ColumnMapping columnCache) {
            this.columnMap.put(columnName, columnCache);
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(getEntityName() + " : {\n");
            String sep = "    ";
            for (ColumnMapping columnCache : getColumns()) {
                sb.append(sep + columnCache);
                sep = ",\n    ";
            }
            sb.append("\n}");
            return sb.toString();
        }
    }

    /**
     * 
     */
    protected static class ColumnMapping {
        private String columnName;
        private Field field;
        private Method statementSetMethod;
        private Method resultSetGetMethod;
        private int primaryKey = -1;

        public ColumnMapping(String columnName) {
            this.columnName = columnName;
        }
        public String getColumnName() {
            return columnName;
        }
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
        public Field getField() {
            return field;
        }
        public void setField(Field field) {
            this.field = field;
        }
        public Method getStatementSetMethod() {
            return statementSetMethod;
        }
        public void setStatementSetMethod(Method statementSetMethod) {
            this.statementSetMethod = statementSetMethod;
        }
        public Method getResultSetGetMethod() {
            return resultSetGetMethod;
        }
        public void setResultSetGetMethod(Method resultSetGetMethod) {
            this.resultSetGetMethod = resultSetGetMethod;
        }
        public int getPrimaryKey() {
            return primaryKey;
        }
        public void setPrimaryKey(int primaryKey) {
            this.primaryKey = primaryKey;
        }
        @Override
        public String toString() {
            return "[" +
                    columnName + ", " +
                    field.getName() + ", " +
                    statementSetMethod.getName() + ", " +
                    resultSetGetMethod.getName() + ", " +
                    primaryKey + "]";
        }
    }

    private static class ResultSetMapper {
        private Method getter;
        private Method setter;

        public Method getGetter() {
            return getter;
        }
        public void setGetter(Method getter) {
            this.getter = getter;
        }
        public Method getSetter() {
            return setter;
        }
        public void setSetter(Method setter) {
            this.setter = setter;
        }
    }
}
