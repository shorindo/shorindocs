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

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

/**
 * 
 */
public class BeanUtils {
    private static final Logger LOG = Logger.getLogger(BeanUtils.class);
    private static final Pattern propPattern = Pattern.compile("([a-zA-Z])([a-z0-9]*)");

    private static String createMethodName(String prefix, String propertyName) {
        String getterName = prefix;
        Matcher matcher = propPattern.matcher(propertyName);
        int start = 0;
        while (matcher.find(start)) {
            getterName +=
                    matcher.group(1).toUpperCase() +
                    matcher.group(2);
            start = matcher.end();
        }
        return getterName;
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            String setterName = createMethodName("set", name);
            Method method = bean.getClass().getMethod(setterName, Object.class);
            method.invoke(bean, value);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void setProperty(Object bean, String name, String value) {
        try {
            String setterName = createMethodName("set", name);
            Method method = bean.getClass().getMethod(setterName, String.class);
            method.invoke(bean, value);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static Object getProperty(Object bean, String name) {
        try {
            String getterName = createMethodName("get", name);
            Method method = bean.getClass().getMethod(getterName);
            return method.invoke(bean);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
