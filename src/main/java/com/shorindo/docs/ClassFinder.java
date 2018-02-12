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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 
 */
public class ClassFinder {
    private static DocsLogger LOG = DocsLogger.getLogger(ClassFinder.class);

    public static List<Class<?>> find(File root, ClassMatcher matcher) {
        LOG.debug("find({0}, {1})", root.exists(), matcher);
        return find(root, root, matcher);
    }
    
    private static List<Class<?>> find(File root, File curr, ClassMatcher matcher) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        if (curr.isDirectory()) {
            for (File child : curr.listFiles()) {
                result.addAll(find(root, child, matcher));
            }
        } else if (curr.getName().endsWith(".class")) {
            int pos = root.getAbsolutePath().length() + 1;
            String className = curr.getAbsolutePath().substring(pos)
                    .replaceAll("\\.class$", "")
                    .replace(System.getProperty("file.separator"), ".");
            try {
                Class<?> clazz = Class.forName(className);
                if (matcher.matches(clazz)) {
                    result.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.debug("Not found:" + className);
            }
        } else if (curr.getName().endsWith(".jar")) {
            result.addAll(findFromJar(curr, matcher));
        }
        return result;
    }

    private static List<Class<?>> findFromJar(File jar, ClassMatcher matcher) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        JarInputStream jis = null;
        try {
            jis = new JarInputStream(new FileInputStream(jar));
            while (true) {
                JarEntry entry = jis.getNextJarEntry();
                if (entry == null) break;
                if (!entry.getName().endsWith(".class")) continue;
                String className = entry.getName()
                        .replaceAll("\\.class$", "")
                        .replace("/", ".");
                //LOG.debug("found - " + className);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (matcher.matches(clazz)) {
                        result.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    LOG.debug("Not found:" + className);
                }
            }
        } catch (FileNotFoundException e) {
            LOG.error(Messages.E9999, e);
        } catch (IOException e) {
            LOG.error(Messages.E9999, e);
        } finally {
            try {
                jis.close();
            } catch (IOException e) {
                LOG.error(Messages.E9999, e);
            }
        }
        return result;
    }

    public static interface ClassMatcher {
        public boolean matches(Class<?> clazz);
    }
}
