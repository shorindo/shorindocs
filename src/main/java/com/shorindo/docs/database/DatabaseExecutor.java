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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocsMessages;

/**
 * 
 */
public abstract class DatabaseExecutor<T> {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseExecutor.class);
    private static ThreadLocal<Connection> local = new ThreadLocal<Connection>();

    public abstract T run(Connection conn) throws SQLException;
    public abstract void beginTransaction(Connection conn) throws SQLException;
    public abstract void commitTransaction(Connection conn) throws SQLException;
    public abstract void rollbackTransaction(Connection conn) throws SQLException;

    protected void setConnection(Connection conn) {
        local.set(conn);
    }

    protected void removeConnection() {
        local.remove();
    }

    protected final int execute(String sql, Object...params) throws SQLException {
        Connection conn = local.get();
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeUpdate(sql);
    }

    protected final <E> List<E> query(String sql, Class<E> clazz, Object...params) throws SQLException {
        List<E> resultList = new ArrayList<E>();
        Connection conn = local.get();
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (Object param : params) {
            
        }
        ResultSet rset = stmt.executeQuery();
        try {
            ResultSetMapper[] mappers = null;
            while (rset.next()) {
                mappers = generateMappers(mappers, rset.getMetaData(), clazz);
                E bean = applyMappers(mappers, rset, clazz);
                resultList.add(bean);
            }
        } catch (InstantiationException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        } catch (InvocationTargetException e) {
            throw new SQLException(e);
        } finally {
            closeStatement(stmt);
            closeResultSet(rset);
        }
        return resultList;
    }

    protected final <E extends SchemaEntity> E select(E entity) throws SQLException {
        return null;
    }

    protected final int insert(SchemaEntity entity) throws SQLException {
        return 0;
    }
    
    protected final int update(SchemaEntity entity) throws SQLException {
        return 0;
    }

    protected final int delete(SchemaEntity entity) throws SQLException {
        return 0;
    }

    protected ResultSetMapper[] generateMappers(ResultSetMapper[] mappers, ResultSetMetaData meta, Class<?> target) throws SQLException {
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

    private void closeStatement(Statement stmt) {
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error(DocsMessages.E_5106, e);
            }
    }

    private void closeResultSet(ResultSet rset) {
        if (rset != null)
            try {
                rset.close();
            } catch (SQLException e) {
                LOG.error(DocsMessages.E_5107, e);
            }
    }

    protected static class ResultSetMapper {
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
