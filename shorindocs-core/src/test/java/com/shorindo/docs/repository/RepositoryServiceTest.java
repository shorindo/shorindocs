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

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;
import com.shorindo.docs.repository.Transactionable;

/**
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryServiceTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(RepositoryServiceTest.class);
    private static RepositoryService repositoryService;
    
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        ApplicationContext.loadProperties(is);
        
        repositoryService = RepositoryServiceFactory.repositoryService();
        repositoryService.execute("DROP TABLE IF EXISTS SAMPLE");
        repositoryService.execute("CREATE TABLE IF NOT EXISTS SAMPLE (" +
                     "    STRING_VALUE VARCHAR(100) UNIQUE," +
                     "    INT_VALUE INT," +
                     "    DOUBLE_VALUE DOUBLE," +
                     "    DATE_VALUE DATETIME," +
                     "    CONSTRAINT PRIMARY KEY (STRING_VALUE, INT_VALUE)" +
                     ")"
                );
    }

    @AfterClass
    public static void tearDownAfter() throws Exception {
        repositoryService.execute("DROP TABLE SAMPLE");
    }

    @Test
    public void testTransactional() throws Exception {
        long st = System.currentTimeMillis();
        SampleEntity result = repositoryService.transaction(new Transactionable<SampleEntity>() {
            @Override
            public SampleEntity run(Object...params) throws DatabaseException {
                repositoryService.execute("INSERT INTO SAMPLE VALUES('BAR', 123, 123.456, '1970/01/01 12:34:56')");
                List<SampleEntity> resultList = repositoryService.query("SELECT * FROM SAMPLE", SampleEntity.class);
                return resultList.get(0);
            }
        });
        LOG.debug("elapsed:" + (System.currentTimeMillis() - st) + "ms"); 
        assertEquals("BAR", result.getStringValue());
        assertEquals(123, (int)result.getIntValue());
        assertEquals(123.456, result.getDoubleValue(), 0.001);
        LOG.debug(result.getDateValue().getClass() + "=" + result.getDateValue());
    }

    private static Transactionable<Integer> TL = new Transactionable<Integer>() {
        public Integer run(Object...params) throws DatabaseException {
            return repositoryService.query("SELECT 123", int.class).get(0);
        }
    };

    @Test
    public void testTransactionless() throws Exception {
        assertEquals(123, (int)repositoryService.transaction(TL));
    }

    @Test
    public void testPut() throws Exception {
        SampleEntity e = generateSampleEntity();
        int result = repositoryService.put(e);
        assertEquals(1, result);
    }

    @Test
    public void testGet() throws Exception {
        SampleEntity expect = generateSampleEntity();
        repositoryService.put(expect);
        SampleEntity actual = repositoryService.get(expect);
        assertEquals("stringValue", actual.getStringValue());
        assertEquals(123, (int)actual.getIntValue());
    }

    @Test
    public void testRemove() throws Exception {
        SampleEntity expect = generateSampleEntity();
        repositoryService.put(expect);
        int result = repositoryService.remove(expect);
        assertEquals(1, result);
        SampleEntity actual = repositoryService.get(expect);
        assertNull(actual);
    }

//    @Test
//    public void testLoadTables() throws Exception {
//        repositoryService.loadTables();
//    }

//    @Test
//    public void testLoadSchema() throws Exception {
//        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
//        service.loadSchema(is);
//        is.close();
//    }
//
//    @Test
//    public void testValidateSchema() throws Exception {
//        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
//        try {
//            DatabaseSchema schema = service.loadSchema(is);
//            service.validateSchema(schema);
//        } finally {
//            is.close();
//        }
//    }

//    @Test
//    public void testGenerateSchemaEntity() throws Exception {
//        InputStream is = getClass().getResourceAsStream("Sample.dsdl");
//        try {
//            DatabaseSchema schema = service.loadSchema(is);
//            service.generateSchemaEntity(schema);
//        } finally {
//            is.close();
//        }
//    }

    public SampleEntity generateSampleEntity() throws DatabaseException {
        SampleEntity entity = new SampleEntity();
        entity.setStringValue("stringValue");
        entity.setByteObject(Byte.valueOf((byte)123));
        entity.setIntValue(123);
        return entity;
    }
}
