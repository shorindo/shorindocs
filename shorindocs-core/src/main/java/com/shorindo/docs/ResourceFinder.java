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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public class ResourceFinder {
    public static String JAVA_CLASS_PATH = "java.class.path";
    public static String PATH_SEPARATOR = "path.separator";
    private static ActionLogger LOG = ActionLogger.getLogger(ResourceFinder.class);
    private File root;

    public ResourceFinder(File root) {
        this.root = root;
    }

    public List<File> find(FileMatcher matcher) {
        return findFile(root, matcher);
    }

    public List<Class<?>> find(ClassMatcher matcher) {
        return findClass(root, matcher);
    }

    private List<File> findFile(File base, FileMatcher matcher) {
        //LOG.debug("findFile(" + base.getAbsolutePath() + ")");
        List<File> resultList = new ArrayList<File>();

        if (matcher.matches(base)) {
            resultList.add(base);
        }

        if (base.isDirectory()) {
            for (File file : base.listFiles()) {
                resultList.addAll(findFile(file, matcher));
            }
        }

        return resultList;
    }

    private List<Class<?>> findClass(File base, ClassMatcher matcher) {
        //LOG.debug("findFile(" + base.getAbsolutePath() + ")");
        List<Class<?>> resultList = new ArrayList<Class<?>>();

        if (base.isFile() && base.getName().endsWith(".class")) {
            String className = base.getAbsolutePath()
                    .substring(root.getAbsolutePath().length() + 1)
                    .replaceAll(".class$", "")
                    .replace(System.getProperty("file.separator"), ".");
            try {
                Class<?> clazz = Class.forName(className);
                if (matcher.matches(clazz)) {
                    resultList.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.warn(DOCS_3002, className);
            }
        } else if (base.isDirectory()) {
            for (File file : base.listFiles()) {
                resultList.addAll(findClass(file, matcher));
            }
        }

        return resultList;
    }

//    /**
//     * 
//     * @param root
//     * @param matcher
//     * @return
//     */
//    public static List<Class<?>> find(File root, ResourceMatcher matcher) {
//        LOG.debug("find({0}, {1})", root.exists(), matcher);
//        return find(root, root, matcher);
//    }

//    /**
//     * 
//     * @param root
//     * @param curr
//     * @param matcher
//     * @return
//     */
//    private static List<Class<?>> find(File root, File curr, ResourceMatcher matcher) {
//        List<Class<?>> result = new ArrayList<Class<?>>();
//        if (curr.isDirectory()) {
//            for (File child : curr.listFiles()) {
//                result.addAll(find(root, child, matcher));
//            }
//        } else if (curr.getName().endsWith(".class")) {
//            int pos = root.getAbsolutePath().length() + 1;
//            String className = curr.getAbsolutePath().substring(pos)
//                    .replaceAll("\\.class$", "")
//                    .replace(System.getProperty("file.separator"), ".");
//            try {
//                Class<?> clazz = Class.forName(className);
//                if (matcher.matches(clazz)) {
//                    result.add(clazz);
//                }
//            } catch (ClassNotFoundException e) {
//                LOG.debug("Not found:" + className);
//            }
//        } else if (curr.getName().endsWith(".jar")) {
//            result.addAll(findFromJar(curr, matcher));
//        }
//        return result;
//    }

//    /**
//     * 
//     * @param jar
//     * @param matcher
//     * @return
//     */
//    private static List<Class<?>> findFromJar(File jar, ResourceMatcher matcher) {
//        List<Class<?>> result = new ArrayList<Class<?>>();
//        JarInputStream jis = null;
//        try {
//            jis = new JarInputStream(new FileInputStream(jar));
//            while (true) {
//                JarEntry entry = jis.getNextJarEntry();
//                if (entry == null) break;
//                if (!entry.getName().endsWith(".class")) continue;
//                String className = entry.getName()
//                        .replaceAll("\\.class$", "")
//                        .replace("/", ".");
//                //LOG.debug("found - " + className);
//                try {
//                    Class<?> clazz = Class.forName(className);
//                    if (matcher.matches(clazz)) {
//                        result.add(clazz);
//                    }
//                } catch (ClassNotFoundException e) {
//                    LOG.debug("Not found:" + className);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            LOG.error(DocumentMessages.DOCS_9999, e);
//        } catch (IOException e) {
//            LOG.error(DocumentMessages.DOCS_9999, e);
//        } finally {
//            try {
//                jis.close();
//            } catch (IOException e) {
//                LOG.error(DocumentMessages.DOCS_9999, e);
//            }
//        }
//        return result;
//    }

    /**
     * 
     */
//    public static interface ResourceMatcher {
//        public boolean matches(Class<?> clazz);
//    }

    public static interface FileMatcher {
        public boolean matches(File file);
    }
    
    public static interface ClassMatcher {
        public boolean matches(Class<?> clazz);
    }
}
