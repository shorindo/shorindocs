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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 
 */
public class DocsLogger {
    private Logger LOG;

    public static DocsLogger getLogger(Class<?> clazz) {
        return new DocsLogger(clazz);
    }
    private DocsLogger(Class<?> clazz) {
        LOG = LogManager.getLogger(clazz);
    }
    public void trace(String msg) {
        LOG.trace(msg);
    }
    public void debug(String msg) {
        LOG.debug(msg);
    }
    public void debug(String msg, Throwable th) {
        LOG.debug(msg, th);
    }
    public void debug(String message, Object...args) {
        if (args.length > 0) {
            LOG.debug(MessageFormat.format(message, args));
        } else {
            LOG.debug(message);
        }
    }
    public void info(Messages code, Object...args) {
        LOG.info(code.name() + ":" + code.getMessage(args));
    }
    public void info(String message, Object...args) {
        if (args.length > 0) {
            LOG.info(MessageFormat.format(message, args));
        } else {
            LOG.info(message);
        }
    }
    public void warn(Messages code, Object...args) {
        LOG.warn("[" + code.name() + "] " + code.getMessage(args));
    }
    public void error(Messages code, Object...args) {
        LOG.error("[" + code.name() + "] " + code.getMessage(args));
    }
    public void error(Messages code, Throwable th, Object...args) {
        LOG.error("[" + code.name() + "] " + code.getMessage(args), th);
    }
}
