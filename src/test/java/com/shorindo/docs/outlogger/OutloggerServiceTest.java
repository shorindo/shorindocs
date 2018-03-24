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
package com.shorindo.docs.outlogger;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;

/**
 * 
 */
public class OutloggerServiceTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerServiceTest.class);
    private static OutloggerService service;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/main/webapp/WEB-INF/site.properties");
        ApplicationContext.loadProperties(is);
        service = new OutloggerService();
    }

    @Test
    public void testCreateSchema() throws Exception {
        service.createSchema();
    }
}
