/*
 * Copyright 2015 Shorindo, Inc.
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
package com.shorindo.docs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * 
 */
public class DatabaseService {
    private static final Logger LOG = Logger.getLogger(DatabaseService.class);
    private static DataSource dataSource;

    public <T>T transaction(Transactional<T> callback) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if (callback.isTransactional()) {
                conn.setAutoCommit(false);
            }
            callback.setConnection(conn);
            T result = callback.run(conn);
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return result;
        } catch (Throwable th) {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        LOG.error("transaction():ROLLBACK", th);
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    LOG.error("transaction()", e);
                }
            }
            throw new RuntimeException(th);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error("transaction()", e);
                }
            }
        }
    }

    public abstract static class Transactional<X> {
        protected static final Logger LOG = Logger.getLogger(Transactional.class);
        Connection conn;
        public abstract X run(Connection conn) throws SQLException;
        public boolean isTransactional() {
            return true;
        }
        public final void setConnection(Connection conn) {
            this.conn = conn;
        }
        public <T> List<T> queryForList(Class<T> expect, String sql, Object... args) throws SQLException {
            LOG.debug("queryForList(" + sql + ")");
            List<T> result = new ArrayList<T>();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                LOG.debug("param[" + i + "]=" + args[i]);
                if (args[i] == null) {
                    stmt.setObject(i + 1, null);
                } else {
                    stmt.setObject(i + 1, args[i]);
                }
            }
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                try {
                    T bean = expect.newInstance();
                    ResultSetMetaData meta = rset.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String propertyName = column2Property(meta.getColumnName(i));
                        setPropertyValue(bean, propertyName, rset.getObject(i));
                    }
                    result.add(bean);
                } catch (InstantiationException e) {
                    throw new SQLException(e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new SQLException(e.getMessage());
                }
            }
            LOG.debug("result=" + result);
            return result;
        }
        public <T>T queryForObject(Class<T> expect, String sql, Object... args) throws SQLException {
            LOG.debug("queryForObject(" + sql + ")");
            List<T> list = queryForList(expect, sql, args);
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        }
        public int executeUpdate(String sql, Object... args) throws SQLException {
            LOG.debug("executeSQL(" + sql + ")");
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    stmt.setObject(i + 1, null);
                } else {
                    stmt.setObject(i + 1, args[i]);
                }
                LOG.debug("param[" + args[i] + "]");
            }
            return stmt.executeUpdate();
        }
        private String column2Property(String columnName) {
            String[] parts = columnName.split("_");
            StringBuffer result = new StringBuffer();
            result.append(parts[0].toLowerCase());
            for (int i = 1; i < parts.length; i++) {
                result.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
            }
            return result.toString();
        }
    }
    
    public static Object setPropertyValue(Object o, String propertyName, Object value) {
        //LOG.debug("setPropertyValue(" + o + ", " + propertyName + ", " + value + ")");
        if (o == null) {
            return null;
        }
        try {
            String setterName = getSetterName(propertyName);
            Method[] methods = o.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (!methods[i].getName().equals(setterName)) {
                    continue;
                }
                Class<?> types[] = methods[i].getParameterTypes();
                if (types == null || types.length != 1)
                    continue;
                Class<?> type = types[0];
                if (type == boolean.class)
                    type = Boolean.class;
                if (type == int.class)
                    type = Integer.class;
                else if (type == long.class)
                    type = Long.class;
                else if (type == float.class)
                    type = Float.class;
                else if (type == double.class)
                    type = Double.class;
                //LOG.debug("expect type=" + type + " via " + (value == null ? "null" : value.getClass()));
                Method method = methods[i];
                if (value == null ? true : type == value.getClass()) {
                    //TODO primitive型にnullはセットできない...
                    return method.invoke(o, new Object[]{ value });
                } else if (type == Integer.class && value.getClass() == Long.class) { //FIXME
                    return method.invoke(o, new Object[]{ (new Long((Long)value)).intValue() });
                } else if (type.isAssignableFrom(value.getClass())) {
                    return method.invoke(o, new Object[]{ value });
                }
            }
            LOG.warn("setPropertyValue():" + propertyName + " is unknown.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }
    public static String getGetterName(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
    public static String getSetterName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
}
