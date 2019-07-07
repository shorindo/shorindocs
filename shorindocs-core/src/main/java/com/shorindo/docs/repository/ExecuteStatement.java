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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public abstract class ExecuteStatement extends RepositoryStatement {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(ExecuteStatement.class);

    protected String sql;

    public abstract int execute(Connection conn, Object entity) throws RepositoryException;

    public ExecuteStatement(Class<?> clazz) throws RepositoryException {
        super(clazz);
    }

    public int execute(Connection conn, String sql, Object...params) throws RepositoryException {
        LOG.debug(DBMS_0001, sql);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            List<Object> paramList = new ArrayList<Object>();
            for (Object param : params) {
                setPlaceHolder(stmt, index++, param);
                paramList.add(param);
            }
            LOG.debug(DBMS_0011, paramList);
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public static class InsertStatement extends ExecuteStatement {
        public InsertStatement(Class<?> clazz) throws RepositoryException {
            super(clazz);
            StringBuffer nameList = new StringBuffer();
            StringBuffer holderList = new StringBuffer();
            String sep = "";
            for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                nameList.append(sep + entry.getValue().getColumn().name());
                holderList.append(sep + "?");
                sep = ", ";
            }
            StringBuffer sb = new StringBuffer("INSERT INTO ");
            sb.append(tableName + "(");
            sb.append(nameList);
            sb.append(") VALUES (");
            sb.append(holderList);
            sb.append(")");
            sql = sb.toString();
        }

        @Override
        public int execute(Connection conn, Object entity) throws RepositoryException {
            LOG.debug(DBMS_0007, sql);
            long st = System.currentTimeMillis();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int index = 1;
                List<Object> paramList = new ArrayList<Object>();
                for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                    String fieldName = entry.getKey().getName();
                    Object param = BeanUtil.getProperty(entity, fieldName);
                    setPlaceHolder(stmt, index++, param);
                    paramList.add(param);
                }
                LOG.debug(DBMS_0011, paramList);
                return stmt.executeUpdate();
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {
                LOG.debug(DBMS_0008, (System.currentTimeMillis() - st) + "ms");
            }
        }

    }

    public static class UpdateStatement extends ExecuteStatement {
        public UpdateStatement(Class<?> clazz) throws RepositoryException {
            super(clazz);
            List<String> valueList = new ArrayList<String>();
            List<String> keyList = new ArrayList<String>();
            for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                Column column = entry.getValue().getColumn();
                if (column.primaryKey() > 0) {
                    keyList.add(column.name());
                } else {
                    valueList.add(column.name());
                }
            }
            if (keyList.size() == 0) {
                throw new RepositoryException(DBMS_5130, clazz.getSimpleName());
            }
            StringBuffer sb = new StringBuffer("UPDATE ");
            sb.append(tableName + " SET ");
            String sep = "";
            for (String value : valueList) {
                sb.append(sep + value + "=?");
                sep = ", ";
            }
            sb.append(" WHERE ");
            sep = "";
            for (String key : keyList) {
                sb.append(sep + key + "=?");
                sep = " AND ";
            }
            sql = sb.toString();
        }

        @Override
        public int execute(Connection conn, Object entity)
                throws RepositoryException {
            LOG.debug(DBMS_0009, sql);
            long st = System.currentTimeMillis();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int index = 1;
                List<Object> paramList = new ArrayList<Object>();
                for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                    String fieldName = entry.getKey().getName();
                    Column column = entry.getValue().getColumn();
                    if (column.primaryKey() == 0) {
                        Object param = BeanUtil.getProperty(entity, fieldName);
                        setPlaceHolder(stmt, index++, param);
                        paramList.add(param);
                    }
                }
                for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                    String fieldName = entry.getKey().getName();
                    Column column = entry.getValue().getColumn();
                    if (column.primaryKey() > 0) {
                        Object param = BeanUtil.getProperty(entity, fieldName);
                        setPlaceHolder(stmt, index++, param);
                        paramList.add(param);
                    }
                }
                LOG.debug(DBMS_0011, paramList);
                return stmt.executeUpdate();
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {
                LOG.debug(DBMS_0010, (System.currentTimeMillis() - st) + "ms");
            }
        }

    }

    public static class DeleteStatement extends ExecuteStatement {
        public DeleteStatement(Class<?> clazz) throws RepositoryException {
            super(clazz);
            List<String> keyList = new ArrayList<String>();
            for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                Column column = entry.getValue().getColumn();
                if (column.primaryKey() > 0) {
                    keyList.add(column.name());
                }
            }
            if (keyList.size() == 0) {
                throw new RepositoryException(DBMS_5130, clazz.getSimpleName());
            }
            StringBuffer sb = new StringBuffer("DELETE FROM ");
            sb.append(tableName + " WHERE ");
            String sep = "";
            for (String key : keyList) {
                sb.append(sep + key + "=?");
                sep = " AND ";
            }
            sql = sb.toString();
        }

        @Override
        public int execute(Connection conn, Object entity)
                throws RepositoryException {
            LOG.debug(DBMS_0005, sql);
            long st = System.currentTimeMillis();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int index = 1;
                List<Object> paramList = new ArrayList<Object>();
                for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                    String fieldName = entry.getKey().getName();
                    Column column = entry.getValue().getColumn();
                    if (column.primaryKey() > 0) {
                        Object param = BeanUtil.getProperty(entity, fieldName);
                        setPlaceHolder(stmt, index++, param);
                        paramList.add(param);
                    }
                }
                LOG.debug(DBMS_0011, paramList);
                return stmt.executeUpdate();
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {
                LOG.debug(DBMS_0006, (System.currentTimeMillis() - st) + "ms");
            }
        }

    }

}
