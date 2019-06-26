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
package com.shorindo.docs.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 */
public interface ActionMessages {
    public String name();
    public Map<String,MessageFormat> getBundle();
    public String getCode();
    public String getMessage(Object...args);
    public String getMessage(Locale locale, Object...args);

    /**
     * 
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Message {
        String ja();
        String en() default "undefined";
    }

    /**
     * 
     */
    public static abstract class Util {

        public static Map<String,MessageFormat> bundle(ActionMessages m) {
            Map<String,MessageFormat> bundle = new LinkedHashMap<String,MessageFormat>();
            try {
                Field field = m.getClass().getField(m.name());
                Message message = field.getAnnotation(Message.class);
                if (message != null) {
                    bundle.put(Locale.JAPANESE.getLanguage(), new MessageFormat(message.ja()));
                    bundle.put(Locale.ENGLISH.getLanguage(), new MessageFormat(message.en()));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return bundle;
        }

        public static String getCode(ActionMessages m) {
            return m.name();
        }

        public static String getMessage(ActionMessages m, Object...params) {
            return getMessage(Locale.getDefault(), m, params);
        }

        public static String getMessage(Locale locale, ActionMessages m, Object...params) {
            MessageFormat format = m.getBundle().get(locale.getLanguage());
            if (format != null)
                return format.format(params);
            else
                return null;
        }

        public static String getString(ActionMessages m) {
            return m.name() + ":" + m.getBundle();
        }
    }
}
