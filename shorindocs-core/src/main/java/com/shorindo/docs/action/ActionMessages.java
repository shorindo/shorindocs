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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * For example:
 * public enum FooMessages implements ActionMessages {
 *     @Message(ja = "key={0}")
 *     X001,
 *     @Message(ja = "value={0}")
 *     X002;
 *
 *     private Map<String,MessageFormat> bundle;
 *
 *     private DocumentMessages() {
 *         bundle = ActionMessages.Util.bundle(this);
 *     }
 *
 *     @Override
 *     public Map<String, MessageFormat> getBundle() {
 *         return bundle;
 *     }
 *
 *     @Override
 *     public String getCode() {
 *         return ActionMessages.Util.getCode(this);
 *     }
 *
 *     @Override
 *     public String getMessage(Object... params) {
 *         return ActionMessages.Util.getMessage(this, params);
 *     }
 *
 *     @Override
 *     public String getMessage(Locale locale, Object... params) {
 *         return ActionMessages.Util.getMessage(this, params);
 *     }
 * } 
 */
public interface ActionMessages {
    public String name();

    /**
     * 
     * @return
     */
    default public String getCode() {
        return name();
    }

    /**
     * 
     * @param locale
     * @param args
     * @return
     */
    default public String getMessage(Locale locale, Object...args) {
        MessageFormat format = getFormat(locale);
        if (format != null) {
            return format.format(args);
        } else {
            return null;
        }
    }

    default public MessageFormat getFormat() {
        return getFormat(Locale.getDefault());
    }

    default public MessageFormat getFormat(Locale locale) {
        try {
            Field field = getClass().getField(name());
            Message[] messages = field.getAnnotationsByType(Message.class);
            for (int i = 0; messages != null && i < messages.length; i++) {
                Message message = messages[i];
                if (Objects.equals(message.lang(), locale.getLanguage())) {
                    MessageFormat format = new MessageFormat(message.content());
                    return format;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * デフォルトLocaleのメッセージを取得する
     *
     * @param args メッセージパラメータ
     * @return パラメータ解決したメッセージ
     */
    default public String getMessage(Object...args) {
        return getMessage(Locale.getDefault(), args);
    }

    /**
     * Messageアノテーションの定義
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Repeatable(MessageHolder.class)
    public static @interface Message {
        String lang();
        String content();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface MessageHolder {
        public Message[] value();
    }
}
