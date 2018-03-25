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

import static com.shorindo.docs.ApplicationContext.*;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ActionLogger {
    private Logger LOG;
    private Locale lang = ApplicationContext.getLang();

    public static ActionLogger getLogger(Class<?> clazz) {
        return new ActionLogger(clazz);
    }

    private ActionLogger(Class<?> clazz) {
        LOG = Logger.getLogger(clazz);
    }

    public void trace(String msg) {
        LOG.trace(msg);
    }

    public void debug(String msg) {
        LOG.debug(msg);
    }

    public void debug(String message, Object...args) {
        if (args.length > 0) {
            LOG.debug(MessageFormat.format(message, args));
        } else {
            LOG.debug(message);
        }
    }

    public void debug(ActionMessages code, Object...args) {
        LOG.debug(code.getCode() + ":" + code.getMessage(lang, args));
    }

    public void info(ActionMessages code, Object...args) {
        LOG.info(code.getCode() + ":" + code.getMessage(lang, args));
    }

//    public void info(String message, Object...args) {
//        if (args.length > 0) {
//            LOG.info(MessageFormat.format(message, args));
//        } else {
//            LOG.info(message);
//        }
//    }

    public void warn(ActionMessages code, Object...args) {
        LOG.warn(code.getCode() + ":" + code.getMessage(lang, args));
    }

    public void warn(ActionMessages code, Throwable th, Object...args) {
        LOG.warn(code.getCode() + ":" + code.getMessage(lang, args), th);
    }

    public String error(ActionMessages code, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(lang, args);
        LOG.error(msg);
        return msg;
    }

    public String error(ActionMessages code, Throwable th, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(lang, args);
        LOG.error(msg, th);
        return msg;
    }
}
