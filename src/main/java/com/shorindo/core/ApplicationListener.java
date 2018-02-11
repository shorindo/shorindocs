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
package com.shorindo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.shorindo.core.ClassFinder.ClassMatcher;
import com.shorindo.core.annotation.ActionMapping;
import com.shorindo.core.annotation.ActionMethod;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ApplicationListener implements ServletContextListener {
    private static final DocsLogger LOG = DocsLogger.getLogger(ApplicationListener.class);

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
            LOG.error(Messages.E_9999, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                LOG.error(Messages.E_9999, e);
            }
        }

//        File root = new File(event.getServletContext().getRealPath("/WEB-INF/classes"));
//        ClassFinder.find(root, new ClassMatcher() {
//            public boolean matches(Class<?> clazz) {
//                ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
//                if (mapping != null) {
//                    LOG.info(Messages.I_0001, mapping.value(), clazz);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        XumlView.init(event.getServletContext().getRealPath("/WEB-INF/classes"));
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

}
