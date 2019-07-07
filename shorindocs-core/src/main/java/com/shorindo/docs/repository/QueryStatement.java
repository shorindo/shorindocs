/*
 * Copyright 2019 Shorindo, Inc.
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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.BeanUtil.BeanNotFoundException;
import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public abstract class QueryStatement extends RepositoryStatement {
    private static ActionLogger LOG =
            ActionLogger.getLogger(QueryStatement.class);

    public QueryStatement(Class<?> clazz) throws RepositoryException {
        super(clazz);
    }

    public abstract <T> List<T> queryList(Connection conn, String sql, Object...params) throws RepositoryException;
    public abstract <T> T querySingle(Connection conn, String sql, Object...params) throws RepositoryException;
    public abstract <T> T get(Connection conn, T entity) throws RepositoryException;
    
    public static class SelectStatement extends QueryStatement {

        public SelectStatement(Class<?> clazz) throws RepositoryException {
            super(clazz);
            List<String> valueList = new ArrayList<String>();
            List<String> keyList = new ArrayList<String>();
            for (Entry<Field,ColumnMapper> entry : getColumnMap().entrySet()) {
                Column column = entry.getValue().getColumn();
                valueList.add(column.name());
                if (column.primaryKey() > 0) {
                    keyList.add(column.name());
                }
            }
            if (keyList.size() == 0) {
                throw new RepositoryException(DBMS_5130, clazz.getSimpleName());
            }
            StringBuffer sb = new StringBuffer("SELECT ");
            String sep = "";
            for (String value : valueList) {
                sb.append(sep + value);
                sep = ", ";
            }
            sb.append(" FROM " + getTableName());
            sb.append(" WHERE ");
            sep = "";
            for (String key : keyList) {
                sb.append(sep + key + "=?");
                sep = " AND ";
            }
            setSql(sb.toString());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(Connection conn, T entity) throws RepositoryException {
            LOG.debug(DBMS_0003, getSql());
            long st = System.currentTimeMillis();
            ResultSet rset = null;
            try (PreparedStatement stmt = conn.prepareStatement(getSql())) {
                int index = 1;
                List<Object> paramList = new ArrayList<Object>();
                for (Entry<Field,ColumnMapper> entry : getColumnMap().entrySet()) {
                    String fieldName = entry.getKey().getName();
                    Column column = entry.getValue().getColumn();
                    if (column.primaryKey() > 0) {
                        Object key = BeanUtil.getProperty(entity, fieldName);
                        setPlaceHolder(stmt, index++, key);
                        paramList.add(key);
                    }
                }
                LOG.debug(DBMS_0011, paramList);
                rset = stmt.executeQuery();
                if (rset.next()) {
                    return (T)getResult(rset, entity.getClass());
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {
                dispose(rset);
                LOG.debug(DBMS_0004, (System.currentTimeMillis() - st) + " ms");
            }
        }

        @Override
        public <T> T querySingle(Connection conn, String sql, Object... params)
                throws RepositoryException {
            return null;
        }

        @Override
        public <T> List<T> queryList(Connection conn, String sql,
                Object... params) throws RepositoryException {
            // TODO Auto-generated method stub
            return null;
        }

        private Set<String> getResultSetMetaData(ResultSet rset) throws SQLException {
            Set<String> columnSet = new HashSet<String>();
            ResultSetMetaData meta = rset.getMetaData();
            for (int index = 1; index < meta.getColumnCount(); index++) {
                columnSet.add(meta.getColumnName(index));
            }
            return columnSet;
        }

        private <T> T getResult(ResultSet rset, Class<T> clazz) throws SQLException, BeanNotFoundException, InstantiationException, IllegalAccessException {
            Set<String> columnSet = getResultSetMetaData(rset);
            T entity = clazz.newInstance();
            for (String columnName : columnSet) {
                ColumnMapper mapper = getColumnByName(columnName);
                switch (mapper.getFieldType()) {
                case BOOLEAN:
                case BOOLEAN_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getBoolean(columnName));
                    break;
                case BYTE:
                case BYTE_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getByte(columnName));
                    break;
                case SHORT:
                case SHORT_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getShort(columnName));
                    break;
                case INTEGER:
                case INTEGER_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getInt(columnName));
                    break;
                case LONG:
                case LONG_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getLong(columnName));
                    break;
                case FLOAT:
                case FLOAT_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getFloat(columnName));
                    break;
                case DOUBLE:
                case DOUBLE_OBJECT:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getDouble(columnName));
                    break;
                case STRING:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            rset.getString(columnName));
                    break;
                case DATE:
                    BeanUtil.setProperty(entity,
                            mapper.getField().getName(),
                            new Date(rset.getDate(columnName).getTime()));
                    break;
                default:
                    LOG.warn(DBMS_5131, columnName);
                }
            }
            return entity;
        }
    }
}
