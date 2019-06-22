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
package com.shorindo.docs.auth;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.IdentityProvider;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.auth.entity.UserEntity;

/**
 * 
 */
public class AuthenticateServiceTest {
    private static AuthenticateService authenticateService;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/site.properties");
        try {
            ApplicationContext.loadProperties(is);
            authenticateService = AuthenticateServiceFactory.authenticateService();
        } finally {
            is.close();
        }
    }

    @Test
    public void testHashPassword() throws Exception {
        String hash = authenticateService.hashPassword("password");
        assertEquals(36, hash.length());
        assertTrue(authenticateService.confitmPassword("password", hash));
    }

    @Test
    public void testLogin() throws Exception {
        authenticateService.login("test", "password");
    }

    @Test
    public void testCreateUser() throws Exception {
        UserEntity entity = new UserEntity();
        entity.setLoginName(String.format("%x", IdentityProvider.newId()));
        entity.setPassword("password");
        entity.setMail("mail");
        authenticateService.createUser(entity);
        authenticateService.login(entity.getLoginName(), entity.getPassword());
    }
}
