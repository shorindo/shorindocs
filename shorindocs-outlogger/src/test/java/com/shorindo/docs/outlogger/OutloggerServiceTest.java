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
import com.shorindo.docs.IdentityManager;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.document.DocumentServiceImpl;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
public class OutloggerServiceTest {
    public static final String DOCUMENT_ID = "outlogger";
    private static OutloggerService outloggerService;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        //InputStream is = OutloggerServiceTest.class.getClassLoader().getResourceAsStream("site.properties");
        //ApplicationContext.load(is);
        ApplicationContext.addBean(RepositoryService.class, RepositoryServiceImpl.class);
        ApplicationContext.addBean(DocumentService.class, DocumentServiceImpl.class);
        ApplicationContext.addBean(OutloggerService.class, OutloggerServiceImpl.class);
        outloggerService = ApplicationContext.getBean(OutloggerService.class);
    }

    @Test
    public void testCreateMeta() throws Exception {
        DocumentEntity entity = outloggerService.newDocument();
        entity.setDocumentId(Long.toString(IdentityManager.newId()));
        entity.setController(OutloggerController.class.getName());
        entity.setTitle("test outlogger");
        entity.setContent("");
        entity.setOwnerId("outlogger");
        entity.setCreateUser("outlogger");
        entity.setCreateDate(new Date());
        entity.setUpdateUser("outlogger");
        entity.setUpdateDate(new Date());
        outloggerService.save(entity);
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
        outloggerService.putLog(entity);
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
        entity = outloggerService.putLog(entity);

        entity.setContent("putLog[2]");
        entity = outloggerService.putLog(entity);

        entity.setContent("putLog[3]");
        entity = outloggerService.putLog(entity);
    }

    @Test
    public void testListLog() throws Exception {
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(DOCUMENT_ID);
        List<OutloggerEntity> entityList = outloggerService.listLog(entity);
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
        entity = outloggerService.putLog(entity);

        outloggerService.removeLog(entity);
        assertNull(outloggerService.getLog(entity));
    }

//    private OutloggerMetaData newOutlogger() {
//        OutloggerMetaData meta = new OutloggerMetaData();
//        List<Column> columnList = new ArrayList<Column>();
//        Column column = new Column();
//        column.setTitle("test outlogger");
//        columnList.add(column);
//        return meta;
//    }
}
