/*
 * Copyright 2015 Shorindo, Inc.
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

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 
 */
public class ActionLogger {
    private Logger logger;
    private Locale lang = ApplicationContext.getLang();

    public static ActionLogger getLogger(Class<?> clazz) {
        return new ActionLogger(clazz);
    }

    private ActionLogger(Class<?> clazz) {
        logger = LogManager.getLogger(clazz);
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String message, Object...args) {
        if (args.length > 0) {
            logger.debug(MessageFormat.format(message, args));
        } else {
            logger.debug(message);
        }
    }

    public void debug(ActionMessages code, Object...args) {
        logger.debug(code.getCode() + ":" + code.getMessage(lang, args));
    }

    public void info(ActionMessages code, Object...args) {
        logger.info(code.getCode() + ":" + code.getMessage(lang, args));
    }

    public void info(String message, Object...args) {
        logger.info(MessageFormat.format(message, args));
    }

    public void warn(ActionMessages code, Object...args) {
        logger.warn(code.getCode() + ":" + code.getMessage(lang, args));
    }

    public void warn(ActionMessages code, Throwable th, Object...args) {
        logger.warn(code.getCode() + ":" + code.getMessage(lang, args), th);
    }

    public String error(ActionMessages code, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(lang, args);
        logger.error(msg);
        return msg;
    }

    public String error(ActionMessages code, Throwable th, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(lang, args);
        logger.error(msg, th);
        return msg;
    }

    public String error(String message, Throwable th, Object...args) {
        logger.error(message, th);
        return message;
    }
}
