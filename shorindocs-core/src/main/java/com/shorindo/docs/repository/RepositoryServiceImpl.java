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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.util.Stack;
import java.util.TreeMap;

import javax.sql.DataSource;
import javax.xml.bind.JAXB;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.TxEvent;
import com.shorindo.docs.TxEventListener;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.QueryStatement.SelectStatement;
import com.shorindo.docs.repository.ExecuteStatement.*;

/**
 * 
 */
public class RepositoryServiceImpl implements RepositoryService, TxEventListener {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(RepositoryServiceImpl.class);
    private DataSource dataSource;
    private ThreadLocal<TxConnection> txMap =
            new ThreadLocal<TxConnection>();

    /**
     * 
     */
    public RepositoryServiceImpl(DataSource dataSource) {
    	this.dataSource = dataSource;
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
            } catch (Exception e) {
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
                            ApplicationContext.getLang(),
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

    private synchronized Connection getThreadConnection() {
        TxConnection tx = txMap.get();
        if (tx == null) return null;
        else return tx.getConnection();
    }

    private void removeThreadConnection() {
        TxConnection txConn = txMap.get();
        Connection conn = txConn.getConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.error(DBMS_5103, e);
            }
        }
        txMap.remove();
    }

    @Override
    public synchronized void onEvent(TxEvent event) {
        switch(event.getType()) {
        case BEGIN:
            transaction(event);
            break;
        case COMMIT:
            commit(event);
            break;
        case ROLLBACK:
            rollback(event);
            break;
        }
    }

    private void transaction(TxEvent event) {
        TxConnection txConn = txMap.get();
        try {
            if (txConn == null) {
                Connection conn = getConnection();
                conn.setAutoCommit(false);
                txConn = new TxConnection(conn);
                LOG.debug(DBMS_1108, String.format("%x", txConn.hashCode()));
                txMap.set(txConn);
            }
            txConn.pushEvent(event);
        } catch (Exception e) {
            LOG.error(DBMS_9999, e);
        }
    }

    private void commit(TxEvent event) {
        try {
            TxConnection txConn = txMap.get();
            if (txConn.popEvent() > 0) {
                LOG.info(DBMS_1109, String.format("%x", txConn.hashCode()));
                return;
            }

            Connection conn = txConn.getConnection();
            if (conn == null) {
                LOG.warn(DBMS_3001);
                return;
            }
            
            LOG.debug(DBMS_1102, String.format("%x", txConn.hashCode()));
            try {
                conn.commit();
            } finally {
                removeThreadConnection();
            }
        } catch (Exception e) {
            LOG.error(DBMS_5104, e);
        }
    }

    private void rollback(TxEvent event) {
        try {
            TxConnection txConn = txMap.get();
            if (txConn.popEvent() > 0) {
                LOG.info(DBMS_1110, String.format("%x", txConn.hashCode()));
                return;
            }

            Connection conn = txConn.getConnection();
            if (conn == null) {
                LOG.warn(DBMS_3001);
                return;
            }

            LOG.debug(DBMS_1103, String.format("%x", txConn.hashCode()));
            try {
                conn.rollback();
            } finally {
                removeThreadConnection();
            }
        } catch (Exception e) {
            LOG.error(DBMS_5105, e);
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
            ExecuteStatement stmt = new ExecuteStatement();
            return stmt.execute(conn, sql, params);
        } finally {
            LOG.debug(DBMS_0002, (System.currentTimeMillis() - st));
        }
    }

    /**
     * 
     */
    public final <E> List<E> queryList(String sql, Class<E> clazz, Object...params) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return queryList(conn, sql, clazz, params);
        } else {
            try {
                conn = getConnection();
                return queryList(conn, sql, clazz, params);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    private <E> List<E> queryList(Connection conn, String sql, Class<E> clazz, Object...params)
            throws RepositoryException {
        QueryStatement stmt = new QueryStatement(clazz);
        return stmt.queryList(conn, sql, params);
    }

    /**
     *
     */
    public final <E> E querySingle(String sql, Class<E> clazz, Object...params) throws RepositoryException {
        Connection conn = getThreadConnection();
        if (conn != null) {
            return querySingle(conn, sql, clazz, params);
        } else {
            try {
                conn = getConnection();
                return querySingle(conn, sql, clazz, params);
            } catch (SQLException e) {
                throw new RepositoryException(e);
            } finally {
                dispose(conn);
            }
        }
    }

    private <E> E querySingle(Connection conn, String sql, Class<E> clazz, Object...params) throws RepositoryException {
        QueryStatement stmt = new QueryStatement(clazz);
        return stmt.querySingle(conn, sql, params);
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

    /**
     * 
     */
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

    private class TxConnection {
        private Connection conn;
        private Stack<TxEvent> stack = new Stack<TxEvent>();

        public TxConnection(Connection conn) {
            this.conn = conn;
        }

        public Connection getConnection() {
            return conn;
        }

        public void pushEvent(TxEvent event) {
            stack.push(event);
        }
        public int popEvent() {
            stack.pop();
            return stack.size();
        }
    }
}
