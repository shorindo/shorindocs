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
package com.shorindo.core;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.datasource.DataSourceFactory;
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
            Pattern p = Pattern.compile("^datasource\\.(.*)$");
            for (Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
                String key = e.nextElement().toString();
                Matcher m = p.matcher(key);
                if (m.matches()) {
                    params.put(m.group(1), props.get(key));
                    if ("url".equals(m.group(1))) {
                        engineName = ((String)props.get(key)).replaceAll("^[^:]+:([^:]+):.*$", "$1");
                    }
                }
            }
            DataSourceFactory dataSourceFactory = new DbcpDataSourceFactory();
            dataSourceFactory.setProperties(params);
            Environment env = new Environment("database",
                    new JdbcTransactionFactory(),
                    dataSourceFactory.getDataSource());
            Configuration config = new Configuration(env);
            new XMLMapperBuilder(DatabaseManager.class.getResourceAsStream("/mybatis/" + engineName + ".xml"), config, "", config.getSqlFragments()).parse();
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static <T>T selectOne(String id, Object arg) {
        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            return session.selectOne(id, arg);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        } finally {
            session.close();
        }
    }

    public static <T> List<T> selectList(String id, Object arg) {
        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            return session.selectList(id, arg);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        } finally {
            session.close();
        }
    }

    public static int insert(String id, Object arg) {
        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            return session.insert(id, arg);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        } finally {
            session.close();
        }
    }

    public static int update(String id, Object arg) {
        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            return session.update(id, arg);
        } catch (Throwable th) {
            throw new RuntimeException(th);
        } finally {
            session.close();
        }
    }

    public static <T>T transaction(Transactional<T> callback) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            T result = callback.execute(session);
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
        public X execute(SqlSession session) throws SQLException;
    }

    public static class DbcpDataSourceFactory implements DataSourceFactory {

        private DataSource datasource = null;

        //@Override
        public DataSource getDataSource() {
            return datasource;
        }

        //@Override
        public void setProperties(final Properties property) {
            try {
                datasource = BasicDataSourceFactory.createDataSource(property);
            } catch (Exception e) {
                throw new DataSourceException("DBCPの設定に失敗しました。", e);
            }
        }
    }
}
