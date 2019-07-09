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
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.RepositoryServiceImpl;
import com.shorindo.docs.repository.ExecuteStatement.InsertStatement;
import com.shorindo.docs.repository.ExecuteStatement.UpdateStatement;
import com.shorindo.docs.repository.ExecuteStatement.DeleteStatement;

/**
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryServiceTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(RepositoryServiceTest.class);
    private static RepositoryService repositoryService;
    private static DataSource dataSource;
    
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/site.properties");
        ApplicationContext.loadProperties(is);

        ServiceFactory.addService(
                RepositoryService.class,
                RepositoryServiceImpl.class);
        repositoryService = ServiceFactory.getService(RepositoryService.class);
        repositoryService.execute(
                "DROP TABLE IF EXISTS SAMPLE");
        repositoryService.execute(
                "CREATE TABLE IF NOT EXISTS SAMPLE (" +
                "    STRING_VALUE VARCHAR(100)," +
                "    BOOLEAN_VALUE BOOLEAN NOT NULL," +
                "    BOOLEAN_OBJECT BOOLEAN," +
                "    BYTE_VALUE TINYINT NOT NULL," +
                "    BYTE_OBJECT TINYINT," +
                "    SHORT_VALUE SMALLINT NOT NULL," +
                "    SHORT_OBJECT SMALLINT," +
                "    INT_VALUE INT NOT NULL," +
                "    INT_OBJECT INT," +
                "    LONG_VALUE LONG NOT NULL," +
                "    LONG_OBJECT LONG," +
                "    FLOAT_VALUE FLOAT NOT NULL," +
                "    FLOAT_OBJECT FLOAT," +
                "    DOUBLE_VALUE DOUBLE NOT NULL," +
                "    DOUBLE_OBJECT DOUBLE," +
                "    DATE_VALUE DATETIME," +
                "    TIMESTAMP_VALUE TIMESTAMP," +
                "    CONSTRAINT PRIMARY KEY (STRING_VALUE, INT_VALUE)" +
                ")");
        
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

    @AfterClass
    public static void tearDownAfter() throws Exception {
        repositoryService.execute(
                "DROP TABLE SAMPLE");
    }

    @Test
    public void testPut() throws Exception {
        SampleEntity e = generateSampleEntity();
        try {
            int result = repositoryService.put(e);
            assertEquals(1, result);
        } finally {
            repositoryService.delete(e);
        }
    }

    @Test
    public void testGet() throws Exception {
        SampleEntity expect = generateSampleEntity();
        try {
            repositoryService.put(expect);
            SampleEntity actual = repositoryService.get(expect);
            assertEquals(expect.getStringValue(), actual.getStringValue());
            assertEquals(expect.getIntValue(), (int)actual.getIntValue());
        } finally {
            repositoryService.delete(expect);
        }
    }

    @Test
    public void testRemove() throws Exception {
        SampleEntity expect = generateSampleEntity();
        repositoryService.put(expect);
        int result = repositoryService.delete(expect);
        assertEquals(1, result);
        SampleEntity actual = repositoryService.get(expect);
        assertNull(actual);
    }

    @Test
    public void testInsertStatement() throws Exception {
        ExecuteStatement insert = new InsertStatement(SampleEntity.class);
        Connection conn = dataSource.getConnection();
        try {
            SampleEntity entity = generateSampleEntity();
            insert.execute(conn, entity);
        } finally {
            conn.close();
        }
    }

    @Test
    public void testUpdateStatement() throws Exception {
        ExecuteStatement update = new UpdateStatement(SampleEntity.class);
        Connection conn = dataSource.getConnection();
        try {
            SampleEntity entity = generateSampleEntity();
            update.execute(conn, entity);
        } finally {
            conn.close();
        }
    }

    @Test
    public void testDeleteStatement() throws Exception {
        ExecuteStatement delete = new DeleteStatement(SampleEntity.class);
        Connection conn = dataSource.getConnection();
        try {
            SampleEntity entity = generateSampleEntity();
            delete.execute(conn, entity);
        } finally {
            conn.close();
        }
    }

    @Test
    public void testSucceed() throws Exception {
        ServiceFactory.addService(TxTestService.class, TxTestServiceImpl.class);
        ServiceFactory.addService(TxNestedService.class, TxNestedServiceImpl.class);
        TxTestService txService = ServiceFactory.getService(TxTestService.class);
        SampleEntity expect = generateSampleEntity();
        txService.succeed(expect);
        SampleEntity actual = repositoryService.get(expect);
        assertNull(actual);
    }

    @Test
    public void testFailOnDelete() throws Exception {
        ServiceFactory.addService(TxTestService.class, TxTestServiceImpl.class);
        ServiceFactory.addService(TxNestedService.class, TxNestedServiceImpl.class);
        TxTestService txService = ServiceFactory.getService(TxTestService.class);
        SampleEntity expect = generateSampleEntity();
        try {
            txService.failOnDelete(expect);
            fail();
        } catch (Exception e) {
            SampleEntity actual = repositoryService.get(expect);
            assertNull(actual);
        }
    }

    @Test
    public void testFailOnUpdate() throws Exception {
        ServiceFactory.addService(TxTestService.class, TxTestServiceImpl.class);
        ServiceFactory.addService(TxNestedService.class, TxNestedServiceImpl.class);
        TxTestService txService = ServiceFactory.getService(TxTestService.class);
        SampleEntity expect = generateSampleEntity();
        try {
            txService.failOnUpdate(expect);
            fail();
        } catch (Exception e) {
            SampleEntity actual = repositoryService.get(expect);
            assertNull(actual);
        }
    }

    public static SampleEntity generateSampleEntity() throws RepositoryException {
        SampleEntity entity = new SampleEntity();
        entity.setStringValue("stringValue");
        entity.setByteObject(Byte.valueOf((byte)123));
        entity.setIntValue(new Random().nextInt());
        entity.setDateValue(new java.util.Date());
        return entity;
    }

    public static interface TxTestService {
        public void succeed(SampleEntity entity) throws Exception;
        public void failOnDelete(SampleEntity entity) throws Exception;
        public void failOnUpdate(SampleEntity entity) throws Exception;
    }

    public static class TxTestServiceImpl implements TxTestService {
        private TxNestedService nestedService =
                 ServiceFactory.getService(TxNestedService.class);

        @Override
        @Transactional
        public void succeed(SampleEntity entity) throws Exception {
            repositoryService.insert(entity);
            entity.setBooleanValue(true);
            repositoryService.update(entity);
            repositoryService.delete(entity);
            nestedService.nested(entity);
        }

        @Override
        @Transactional
        public void failOnDelete(SampleEntity entity) throws Exception {
            repositoryService.insert(entity);
            entity.setBooleanValue(true);
            repositoryService.update(entity);
            repositoryService.delete(null);
        }

        @Override
        @Transactional
        public void failOnUpdate(SampleEntity entity) throws Exception {
            repositoryService.insert(entity);
            entity.setBooleanValue(true);
            repositoryService.update(null);
            repositoryService.delete(null);
        }
        
    }

    public static interface TxNestedService {
        public void nested(SampleEntity entity) throws Exception;
    }

    public static class TxNestedServiceImpl implements TxNestedService {

        @Override
        @Transactional
        public void nested(SampleEntity entity) throws Exception {
            repositoryService.get(entity);
        }
        
    }
}
