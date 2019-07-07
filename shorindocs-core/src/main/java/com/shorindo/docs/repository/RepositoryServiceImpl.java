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

import java.io.IOException;
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
import java.util.Properties;
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.TransactionEvent;
import com.shorindo.docs.TransactionListener;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.QueryStatement.SelectStatement;
import com.shorindo.docs.repository.ExecuteStatement.*;

/**
 * 
 */
public class RepositoryServiceImpl implements RepositoryService, TransactionListener {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(RepositoryServiceImpl.class);
    private static final Locale LANG = ApplicationContext.getLang();
    private DataSource dataSource;
    private ThreadLocal<Map<String,Object>> threadMap =
            new ThreadLocal<Map<String,Object>>();

    /**
     * 
     */
    public RepositoryServiceImpl() {
        try {
            Properties props = new Properties();
            for (Entry<Object,Object> e : ApplicationContext.getProperties().entrySet()) {
                String key = (String)e.getKey();
                String val = (String)e.getValue();
                if (key.startsWith("datasource.") && val != null) {
                    props.setProperty(key.substring(11), val);
                }
            }
            dataSource = BasicDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            LOG.error(DBMS_5100, e);
        }
    }

    /**
     * 
     */
    public DatabaseSchema loadSchema(InputStream is) {
        try {
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
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.error(DBMS_5103, e);
            }
        }
    }

    /**
     * 
     */
    public List<String> validateSchema(DatabaseSchema schema) throws RepositoryException {
        Connection conn = null;
        try {
            conn = getConnection();
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
                if (resultList.size() == 0) {
                    LOG.info(DBMS_1104, entityName);
                } else {
                    LOG.error(DBMS_1107, entityName);
                }
            }
            return resultList;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        } finally {
            dispose(conn);
        }
    }


    /**
     * 
     */
    public int createTableFromSchema(DatabaseSchema.Table table) throws RepositoryException {
        String ddl = generateDDL(table);
        return execute(ddl);
    }

    /**
     * 
     */
    public String generateDDL(DatabaseSchema.Table table) throws RepositoryException {
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
                    throw new RepositoryException(DBMS_5122.getMessage(
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

    private Connection getThreadConnection() {
        Map<String,Object> map = threadMap.get();
        if (map == null) return null;
        else return (Connection)map.get(Connection.class.getName());
    }

    private void removeThreadConnection() {
        Connection conn = getThreadConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.error(DBMS_5103, e);
            }
        }
        threadMap.remove();
    }

    @Override
    public void onEvent(TransactionEvent event) {
        switch(event) {
        case BEGIN:
            transaction();
            break;
        case COMMIT:
            commit();
            break;
        case ROLLBACK:
            rollback();
            break;
        }
    }

    private void transaction() {
        Connection conn = getThreadConnection();
        try {
            if (conn == null) {
                conn = getConnection();
                LOG.debug(DBMS_1108);
                conn.setAutoCommit(false);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put(Connection.class.getName(), conn);
                threadMap.set(map);
            }
        } catch (Exception e) {
            LOG.error(DBMS_9999, e);
        }
    }

    private void commit() {
        try {
            Connection conn = getThreadConnection();
            if (conn != null) {
                LOG.debug(DBMS_1102);
                conn.commit();
            } else {
                LOG.warn(DBMS_3001);
            }
        } catch (Exception e) {
            LOG.error(DBMS_5104, e);
        } finally {
            removeThreadConnection();
        }
    }

    private void rollback() {
        try {
            Connection conn = getThreadConnection();
            if (conn != null) {
                LOG.debug(DBMS_1103);
                conn.rollback();
            } else {
                LOG.warn(DBMS_3001);
            }
        } catch (Exception e) {
            LOG.error(DBMS_5105, e);
        } finally {
            removeThreadConnection();
        }
    }

    private Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        LOG.debug(DBMS_1105, Integer.toHexString(conn.toString().hashCode()));
        return conn;
    }

    /**
     * 
     * @param conn
     */
    private void dispose(Connection conn) {
        if (conn != null)
            try {
                LOG.debug(DBMS_1106,Integer.toHexString(conn.toString().hashCode()));
                conn.close();
            } catch (SQLException e) {
                LOG.error(DBMS_5103, e);
            }
    }

    /**
     * 
     * @param stmt
     */
    private void dispose(Statement stmt) {
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error(DBMS_5106, e);
            }
    }

    /**
     * 
     * @param rset
     */
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
     */
    public final int execute(String sql, Object...params) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return execute(conn, sql, params);
        } else {
            try {
                conn = getConnection();
                //LOG.debug(DBMS_1106,Integer.toHexString(conn.hashCode()));
                return execute(conn, sql, params);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    /**
     * 
     */
    private final int execute(Connection conn, String sql, Object...params) throws RepositoryException {
        long st = System.currentTimeMillis();
        LOG.debug(DBMS_0001, sql);
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; params != null && i < params.length; i++) {
                stmt.setObject(i + 1, params[i]); // FIXME
            }
            int result = stmt.executeUpdate();
            LOG.debug(DBMS_0002, (System.currentTimeMillis() - st));
            return result;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * 
     */
    public final <E> List<E> queryList(String sql, Class<E> clazz, Object...params) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return query(conn, sql, clazz, params);
        } else {
            try {
                conn = getConnection();
                return query(conn, sql, clazz, params);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    /**
     *
     */
    public final <E> E querySingle(String sql, Class<E> clazz, Object...params) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            List<E> list = query(conn, sql, clazz, params);
            return list.size() == 0 ? null : list.get(0);
        } else {
            try {
                conn = getConnection();
                List<E> list = query(conn, sql, clazz, params);
                return list.size() == 0 ? null : list.get(0);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    /**
     * 
     */
    private final <E> List<E> query(Connection conn, String sql, Class<E> clazz, Object...params) throws RepositoryException {
        LOG.debug(DBMS_0003, sql);
        long st = System.currentTimeMillis();
        List<E> resultList = new ArrayList<E>();
        int index = 1;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        List<Object> paramList = new ArrayList<Object>();

        try {
            stmt = conn.prepareStatement(sql);
            for (Object param : params) {
                setColumnByClass(stmt, index, param);
                paramList.add(param);
                index++;
            }

            LOG.debug(DBMS_0011, paramList);
            rset = stmt.executeQuery();
            List<String> columnList = null;
            Map<String,Method> methodMap = new HashMap<String,Method>();
            for (Method method : clazz.getMethods()) {
                String methodName = method.getName();
                if (methodName.startsWith("set") && method.getParameterCount() == 1) {
                    methodMap.put(
                            BeanUtil.camel2snake(methodName.substring(3)),
                            method);
                }
            }
            while (rset.next()) {
                if (columnList == null) {
                    columnList = new ArrayList<String>();
                    ResultSetMetaData meta = rset.getMetaData();
                    int count = meta.getColumnCount();
                    for (int i = 1; i <= count; i++) {
                        columnList.add(meta.getColumnName(i));
                    }
                }
                E bean = clazz.newInstance();
                for (String columnName : columnList) {
                    Method method = methodMap.get(columnName);
                    if (method == null) {
                        LOG.warn(DBMS_5119, columnName, BeanUtil.snake2camel(columnName));
                        continue;
                    }
                    Class<?> type = method.getParameters()[0].getType();
                    if (String.class.isAssignableFrom(type)) {
                        method.invoke(bean, rset.getString(columnName));
                    } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
                        method.invoke(bean, fixInt(rset.getInt(columnName), rset.wasNull()));
                    } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
                        method.invoke(bean, fixLong(rset.getLong(columnName), rset.wasNull()));
                    } else if (Timestamp.class.isAssignableFrom(type)) {
                        method.invoke(bean, rset.getTimestamp(columnName));
                    } else if (Date.class.isAssignableFrom(type)) {
                        method.invoke(bean, Date.from(rset.getTimestamp(columnName).toInstant()));
                    } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
                        method.invoke(bean, fixShort(rset.getShort(columnName), rset.wasNull()));
                    } else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
                        method.invoke(bean, fixFloat(rset.getFloat(columnName), rset.wasNull()));
                    } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
                        method.invoke(bean, fixDouble(rset.getDouble(columnName), rset.wasNull()));
                    } else {
                        method.invoke(bean, rset.getObject(columnName));
                    }
                }
                resultList.add(bean);
            }
        } catch (InstantiationException e) {
            throw new RepositoryException(e);
        } catch (IllegalAccessException e) {
            throw new RepositoryException(e);
        } catch (IllegalArgumentException e) {
            throw new RepositoryException(e);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        } catch (InvocationTargetException e) {
            throw new RepositoryException(e);
        } finally {
            dispose(stmt);
            dispose(rset);
        }

        LOG.debug(DBMS_0004, (System.currentTimeMillis() - st) + " ms");
        return resultList;
    }

    private Boolean fixBoolean(boolean value, boolean isNull) {
        return isNull ? null : value;
    }

    private Byte fixByte(byte value, boolean isNull) {
        return isNull ? null : value;
    }

    private Short fixShort(short value, boolean isNull) {
        return isNull ? null : value;
    }

    private Integer fixInt(int value, boolean isNull) {
        return isNull ? null : value;
    }

    private Long fixLong(long value, boolean isNull) {
        return isNull ? null : value;
    }

    private Float fixFloat(float value, boolean isNull) {
        return isNull ? null : value;
    }

    private Double fixDouble(double value, boolean isNull) {
        return isNull ? null : value;
    }

    /**
     *
     */
    public final <E> E get(E entity)
            throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return get(conn, entity);
        } else {
            try {
                conn = getConnection();
                return get(conn, entity);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    /**
     * 
     */
    private final <E> E get(Connection conn, E entity) throws RepositoryException {
        SelectStatement select = getMapper(entity.getClass()).getSelect();
        return select.get(conn, entity);
    }

    /**
     *
     */
    public final int put(Object entity) throws RepositoryException {
        int result = 0;
        result = update(entity);
        if (result > 0) {
            return result;
        } else {
            return insert(entity);
        }
    }

    /**
     *
     */
    public final int delete(Object entity) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return delete(conn, entity);
        } else {
            try {
                conn = getConnection();
                return delete(conn, entity);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    /**
     * 
     */
    private final int delete(Connection conn, Object entity) throws RepositoryException {
        ExecuteStatement deleteStatement = getMapper(entity.getClass()).getDelete();
        return deleteStatement.execute(conn, entity);
    }

    /**
     * 
     */
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

    /**
     * 
     */
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

    private Object getValueByColumnName(Object entity, String name) throws RepositoryException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && name.equals(column.name())) {
                try {
                    field.setAccessible(true);
                    return field.get(entity);
                } catch (IllegalArgumentException e) {
                    throw new RepositoryException(DBMS_5119.getMessage(LANG, name));
                } catch (IllegalAccessException e) {
                    throw new RepositoryException(DBMS_5119.getMessage(LANG, name));
                }
            }
        }
        throw new RepositoryException(DBMS_5124.getMessage(LANG, name));
    }

    /**
     * 
     */
    private int applySetMethod(Statement stmt, Object entity, ColumnMapping mapping, int index)
            throws RepositoryException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //LOG.debug("applySetMethod(" + mapping.getStatementSetMethod() + "," + mapping.getField() + ")");
        Method setMethod = mapping.getStatementSetMethod();
        setMethod.invoke(stmt, index, getValueByColumnName(entity, mapping.getColumnName()));
        return index + 1;
    }

    /**
     * 
     */
    private String getEntityName(Object entity) throws RepositoryException {
        Table table = entity.getClass().getAnnotation(Table.class);
        if (table != null) {
            return table.value();
        } else {
            throw new RepositoryException(DBMS_5125);
        }
    }

    /**
     * 
     */
    @SuppressWarnings("resource")
    private EntityMapping bind(Connection conn, Object entity) throws RepositoryException {
        String entityName = getEntityName(entity);
        EntityMapping entityMapping = new EntityMapping(entityName);
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

            primarySet = meta.getPrimaryKeys(null, null, entityName);
            Map<String,Integer> primaryOrder = new HashMap<String,Integer>();
            while (primarySet.next()) {
                String columnName = primarySet.getString("COLUMN_NAME");
                int seq = primarySet.getInt("KEY_SEQ");
                primaryOrder.put(columnName, seq);
            }

            columnSet = meta.getColumns(null,  null, entityName,  null);
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
                    throw new SQLException(DBMS_5119.getMessage(LANG, columnName, columnName));
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
            throw new RepositoryException(e);
        } finally {
            dispose(primarySet);
            dispose(columnSet);
        }

        entityMapping.build();
//        LOG.debug("bind(" + entity.getClass().getSimpleName() + ") : elapsed " +
//                (System.currentTimeMillis() - st) + " ms");
        return entityMapping;
    }

    /**
     * 
     */
    private Method[] getStatementSetMethod(Class<?> clazz) {
        //LOG.debug("getStatementSetMethod(" + clazz + ")");
        try {
            switch (SqlType.assignable(clazz)) {
            case BOOLEAN:
            case BOOLEAN_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setBoolean", int.class, boolean.class),
                        ResultSet.class.getMethod("getBoolean", int.class)
                };
            case BYTE:
            case BYTE_OBJECT:
                return new Method[] {
                        PreparedStatement.class.getMethod("setByte", int.class, byte.class),
                        ResultSet.class.getMethod("getByte", int.class)
                };
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

//    protected void placeHolder(PreparedStatement stmt, int index, Object param) throws SQLException {
//        if (param == null) {
//            stmt.setObject(index, null);
//            return;
//        }
//        Class<?> clazz = param.getClass();
//        if (String.class.isAssignableFrom(clazz)) {
//            stmt.setString(index, (String)param);
//        } else if (short.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz)) {
//            stmt.setShort(index, (short)param);
//        } else if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
//            stmt.setInt(index, (int)param);
//        } else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
//            stmt.setLong(index, (long)param);
//        } else if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
//            stmt.setFloat(index, (float)param);
//        } else if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
//            stmt.setDouble(index, (double)param);
//        } else if (Date.class.isAssignableFrom(clazz)) {
//            //FIXME
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            stmt.setString(index, format.format((Date)param));
//        }
//    }

    /**
     * 
     */
    protected enum SqlType {
        BOOLEAN     (boolean.class),
        BOOLEAN_OBJECT(Boolean.class),
        BYTE        (byte.class),
        BYTE_OBJECT (Byte.class),
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

    /**
     * 
     */
    private static class ResultSetMapper {
        private Method getter;
        private Method setter;

        public Method getGetter() {
            return getter;
        }
        public Method getSetter() {
            return setter;
        }
    }

    public int insert(Object entity) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return insert(conn, entity);
        } else {
            try {
                conn = getConnection();
                return insert(conn, entity);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    private int insert(Connection conn, Object entity) throws RepositoryException {
        ExecuteStatement insertStatement = getMapper(entity.getClass()).getInsert();
        return insertStatement.execute(conn, entity);
    }

    @Override
    public int update(Object entity) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return update(conn, entity);
        } else {
            try {
                conn = getConnection();
                return update(conn, entity);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    private int update(Connection conn, Object entity) throws RepositoryException {
        UpdateStatement stmt = getMapper(entity.getClass()).getUpdate();
        return stmt.execute(conn, entity);
    }

    private Map<Class<?>, StatementMap> mapperMap = new HashMap<Class<?>, StatementMap>();
    protected synchronized StatementMap getMapper(Class<?> clazz) throws RepositoryException {
        if (!mapperMap.containsKey(clazz)) {
            mapperMap.put(clazz, new StatementMap(clazz));
        }
        return mapperMap.get(clazz);
    }

    private static class StatementMap {
        private InsertStatement insert;
        private UpdateStatement update;
        private DeleteStatement delete;
        private SelectStatement select;

        public StatementMap(Class<?> clazz) throws RepositoryException {
            insert = new InsertStatement(clazz);
            update = new UpdateStatement(clazz);
            delete = new DeleteStatement(clazz);
            select = new SelectStatement(clazz);
        }

        public InsertStatement getInsert() {
            return insert;
        }

        public UpdateStatement getUpdate() {
            return update;
        }

        public DeleteStatement getDelete() {
            return delete;
        }

        public SelectStatement getSelect() {
            return select;
        }
    }
}
