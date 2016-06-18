/*
 * Copyright 2016 Shorindo, Inc.
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

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * 
 */
public abstract class DatabaseManager {
    private static final Logger LOG = Logger.getLogger(DatabaseManager.class);
    private static DataSource dataSource;

    public static void init(Properties props) {
        try {
            Properties params = new Properties();
            for (Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
                String key = e.nextElement().toString();
                if (key.matches("^datasource\\..*")) {
                    String name = key.replaceAll("^datasource\\.(.*)$", "$1");
                    params.put(name, props.getProperty(key));
                }
            }
            dataSource = BasicDataSourceFactory.createDataSource(params);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static <T>T selectOne(Class<T> clazz, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<T> rsh = new BeanHandler<T>(clazz);
        return runner.query(sql, rsh, params);
    }

    public static <T> List<T> select(Class<T> clazz, String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        BeanListHandler<T> rsh = new BeanListHandler<T>(clazz);
        return (List<T>)runner.query(sql, rsh, params);
    }

//    public <T>T transaction(Transactional<T> callback) {
//        Connection conn = null;
//        try {
//            conn = dataSource.getConnection();
//            if (callback.isTransactional()) {
//                conn.setAutoCommit(false);
//            }
//            callback.setConnection(conn);
//            T result = callback.run(conn);
//            if (!conn.getAutoCommit()) {
//                conn.commit();
//            }
//            return result;
//        } catch (Throwable th) {
//            if (conn != null) {
//                try {
//                    if (!conn.getAutoCommit()) {
//                        LOG.error("transaction():ROLLBACK", th);
//                        conn.rollback();
//                    }
//                } catch (SQLException e) {
//                    LOG.error("transaction()", e);
//                }
//            }
//            throw new RuntimeException(th);
//        } finally {
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    LOG.error("transaction()", e);
//                }
//            }
//        }
//    }
//
//    public abstract static class Transactional<X> {
//        protected static final Logger LOG = Logger.getLogger(Transactional.class);
//        Connection conn;
//        public abstract X run(Connection conn) throws SQLException;
//        public boolean isTransactional() {
//            return true;
//        }
//        public final void setConnection(Connection conn) {
//            this.conn = conn;
//        }
//        public <T> List<T> queryForList(Class<T> expect, String sql, Object... args) throws SQLException {
//            LOG.debug("queryForList(" + sql + ")");
//            List<T> result = new ArrayList<T>();
//            PreparedStatement stmt = conn.prepareStatement(sql);
//            for (int i = 0; i < args.length; i++) {
//                LOG.debug("param[" + i + "]=" + args[i]);
//                if (args[i] == null) {
//                    stmt.setObject(i + 1, null);
//                } else {
//                    stmt.setObject(i + 1, args[i]);
//                }
//            }
//            ResultSet rset = stmt.executeQuery();
//            while (rset.next()) {
//                try {
//                    T bean = expect.newInstance();
//                    ResultSetMetaData meta = rset.getMetaData();
//                    for (int i = 1; i <= meta.getColumnCount(); i++) {
//                        String propertyName = column2Property(meta.getColumnName(i));
//                        setPropertyValue(bean, propertyName, rset.getObject(i));
//                    }
//                    result.add(bean);
//                } catch (InstantiationException e) {
//                    throw new SQLException(e.getMessage());
//                } catch (IllegalAccessException e) {
//                    throw new SQLException(e.getMessage());
//                }
//            }
//            LOG.debug("result=" + result);
//            return result;
//        }
//        public <T>T queryForObject(Class<T> expect, String sql, Object... args) throws SQLException {
//            LOG.debug("queryForObject(" + sql + ")");
//            List<T> list = queryForList(expect, sql, args);
//            if (list.size() > 0) {
//                return list.get(0);
//            } else {
//                return null;
//            }
//        }
//        public int executeUpdate(String sql, Object... args) throws SQLException {
//            LOG.debug("executeSQL(" + sql + ")");
//            PreparedStatement stmt = conn.prepareStatement(sql);
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] == null) {
//                    stmt.setObject(i + 1, null);
//                } else {
//                    stmt.setObject(i + 1, args[i]);
//                }
//                LOG.debug("param[" + args[i] + "]");
//            }
//            return stmt.executeUpdate();
//        }
//        private String column2Property(String columnName) {
//            String[] parts = columnName.split("_");
//            StringBuffer result = new StringBuffer();
//            result.append(parts[0].toLowerCase());
//            for (int i = 1; i < parts.length; i++) {
//                result.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase());
//            }
//            return result.toString();
//        }
//    }

}
