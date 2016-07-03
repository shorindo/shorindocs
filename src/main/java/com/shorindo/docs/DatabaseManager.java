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
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * 
 */
public abstract class DatabaseManager {
    private static final Logger LOG = Logger.getLogger(DatabaseManager.class);
    private static SqlSessionFactory sqlSessionFactory;

    public static void init(Properties props) {
        try {
            Properties params = new Properties();
            String engineName = "";
            for (Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
                String key = e.nextElement().toString();
                if ("datasource.driverClassName".equals(key)) {
                    params.put("driver", props.get(key));
                } else if ("datasource.url".equals(key)) {
                    params.put("url", props.get(key));
                    engineName = ((String)props.get(key)).replaceAll("^[^:]+:([^:]+):.*$", "$1");
                } else if ("datasource.username".equals(key)) {
                    params.put("username", props.get(key));
                } else if ("datasource.password".equals(key)) {
                    params.put("password", props.get(key));
                }
            }
            DataSourceFactory dataSourceFactory = new PooledDataSourceFactory();
            dataSourceFactory.setProperties(params);
            Environment env = new Environment("database",
                    new JdbcTransactionFactory(),
                    dataSourceFactory.getDataSource());
            Configuration config = new Configuration(env);
            new XMLMapperBuilder(DatabaseManager.class.getResourceAsStream("/mybatis/" + engineName + ".xml"), config, "", config.getSqlFragments()).parse();
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);

//            params = new Properties();
//            for (Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
//                String key = e.nextElement().toString();
//                if (key.matches("^datasource\\..*")) {
//                    String name = key.replaceAll("^datasource\\.(.*)$", "$1");
//                    params.put(name, props.getProperty(key));
//                }
//            }
//            dataSource = BasicDataSourceFactory.createDataSource(params);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    public static void shutdown() {
//        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
//            try {
//                DriverManager.deregisterDriver(e.nextElement());
//            } catch (SQLException ex) {
//                LOG.error(ex.getMessage(), ex);
//            }
//        }
    }
//
//    public static <T>T selectOne(Class<T> clazz, String sql, Object... params) throws SQLException {
//        RowProcessor rp = new BasicRowProcessor(new UnderscoreProcessor());
//        QueryRunner runner = new QueryRunner(dataSource);
//        ResultSetHandler<T> rsh = new BeanHandler<T>(clazz, rp);
//        return runner.query(sql, rsh, params);
//    }
//
//    public static <T> List<T> select(Class<T> clazz, String sql, Object... params) throws SQLException {
//        RowProcessor rp = new BasicRowProcessor(new UnderscoreProcessor());
//        QueryRunner runner = new QueryRunner(dataSource);
//        BeanListHandler<T> rsh = new BeanListHandler<T>(clazz, rp);
//        return (List<T>)runner.query(sql, rsh, params);
//    }

    public static <T>T transaction(Transactional<T> callback) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            T result = callback.run(session);
            session.commit();
            return result;
        } catch (Throwable th) {
            session.rollback();
            throw new RuntimeException(th);
        } finally {
            session.close();
        }
    }

    public static interface Transactional<X> {
        public X run(SqlSession session) throws SQLException;
    }

//    public static class UnderscoreProcessor extends BeanProcessor {
//        @Override
//        protected int[] mapColumnsToProperties(ResultSetMetaData rsmd,
//                PropertyDescriptor[] props) throws SQLException {
//            int cols = rsmd.getColumnCount();
//            int columnToProperty[] = new int[cols + 1];
//            Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
//            for (int col = 1; col <= cols; col++) {
//                String columnName = rsmd.getColumnName(col);
//                for (int i = 0; i < props.length; i++) {
//                    if (equalsColumnProperty(columnName, props[i].getName())) {
//                        columnToProperty[col] = i;
//                        break;
//                    }
//                }
//            }
//            return columnToProperty;
//        }
//        private boolean equalsColumnProperty(String colName, String propName) {
//            return colName.replaceAll("_", "").equalsIgnoreCase(propName);
//        }
//    }
}
