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
package com.shorindo.docs.specout;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Timestamp;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.DocumentEntity;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.Transactional;
import com.shorindo.docs.specout.SpecoutController;

/**
 * 
 */
public class SpecoutControllerTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(SpecoutControllerTest.class);
    private static DatabaseService databaseService;
    
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        ApplicationContext.loadProperties(is);
        databaseService = DatabaseService.newInstance();
    }

    @Test
    public void testSample() throws Exception {
        InputStream is = getClass()
                .getResourceAsStream("specsample.xml");
        StringBuilder body = new StringBuilder();
        Reader reader = new InputStreamReader(is, "UTF-8");
        char[] buff = new char[2048];
        int len = 0;
        while ((len = reader.read(buff)) > 0) {
            body.append(buff, 0, len);
        }
       
        int count = databaseService.provide(new Transactional<Integer>() {

            @Override
            public Integer run(Connection conn, Object... params)
                    throws DatabaseException {
                DocumentEntity entity = new DocumentEntity();
                entity.setDocumentId("specout");
                entity.setContentType(SpecoutController.class.getName());
                entity.setTitle("specout");
                entity.setBody(body.toString());
                entity.setStatus(0);
                entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
                entity.setUpdateDate(new Timestamp(System.currentTimeMillis()));
                entity.setOwnerId(getClass().getName());
                entity.setAclId("aclId");
                return put(entity);
            }
            
        });
        assertEquals(1, count);
    }
}
