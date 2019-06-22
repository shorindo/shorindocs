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
package com.shorindo.docs;

import static org.junit.Assert.*;

import org.junit.Test;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.NotFoundException;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
public class ActionContextTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }

    public static class MyContext extends ActionContext {
        private RepositoryServiceImpl repository = getService(RepositoryServiceImpl.class);

        public void run() throws NotFoundException, DatabaseException {
            getUser();
            UserEntity e = new UserEntity();
            e = repository.get(e);
        }
    }
}
