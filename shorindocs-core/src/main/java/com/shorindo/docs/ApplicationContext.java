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
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;

/**
 * 
 */
public class ApplicationContext {
    private static final ActionLogger LOG = ActionLogger.getLogger(ApplicationContext.class);
    private static final Properties props = new Properties();
    private static final Locale DEFAULT_LANG = Locale.JAPANESE;
    public static final String WEB_INF_CLASSES = "/WEB-INF/classes";
    public static final String WEB_INF_LIB = "/WEB-INF/lib";

    public ApplicationContext() {
    }

    public static void loadProperties(InputStream is) {
        try {
            props.load(is);
        } catch (IOException e) {
            try {
                is.close();
            } catch (IOException e1) {
                LOG.error(DocumentMessages.DOCS_9999, e1);
            }
        }
    }

    public static void init(Properties p) {
        props.putAll(p);
    }

    public static Properties getProperties() {
        return props;
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static Locale getLang() {
        return DEFAULT_LANG;
    }
    
    public static void getClassPath() {
        LOG.info("context path=" + Thread.currentThread().getContextClassLoader().getResource("").getPath());
        String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String path : paths) {
            LOG.info("path=" + path);
        }
    }
}
