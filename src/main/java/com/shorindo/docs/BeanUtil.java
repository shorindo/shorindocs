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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class BeanUtil {
    private static final ActionLogger LOG = ActionLogger.getLogger(BeanUtil.class);
    private static final Pattern propPattern = Pattern.compile("([a-zA-Z])([a-z0-9]*)");
    private static final Pattern SNAKE_PATTERN = Pattern.compile("_*([^_])([^_]*)");

    public static String snake2camel(String name) {
        return snake2camel(name, true);
    }

    public static String snake2camel(String name, boolean upcaseFirst) {
        Matcher m = SNAKE_PATTERN.matcher(name);
        StringBuilder sb = new StringBuilder();
        int start = 0;
        boolean first = true;
        while (m.find(start)) {
            if (first) {
                sb.append(upcaseFirst ?
                        m.group(1).toUpperCase() :
                        m.group(1).toLowerCase());
                first = false;
            } else {
                sb.append(m.group(1).toUpperCase());
            }
            String rest = m.group(2);
            if (rest != null) {
                sb.append(rest.toLowerCase());
            }
            start = m.end();
        }
        return sb.toString();
    }

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

    public static void setValue(Object bean, String name, Object value) throws BeanNotFoundException {
        Matcher m = BEAN_PATTERN.matcher(name);
        int start = 0, end = 0;

        while (m.find(start)) {
            if (m.start() != start) {
                throw new BeanNotFoundException(DocsMessages.E_5007.getMessage(name));
            }

            // 最後の項目はset
            if (m.end() == name.length()) {
                if (m.group(3) == null) {
                    setProperty(bean, m.group(1), value);
                } else {
                    bean = getProperty(bean, m.group(1));
                    setProperty(bean, m.group(3), value);
                }
            } else {
                bean = getProperty(bean, m.group(1));
                if (bean == null && end != name.length()) {
                    throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(name));
                }
                if (m.group(3) != null) {
                    bean = getProperty(bean, m.group(3));
                    if (bean == null && end != name.length()) {
                        throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(name));
                    }
                }
            }
            start = end = m.end();
        }

        if (start != end) {
            throw new BeanNotFoundException(DocsMessages.E_5007.getMessage(name));
        }
    }

    private static boolean isNumeric(String value) {
        return value == null ?
                false :
                (value.matches("^\\d+$") ? true : false);
    }

    public static Object getValue(Object bean, String name) throws BeanNotFoundException {
        Matcher m = BEAN_PATTERN.matcher(name);
        int start = 0, end = 0;

        while (m.find(start)) {
            if (m.start() != start) {
                throw new BeanNotFoundException(DocsMessages.E_5007.getMessage(name));
            }

            bean = getProperty(bean, m.group(1));
            if (bean == null && end != name.length()) {
                throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(m.group(1)));
            }
            if (m.group(3) != null) {
                bean = getProperty(bean, m.group(3));
                if (bean == null && end != name.length()) {
                    throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(m.group(3)));
                }
            }
            start = end = m.end();
        }

        if (start != end) {
            throw new BeanNotFoundException(DocsMessages.E_5007.getMessage(name));
        }

        return bean;
    }

    public static Object getValue(Object bean, String name, Object defaultValue) {
        try {
            return getValue(bean, name);
        } catch (BeanNotFoundException e) {
            LOG.info(DocsMessages.I_0004, name, defaultValue);
            return defaultValue;
        }
    }

    public static byte getValueAsByte(Object bean, String name) throws BeanNotFoundException {
        return ((Byte)getValue(bean, name)).byteValue();
    }
    public static short getValueAsShort(Object bean, String name) throws BeanNotFoundException {
        return ((Short)getValue(bean, name)).shortValue();
    }
    public static int getValueAsInt(Object bean, String name) throws BeanNotFoundException {
        return ((Integer)getValue(bean, name)).intValue();
    }
    public static long getValueAsLong(Object bean, String name) throws BeanNotFoundException {
        return ((Long)getValue(bean, name)).longValue();
    }

    private static final Pattern BEAN_PATTERN =
            Pattern.compile("\\.?([^\\.\\[\\s]+)(\\[([^\\]]+)\\])?");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void setProperty(Object bean, String name, Object value)
            throws BeanNotFoundException {
        if (bean instanceof Map) {
            ((Map)bean).put(name, value);
        } else if (bean.getClass().isArray() && isNumeric(name)) {
            Array.set(bean, Integer.valueOf(name), value);
        } else if (List.class.isAssignableFrom(bean.getClass())) {
            ((List)bean).add(Integer.valueOf(name), value);
        } else {
            Class<?> targetType = value.getClass();
            String setterName = createMethodName("set", name);
            try {
                Method method = bean.getClass().getMethod(setterName, targetType);
                method.invoke(bean, value);
            } catch (Exception e) {
                if (Long.class.isAssignableFrom(targetType)) {
                    targetType = byte.class;
                } else if (Short.class.isAssignableFrom(targetType)) {
                    targetType = short.class;
                } else if (Integer.class.isAssignableFrom(targetType)) {
                    targetType = int.class;
                } else if (Long.class.isAssignableFrom(targetType)) {
                    targetType = long.class;
                } else if (Float.class.isAssignableFrom(targetType)) {
                    targetType = float.class;
                } else if (Double.class.isAssignableFrom(targetType)) {
                    targetType = double.class;
                }
                try {
                    Method method = bean.getClass().getMethod(setterName, targetType);
                    method.invoke(bean, value);
                } catch (Exception ex) {
                    throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(name), ex);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static Object getProperty(Object bean, String name) throws BeanNotFoundException {
        if (bean instanceof Map) {
            Object result = ((Map<?,?>)bean).get(name);
            return result;
        } else if (bean.getClass().isArray() && isNumeric(name)) {
            return Array.get(bean, Integer.valueOf(name));
        } else if (List.class.isAssignableFrom(bean.getClass())) {
            return ((List)bean).get(Integer.valueOf(name));
        } else {
            try {
                String getterName = createMethodName("get", name);
                Method method = bean.getClass().getMethod(getterName);
                return method.invoke(bean);
            } catch (Exception e) {
                try {
                    Field field = bean.getClass().getDeclaredField(name);
                    if (!field.isAccessible()) field.setAccessible(true);
                    return field.get(bean);
                } catch (Exception ex) {
                    throw new BeanNotFoundException(DocsMessages.E_5005.getMessage(name), ex);
                }
            }
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
