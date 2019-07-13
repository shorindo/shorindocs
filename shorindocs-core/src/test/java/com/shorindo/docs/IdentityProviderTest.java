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
package com.shorindo.docs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * 
 */
public class IdentityProviderTest {

    @Test
    public void testNewId() throws Exception {
        Set<Long> idSet = new HashSet<Long>();
        for (int i = 0; i < 1000; i++) {
            long id = IdentityManager.newId();
            //System.out.println(String.format("%x", id) + ":" + String.format("%d", id));
            if (idSet.contains(id)) {
                throw new RuntimeException("conflict");
            } else {
                idSet.add(id);
            }
        }
    }

}
