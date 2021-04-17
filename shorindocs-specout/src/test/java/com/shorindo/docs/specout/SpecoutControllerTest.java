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
import java.sql.Timestamp;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.specout.SpecoutController;

/**
 * 
 */
public class SpecoutControllerTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(SpecoutControllerTest.class);
    private static RepositoryService repositoryService;
    
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        ApplicationContext.loadProperties(is);
        repositoryService = ApplicationContext.getBean(RepositoryService.class);
    }

    @Test
    public void testPoV() throws Exception {
        InputStream is = getClass()
                .getResourceAsStream("testpov.xml");
        StringBuilder body = new StringBuilder();
        Reader reader = new InputStreamReader(is, "UTF-8");
        char[] buff = new char[2048];
        int len = 0;
        while ((len = reader.read(buff)) > 0) {
            body.append(buff, 0, len);
        }
       
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId("testpov");
        entity.setController(SpecoutController.class.getName());
        entity.setTitle("テスト観点");
        entity.setContent(body.toString());
        entity.setCreateUser(getClass().getSimpleName());
        entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
        entity.setUpdateUser(getClass().getSimpleName());
        entity.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        entity.setOwnerId(getClass().getSimpleName());
        int count = repositoryService.put(entity);
        assertEquals(1, count);
    }

    @Test
    public void testSpecout() throws Exception {
        createData("specout.xml", "specout");
    }

    @Test
    public void testOutlogger() throws Exception {
        createData("outloggerspec.xml", "Outlogger仕様書");
    }

    private void createData(String fileName, String title) throws Exception {
        InputStream is = getClass().getResourceAsStream(fileName);
        StringBuilder body = new StringBuilder();
        Reader reader = new InputStreamReader(is, "UTF-8");
        char[] buff = new char[2048];
        int len = 0;
        while ((len = reader.read(buff)) > 0) {
            body.append(buff, 0, len);
        }
       
        String path = fileName.replaceAll("\\..*$", "");
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(path);
        entity.setController(SpecoutController.class.getName());
        entity.setTitle(title);
        entity.setContent(body.toString());
        entity.setCreateUser(getClass().getSimpleName());
        entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
        entity.setUpdateUser(getClass().getSimpleName());
        entity.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        entity.setOwnerId(getClass().getSimpleName());
        repositoryService.put(entity);
    }
}
