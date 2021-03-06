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
package com.shorindo.docs.asciidoc;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.asciidoctor.Asciidoctor.Factory.create;

import org.asciidoctor.Asciidoctor;

/**
 * 
 */
public class ConvertTest {
    private static Asciidoctor doctor;

    @BeforeClass
    public static void setUp() {
        doctor = create();
    }

    @Test
    public void test() {
        long st = System.currentTimeMillis();
        Map<String,Object> attributes = new HashMap<String,Object>();
        attributes.put("backend", "docbook");
        Map<String,Object> options = new HashMap<String,Object>();
        options.put("attributes", attributes);
        options.put("in_place", true);
        String result = doctor.convert("Writing AsciiDoc is _easy_!", options);
        System.out.println((System.currentTimeMillis() - st) + ":" + result);
    }

}
