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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.shorindo.docs.auth.AuthenticateServiceTest;
import com.shorindo.docs.document.DocumentServiceTest;
import com.shorindo.docs.repository.RepositoryServiceTest;

/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({
    BeanUtilTest.class,
//    IdentityProviderTest.class,
    RepositoryServiceTest.class,
    AuthenticateServiceTest.class,
    DocumentServiceTest.class
})
public class AllTests {
}
