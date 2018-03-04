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
import org.junit.Test;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.SystemContext;
import com.shorindo.docs.auth.AuthenticateController;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Transactional;
import com.shorindo.docs.database.Transactionless;

/**
 * 
 */
public class DatabaseServiveTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(DatabaseServiveTest.class);

    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        SystemContext.loadProperties(is);
        
        DatabaseService service = DatabaseService.newInstance();
        service.provide(new Transactionless<Integer>() {
            @Override
            public Integer run(Connection conn) throws SQLException {
                execute("DROP TABLE IF EXISTS FOO");
                execute("CREATE TABLE IF NOT EXISTS FOO(" +
                        "    STRING_VALUE VARCHAR(100) UNIQUE," +
                        "    INT_VALUE INT," +
                        "    DOUBLE_VALUE DOUBLE," +
                        "    DATE_VALUE DATETIME" +
                        ")"
                        );
                return 0;
            }
        });
    }

    @AfterClass
    public static void tearDownAfter() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        service.provide(new Transactionless<Integer>() {
            @Override
            public Integer run(Connection conn) throws SQLException {
                execute("DROP TABLE FOO");
                return 0;
            }
        });
    }

    @Test
    public void testTransactional() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        long st = System.currentTimeMillis();
        SampleEntity result = service.provide(new Transactional<SampleEntity>() {
            @Override
            public SampleEntity run(Connection conn) throws SQLException {
                execute("INSERT INTO FOO VALUES('BAR', 123, 123.456, '1970/01/01 12:34:56')");
                List<SampleEntity> result = query("SELECT * FROM FOO", SampleEntity.class);
                return result.get(0);
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
        public Integer run(Connection conn) throws SQLException {
            return query("SELECT 123", int.class).get(0);
        }
    };

    @Test
    public void testTransactionless() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        assertEquals(123, (int)service.provide(TL));
    }

    @Test
    public void testShortcut() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        service.provide(new Transactional<Boolean>() {
            @Override
            public Boolean run(Connection conn) throws SQLException {
                SampleEntity e = new SampleEntity();
                insert(e);
                select(e);
                update(e);
                select(e);
                delete(e);
                select(e);
                return false;
            }
        });
    }

    @Test
    public void testLoadTables() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        service.loadTables();
    }

    @Test
    public void testLoadSchema() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
        service.loadSchema(is);
        is.close();
    }

    @Test
    public void testValidateSchema() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        InputStream is = AuthenticateController.class.getResourceAsStream("AuthenticateService.dsdl");
        service.loadSchema(is);
        is.close();
        service.validateSchema();
    }

    public static class SampleEntity extends SchemaEntity {
        private String stringValue;
        private short shortValue;
        private int intValue;
        private long longValue;
        private float floatValue;
        private double doubleValue;
        private Date dateValue;

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
