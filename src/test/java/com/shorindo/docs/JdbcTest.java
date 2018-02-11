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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

/**
 * 
 */
public class JdbcTest {

    @Test
    public void test() {
        new MockUp<String>() {
            String it;
            Map<String,Object> map = new HashMap<String,Object>();

            @Mock public void $init(String s) {
                map.put(s, it);
            }
            @Mock public String toString() {
                return String.valueOf(it.hashCode());
            }
        };

        String s1 = new String("s1");
        String s2 = new String("s2");
        System.out.println(s1);
        System.out.println(s2);
    }

    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
