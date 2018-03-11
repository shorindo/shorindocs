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
package com.shorindo.database;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.auth.AuthenticateController;
import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Transactional;
import com.shorindo.docs.database.Transactionless;

/**
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseServiceTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseServiceTest.class);
    private static DatabaseService service;
    
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        ApplicationContext.loadProperties(is);
        
        service = DatabaseService.newInstance();
        service.provide(new Transactionless<Integer>() {
            @Override
            public Integer run(Connection conn, Object...params) throws SQLException {
                exec("DROP TABLE IF EXISTS SAMPLE");
                exec("CREATE TABLE IF NOT EXISTS SAMPLE (" +
                     "    STRING_VALUE VARCHAR(100) UNIQUE," +
                     "    INT_VALUE INT," +
                     "    DOUBLE_VALUE DOUBLE," +
                     "    DATE_VALUE DATETIME," +
                     "    CONSTRAINT PRIMARY KEY (STRING_VALUE, INT_VALUE)" +
                     ")"
                        );
                return 0;
            }
        });
    }

    @AfterClass
    public static void tearDownAfter() throws Exception {
                service.provide(new Transactionless<Integer>() {
            @Override
            public Integer run(Connection conn, Object...params) throws SQLException {
                exec("DROP TABLE SAMPLE");
                return 0;
            }
        });
    }

    @Test
    public void testTransactional() throws Exception {
        long st = System.currentTimeMillis();
        SampleEntity result = service.provide(new Transactional<SampleEntity>() {
            @Override
            public SampleEntity run(Connection conn, Object...params) throws SQLException {
                exec("INSERT INTO SAMPLE VALUES('BAR', 123, 123.456, '1970/01/01 12:34:56')");
                List<SampleEntity> resultList = query("SELECT * FROM SAMPLE", SampleEntity.class);
                return resultList.get(0);
            }
        });
        LOG.info("elapsed:" + (System.currentTimeMillis() - st) + "ms"); 
        assertEquals("BAR", result.getStringValue());
        assertEquals(123, result.getIntValue());
        assertEquals(123.456, result.getDoubleValue(), 0.001);
        LOG.debug(result.getDateValue().getClass() + "=" + result.getDateValue());
    }

    private static Transactionless<Integer> TL = new Transactionless<Integer>() {
        @Override
        public Integer run(Connection conn, Object...params) throws SQLException {
            return query("SELECT 123", int.class).get(0);
        }
    };

    @Test
    public void testTransactionless() throws Exception {
        assertEquals(123, (int)service.provide(TL));
    }

    @Test
    public void testPut() throws Exception {
        int result = service.provide(new Transactional<Integer>() {
            @Override
            public Integer run(Connection conn, Object...params) throws SQLException {
                SampleEntity e = generateSampleEntity();
                return put(e); 
            }
        });
        assertEquals(1, result);
    }

    @Test
    public void testGet() throws Exception {
        SampleEntity e = service.provide(new Transactional<SampleEntity>() {
            @Override
            public SampleEntity run(Connection conn, Object...params) throws SQLException {
                SampleEntity e = generateSampleEntity();
                put(e);
                return get(e);
            }
        });
        assertEquals("stringValue", e.getStringValue());
        assertEquals(123, e.getIntValue());
    }

    @Test
    public void testRemove() throws Exception {
        SampleEntity e = service.provide(new Transactional<SampleEntity>() {
            @Override
            public SampleEntity run(Connection conn, Object...params) throws SQLException {
                SampleEntity e = generateSampleEntity();
                put(e);
                assertNotNull(get(e));
                remove(e);
                return get(e);
            }
        });
        assertNull(e);
    }

    @Test
    public void testLoadTables() throws Exception {
        service.loadTables();
    }

    @Test
    public void testLoadSchema() throws Exception {
        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
        service.loadSchema(is);
        is.close();
    }

    @Test
    public void testValidateSchema() throws Exception {
        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
        service.loadSchema(is);
        is.close();
        service.validateSchema();
    }

    public SampleEntity generateSampleEntity() {
        SampleEntity entity = new SampleEntity();
        entity.setStringValue("stringValue");
        entity.setIntValue(123);
        return entity;
    }

    public static class SampleEntity extends SchemaEntity {
        @Column("STRING_VALUE")
        private String stringValue;
        @Column("SHORT_VALUE")
        private short shortValue;
        @Column("INT_VALUE")
        private int intValue;
        @Column("LONG_VALUE")
        private long longValue;
        @Column("FLOAT_VALUE")
        private float floatValue;
        @Column("DOUBLE_VALUE")
        private double doubleValue;
        @Column("DATE_VALUE")
        private Date dateValue;

        @Override
        public String getEntityName() {
            return "SAMPLE";
        }
        public String getStringValue() {
            return stringValue;
        }
        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
        public short getShortValue() {
            return shortValue;
        }
        public void setShortValue(short shortValue) {
            this.shortValue = shortValue;
        }
        public int getIntValue() {
            return intValue;
        }
        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
        public long getLongValue() {
            return longValue;
        }
        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }
        public float getFloatValue() {
            return floatValue;
        }
        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }
        public double getDoubleValue() {
            return doubleValue;
        }
        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }
        public Date getDateValue() {
            return dateValue;
        }
        public void setDateValue(Date dateValue) {
            this.dateValue = dateValue;
        }
    }
}
