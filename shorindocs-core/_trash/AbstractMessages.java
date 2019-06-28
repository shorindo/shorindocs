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
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

/**
 * 
 */
public abstract class AbstractMessages {
    
    protected static interface Messages {
        public Map<String,MessageFormat> getBundle();
        public String name();
        public String getCode();
        public String getMessage();
        public String getMessage(Locale locale);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Message {
        String ja();
        String en() default "undefined";
    }

    protected void bundle(Messages m) {
        Map<String,MessageFormat> bundle = m.getBundle();
        try {
            Field field = m.getClass().getField(m.name());
            Message message = field.getAnnotation(Message.class);
            if (message != null) {
                bundle.put(Locale.JAPANESE.getLanguage(),
                        new MessageFormat(message.ja()));
                bundle.put(Locale.ENGLISH.getLanguage(),
                        new MessageFormat(message.en()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getMessage(Messages m) {
        return getMessage(Locale.getDefault(), m);
    }

    protected String getMessage(Locale locale, Messages m, Object...args) {
        MessageFormat format = m.getBundle().get(locale.getLanguage());
        return format.format(args);
    }

    protected String getString(Messages m) {
        return m.name() + ":" + m.getBundle();
    }
}
