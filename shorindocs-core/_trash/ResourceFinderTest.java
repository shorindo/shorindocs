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

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.shorindo.docs.ResourceFinder.FileMatcher;
import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public class ResourceFinderTest {
    private static final ActionLogger LOG = ActionLogger.getLogger(ResourceFinderTest.class);
    private ResourceFinder finder;

    @Before
    public void setUp() throws Exception {
        File root = new File("target/docs");
        finder = new ResourceFinder(root);
    }

    @Test
    public void testFindDirectory() {
        List<File> result = finder.find(new FileMatcher() {
            @Override
            public boolean matches(File file) {
                if ("classes".equals(file.getName()) &&
                        "WEB-INF".equals(file.getParentFile().getName())) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        assertEquals(1, result.size());
    }

    @Test
    public void testFindFiles() {
        List<File> result = finder.find(new FileMatcher() {
            @Override
            public boolean matches(File file) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        LOG.info("classes : " + result.size());
        assertTrue(result.size() > 1);
    }
}
