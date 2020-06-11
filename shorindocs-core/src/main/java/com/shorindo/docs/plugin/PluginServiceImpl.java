/*
 * Copyright 2020 Shorindo, Inc.
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
package com.shorindo.docs.plugin;

import static com.shorindo.docs.document.DocumentMessages.DOCS_9999;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.action.ActionPlugin;

/**
 * 
 */
public class PluginServiceImpl implements PluginService {
    private final static ActionLogger LOG = ActionLogger.getLogger(PluginServiceImpl.class);
    private final static String SEPARATOR = File.separator.replaceAll("\\\\", "\\\\\\\\");

    public final void addPlugin(Class<? extends ActionPlugin> clazz) {
        try {
            ActionPlugin plugin = clazz.newInstance();
            plugin.initialize();
        } catch (InstantiationException e) {
            LOG.error(DOCS_9999, e);
        } catch (IllegalAccessException e) {
            LOG.error(DOCS_9999, e);
        }
    }

    /*
     *
     */
    @Override
    public List<Class<? extends ActionPlugin>> findPlugin(File file) {
        return findPlugin(getClass().getClassLoader(), file, file);
    }

    private List<Class<? extends ActionPlugin>> findPlugin(ClassLoader loader, File base, File file) {
        List<Class<? extends ActionPlugin>> result = new ArrayList<>();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                result.addAll(findPlugin(loader, base, child));
            }
        } else {
            if (file.getName().endsWith(".class")) {
                String className = file.getAbsolutePath()
                    .substring(base.getAbsolutePath().length())
                    .replaceAll("\\.class$", "")
                    .replaceAll("^" + SEPARATOR, "")
                    .replaceAll(SEPARATOR, ".");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (ActionPlugin.class.isAssignableFrom(clazz) && clazz != ActionPlugin.class) {
                        LOG.info("Plugin {0} found.", className);
                        result.add((Class<ActionPlugin>)clazz);
                    }
                } catch (Throwable e1) {
                    //LOG.debug(e1.getMessage());
                }
            } else if (file.getName().endsWith(".jar")) {
                LOG.debug("Start scan : " + file.getName());
                try (JarFile jarFile = new JarFile(file)) {
                    Manifest manifest = jarFile.getManifest();
                    if (manifest != null) {
                        Attributes attrs = manifest.getMainAttributes();
                        for (Entry<Object, Object> e : attrs.entrySet()) {
                            LOG.debug(e.getKey() + " ==> " + e.getValue());
                        }
                    }
                    //LOG.debug(manifest.getMainAttributes().getValue("Plugin-Class"));
                    //LOG.debug(manifest.getMainAttributes().getValue("Plugin-Alias"));
//                    for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
//                        JarEntry entry = e.nextElement();
//                        String name = entry.getName();
//                        if (!name.endsWith(".class") || name.startsWith("java")) {
//                            continue;
//                        }
//                        String className = name
//                            .replaceAll("\\.class$", "")
//                            .replaceAll("/", ".");
//                        try {
//                            Class<?> clazz = Class.forName(className);
//                            if (ActionPlugin.class.isAssignableFrom(clazz) && clazz != ActionPlugin.class) {
//                                LOG.info("Plugin {0} found.", className);
//                                result.add((Class<ActionPlugin>)clazz);
//                            }
//                        } catch (Throwable e1) {
//                            //LOG.debug(e1.getMessage());
//                        }
//                    }
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }
    
}
