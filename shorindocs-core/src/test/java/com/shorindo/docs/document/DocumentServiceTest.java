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
package com.shorindo.docs.document;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.model.GroupModel;
import com.shorindo.docs.model.UserModel;

/**
 * 
 */
@Ignore
public class DocumentServiceTest {
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/resources/application-config.xml");
        ApplicationContext.init(is);
//        ApplicationContext.addBean(
//                RepositoryService.class,
//                RepositoryServiceImpl.class);
//        ApplicationContext.addBean(
//                DocumentService.class,
//                DocumentServiceImpl.class);
    }

    @Test
    public void testValidate() throws Exception {
        DocumentService service = ApplicationContext.getBean(DocumentService.class);
        service.validate();
    }

    @Test
    public void testLoad() throws Exception {
        DocumentService service = ApplicationContext.getBean(DocumentService.class);
        DocumentModel model = service.load("index");
        assertNotNull(model);
    }

    @Test
    public void testCreate() throws Exception {
        AuthenticateService authenticateService = ApplicationContext.getBean(AuthenticateService.class);
        authenticateService.setUser(createUser());
        DocumentService service = ApplicationContext.getBean(DocumentService.class);
        DocumentEntity entity = new DocumentEntity();
        entity.setDocType("markdown");
        entity.setTitle("test");
        DocumentModel model = service.create(entity);
        assertNotNull(model);
    }

    private UserModel createUser() {
        return new UserModel() {
            @Override
            public String getId() {
                return getUserId();
            }
            @Override
            public String getUserId() {
                return Thread.currentThread().getName();
            }
            @Override
            public String getLoginName() {
                return null;
            }
            @Override
            public String getPassword() {
                return null;
            }
            @Override
            public String getDisplayName() {
                return null;
            }
            @Override
            public String getMail() {
                return null;
            }
            @Override
            public List<GroupModel> getGroupList() {
                return null;
            }
            @Override
            public boolean isAuthenticated() {
                return false;
            }
        };
    }
}
