/*
 * Copyright 2016 Shorindo, Inc.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.shorindo.docs.view.XumlView;
import com.shorindo.docs.xuml.XumlEngine;

/**
 * 
 */
public class AppListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppListener.class);

    public void contextDestroyed(ServletContextEvent event) {
        LOG.trace("contextDestroyed()");
        DatabaseManager.shutdown();
    }

    public void contextInitialized(ServletContextEvent event) {
        LOG.trace("contextInitialized()");
        Properties siteProperties = new Properties();
        String path = event.getServletContext().getRealPath("/WEB-INF/site.properties");
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            siteProperties.load(is);
            PropertyConfigurator.configure(siteProperties);
            DatabaseManager.init(siteProperties);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        findClass(event.getServletContext().getRealPath("/WEB-INF/classes"));
        XumlEngine.init(event.getServletContext().getRealPath("/WEB-INF/classes"));
        XumlView.setBasePath(event.getServletContext().getRealPath("/"));
    }

    private void findClass(String path) {
        ClassLoader loader = new ClassFinder();
    }

    public static class ClassFinder extends ClassLoader {
    }
}
