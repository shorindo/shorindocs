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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class BeanUtil {
    private static final ActionLogger LOG = ActionLogger.getLogger(BeanUtil.class);
    private static final Pattern propPattern = Pattern.compile("([a-zA-Z])([a-z0-9]*)");
//    private static Pattern SNAKE_PATTERN = Pattern.compile("_*([^_])([^_]*)");
//
//    private static String snake2camel(String name) {
//        Matcher m = SNAKE_PATTERN.matcher(name);
//        StringBuilder sb = new StringBuilder();
//        int start = 0;
//        while (m.find(start)) {
//            sb.append(m.group(1).toUpperCase());
//            String rest = m.group(2);
//            if (rest != null) {
//                sb.append(rest.toLowerCase());
//            }
//            start = m.end();
//        }
//        return sb.toString();
//    }

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setProperty(Object bean, String name, Object value) {
        if (bean instanceof Map) {
            ((Map)bean).put(name, value);
        } else {
            try {
                String setterName = createMethodName("set", name);
                Method method = bean.getClass().getMethod(setterName, Object.class);
                method.invoke(bean, value);
            } catch (Exception e) {
                LOG.error(DocsMessages.E_9999, e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setProperty(Object bean, String name, String value) {
        if (bean instanceof Map) {
            ((Map)bean).put(name, value);
        } else {
            try {
                String setterName = createMethodName("set", name);
                Method method = bean.getClass().getMethod(setterName, String.class);
                method.invoke(bean, value);
            } catch (Exception e) {
                LOG.warn(DocsMessages.W_3001, name);
            }
        }
    }

    public static Object getProperty(Object bean, String name) throws BeanNotFoundException {
        if (bean instanceof Map) {
            Object result = ((Map<?,?>)bean).get(name);
            if (result == null) {
                throw new BeanNotFoundException(name + " from " + bean);
            }
            return result;
        } else {
            try {
                String getterName = createMethodName("get", name);
                Method method = bean.getClass().getMethod(getterName);
                return method.invoke(bean);
            } catch (Exception e) {
                throw new BeanNotFoundException(name + " from " + bean);
            }
        }
    }

    public static void setSnakeProperty(Object bean, String name, Object value) {
        
    }

    public static Object getValue(Object bean, String name) throws BeanNotFoundException {
        String[] seg = name.split("\\.");
        for (String key : seg) {
            bean = getProperty(bean, key);
        }
        return bean;
    }

    public static Object getValue(Object bean, String name, Object defaultValue) {
        try {
            return getValue(bean, name);
        } catch (BeanNotFoundException e) {
            LOG.error(DocsMessages.E_9999, e);
            return defaultValue;
        }
    }

    public static class BeanNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;

        public BeanNotFoundException() {
            super();
        }

        public BeanNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public BeanNotFoundException(String message) {
            super(message);
        }

        public BeanNotFoundException(Throwable cause) {
            super(cause);
        }
    }
}
