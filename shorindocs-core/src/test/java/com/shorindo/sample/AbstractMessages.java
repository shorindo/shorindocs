/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.sample;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 */
public interface AbstractMessages {
    public Map<String,String> getBundle();
    public String name();
    public String getCode();
    public String getMessage();
    public String getMessage(Locale locale);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Message {
        String ja();
        String en() default "undefined";
    }

    public static abstract class MessageUtil {
        protected static void bundle(AbstractMessages m) {
            Map<String,String> bundle = m.getBundle();
            try {
                Field field = m.getClass().getField(m.name());
                Message message = field.getAnnotation(Message.class);
                if (message != null) {
                    bundle.put(Locale.JAPANESE.getLanguage(), message.ja());
                    bundle.put(Locale.ENGLISH.getLanguage(), message.en());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected static String getMessage(AbstractMessages m) {
            return getMessage(Locale.getDefault(), m);
        }

        protected static String getMessage(Locale locale, AbstractMessages m) {
            return m.getBundle().get(locale.getLanguage());
        }

        protected static String getString(AbstractMessages m) {
            return m.name() + ":" + m.getBundle();
        }
    }
}
