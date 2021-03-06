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

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.document.DocumentServiceImpl;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
public class DocumentServiceTest {
    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/site.properties");
        ApplicationContext.loadProperties(is);

        ServiceFactory.addService(
                RepositoryService.class,
                RepositoryServiceImpl.class);
        ServiceFactory.addService(
                DocumentService.class,
                DocumentServiceImpl.class);
    }

    @Test
    public void testValidate() throws Exception {
        DocumentService service = ServiceFactory.getService(DocumentService.class);
        service.validate();
    }

    @Test
    public void testLoad() throws Exception {
        DocumentService service = ServiceFactory.getService(DocumentService.class);
        DocumentModel model = service.load("index");
        assertNotNull(model);
    }
}
