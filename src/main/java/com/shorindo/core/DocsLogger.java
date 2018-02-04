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
package com.shorindo.core;

import org.apache.log4j.LogManager;

/**
 * 
 */
public class DocsLogger {
    org.apache.log4j.Logger logger;

    public static DocsLogger getLogger(Class<?> clazz) {
        return new DocsLogger(clazz);
    }
    protected DocsLogger(Class<?> clazz) {
        logger = LogManager.getLogger(clazz);
    }
    public void trace(String msg) {
        logger.trace(msg);
    }
    public void debug(String msg) {
        logger.debug(msg);
    }
    public void debug(String msg, Throwable th) {
        logger.debug(msg, th);
    }
//    public void info(String msg) {
//        logger.info(msg);
//    }
//    public void info(String msg, Throwable th) {
//        logger.info(msg, th);
//    }
//    public void warn(String msg) {
//        logger.warn(msg);
//    }
//    public void warn(String msg, Throwable th) {
//        logger.warn(msg, th);
//    }
//    public void error(String msg) {
//        logger.error(msg);
//    }
//    public void error(String msg, Throwable th) {
//        logger.error(msg, th);
//    }

    public void info(Messages code, Object...args) {
        logger.info(code.name() + ":" + code.getMessage(args));
    }
    public void warn(Messages code, Object...args) {
        logger.warn(code.name() + ":" + code.getMessage(args));
    }
    public void error(Messages code, Object...args) {
        logger.error(code.name() + ":" + code.getMessage(args));
    }
    public void error(Messages code, Throwable th, Object...args) {
        logger.error(code.name() + ":" + code.getMessage(args), th);
    }
}
