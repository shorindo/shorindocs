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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.action.ActionPlugin;

/**
 * 
 */
public class PluginServiceImpl implements PluginService {
    private final static ActionLogger LOG = ActionLogger.getLogger(PluginServiceImpl.class);
    private final static String PLUGIN_FILE = "META-INF/plugin.xml";

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
        } else if (file.getName().endsWith(".jar")) {
        	try (JarFile jarFile = new JarFile(file)) {
        		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
        			JarEntry entry = e.nextElement();
        			String name = entry.getName();
        			if (PLUGIN_FILE.equals(name)) {
        				LOG.info(file.getName() + " has plugin.xml");
        				InputStream zis = jarFile.getInputStream(entry);
        				byte b[] = new byte[2048];
        				int len = 0;
        				while ((len = zis.read(b)) > 0) {
        					System.out.write(b, 0, len);
        				}
        				break;
        			}
        		}
        	} catch (IOException e) {
        		LOG.error(e.getMessage(), e);
        	}
        }
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        return result;
    }
    
}
