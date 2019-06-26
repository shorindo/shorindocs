/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.docs.outlogger;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
public class OutloggerServiceTest {
    public static final String DOCUMENT_ID = "outlogger";
    private static OutloggerService service;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = OutloggerServiceTest.class.getClassLoader().getResourceAsStream("site.properties");
        ApplicationContext.loadProperties(is);
        ServiceFactory.addService(RepositoryService.class, RepositoryServiceImpl.class);
        ServiceFactory.addService(OutloggerService.class, OutloggerServiceImpl.class);
        service = ServiceFactory.getService(OutloggerService.class);
    }

    @Test
    public void testAddLog() throws Exception {
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(DOCUMENT_ID);
        entity.setLogId(1);
        entity.setContent("putLog " + new Date());
        entity.setCreateUser("testuser");
        entity.setCreateDate(new Date());
        entity.setUpdateUser("testuser");
        entity.setUpdateDate(new Date());
        service.putLog(entity);
    }

    @Test
    public void testModLog() throws Exception {
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(DOCUMENT_ID);
        entity.setContent("putLog[1]");
        entity.setCreateUser("testuser");
        entity.setCreateDate(new Date());
        entity.setUpdateUser("testuser");
        entity.setUpdateDate(new Date());
        entity = service.putLog(entity);

        entity.setContent("putLog[2]");
        entity = service.putLog(entity);

        entity.setContent("putLog[3]");
        entity = service.putLog(entity);
    }

    @Test
    public void testListLog() throws Exception {
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(DOCUMENT_ID);
        List<OutloggerEntity> entityList = service.listLog(entity);
        assertTrue(entityList.size() > 0);
        entityList.stream().forEach(e -> {
            System.out.println(e);
        });
    }

    @Test
    public void testRemoveLog() throws Exception {
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(DOCUMENT_ID);
        entity.setContent("removeLog " + new Date());
        entity.setCreateUser("testuser");
        entity.setCreateDate(new Date());
        entity.setUpdateUser("testuser");
        entity.setUpdateDate(new Date());
        entity = service.putLog(entity);

        service.removeLog(entity);
        assertNull(service.getLog(entity));
    }
}
