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

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.SystemContext;
import com.shorindo.docs.database.DatabaseService;
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
    }

    private static Transactional<Integer> TT = new Transactional<Integer>() {
        @Override
        public Integer run(Connection conn) throws SQLException {
            return execute(conn, "INSERT INTO FOO VALUES(1");
        }
    };

    @Test
    public void testTransactional() {
        DatabaseService service = DatabaseService.newInstance();
        service.provide(new Transactional<String>() {
            @Override
            public String run(Connection conn) throws SQLException {
                execute(conn, "CREATE TABLE IF NOT EXISTS FOO(BAR INTEGER UNIQUE)");
                execute(conn, "INSERT INTO FOO VALUES(123)");
                query  (conn, "SELECT * FROM FOO", String.class);
                execute(conn, "DROP TABLE FOO");
                return "fin";
            }
        });
    }

    @Test
    public void testTransactionless() {
        DatabaseService service = DatabaseService.newInstance();
        service.provide(new Transactionless<String>() {
            @Override
            public String run(Connection conn) {
                return null;
            }
        });
    }

    @Test
    public void testLoadTables() throws Exception {
        DatabaseService service = DatabaseService.newInstance();
        service.loadTables();
    }
}
