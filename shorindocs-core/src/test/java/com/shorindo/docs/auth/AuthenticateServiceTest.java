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

import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.IdentityManager;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.model.SessionModel;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
@Ignore
public class AuthenticateServiceTest {
    private static AuthenticateService authenticateService;

    @BeforeClass
    public static void setUpBefore() throws Exception {
        InputStream is = AuthenticateServiceTest.class
                .getClassLoader()
                .getResourceAsStream("site.properties");
        try {
//            ApplicationContext.load(is);
            ApplicationContext.addBean(RepositoryService.class, RepositoryServiceImpl.class);
            ApplicationContext.addBean(AuthenticateService.class, AuthenticateServiceImpl.class);
            authenticateService = ApplicationContext.getBean(AuthenticateService.class);
        } finally {
            is.close();
        }
    }

    @Test
    public void testCreateUser() throws Exception {
        UserEntity expect = generateUser();
        UserModel user = authenticateService.createUser(expect);
        assertEquals(expect.getLoginName(), user.getLoginName());
    }

    @Test
    public void testLogin() throws Exception {
        UserEntity expect = generateUser();
        UserModel user = authenticateService.createUser(expect);
        assertEquals(expect.getLoginName(), user.getLoginName());

        SessionModel session = authenticateService.login(expect.getLoginName(), expect.getPassword());
        assertEquals(expect.getLoginName(), session.getUser().getLoginName());
    }

    @Test
    public void testAuthenticate() throws Exception {
        UserEntity expect = generateUser();
        authenticateService.createUser(expect);

        SessionModel session = authenticateService.login(expect.getLoginName(), expect.getPassword());
        UserModel actual = authenticateService.authenticate(session.getSessionId(), null);
        assertNotNull(actual);
        assertEquals(expect.getLoginName(), actual.getLoginName());
    }

    private UserEntity generateUser() {
        UserEntity entity = new UserEntity();
        entity.setLoginName(String.format("%x", IdentityManager.newId()));
        entity.setPassword("password");
        entity.setDisplayName("displayName");
        entity.setMail("mail");
        return entity;
    }
}
