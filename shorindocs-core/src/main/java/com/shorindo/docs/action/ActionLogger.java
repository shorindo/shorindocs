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
package com.shorindo.docs.action;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;

import org.slf4j.LoggerFactory;

import com.shorindo.docs.ApplicationContext;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;

/**
 * 
 */
public class ActionLogger {
	private static Appender<ILoggingEvent> appender = createAppender();
	private Locale LANG = ApplicationContext.getLang();
	private Logger logger;

    public static ActionLogger getLogger(Class<?> clazz) {
        return new ActionLogger(clazz);
    }

    private static Appender<ILoggingEvent> createAppender() {
    	LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    	PatternLayoutEncoder ple = new PatternLayoutEncoder();
    	ple.setPattern("%d %5p %c{0} - %msg%n");
    	ple.setContext(lc);
    	ple.setCharset(StandardCharsets.UTF_8);
    	ple.start();

    	ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    	appender.setEncoder(ple);
    	appender.setContext(lc);
    	appender.start();
    	return appender;
    }

    private ActionLogger(Class<?> clazz) {
        logger = (Logger)LoggerFactory.getLogger(clazz);
        logger.addAppender(appender);
        logger.setAdditive(false);
    }
    
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void trace(String message, Object...args) {
        if (args.length > 0) {
            logger.trace(MessageFormat.format(message, args));
        } else {
            logger.trace(message);
        }
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
        logger.debug(code.getCode() + ":" + code.getMessage(LANG, args));
    }

    public void info(ActionMessages code, Object...args) {
        logger.info(code.getCode() + ":" + code.getMessage(LANG, args));
    }

    public void info(String message, Object...args) {
        logger.info(MessageFormat.format(message, args));
    }

    public void warn(ActionMessages code, Object...args) {
        logger.warn(code.getCode() + ":" + code.getMessage(LANG, args));
    }

    public void warn(ActionMessages code, Throwable th, Object...args) {
        logger.warn(code.getCode() + ":" + code.getMessage(LANG, args), th);
    }

    public void warn(String message, Object...args) {
        logger.warn(MessageFormat.format(message, args));
    }

    public String error(ActionMessages code, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(LANG, args);
        logger.error(msg);
        return msg;
    }

    public String error(ActionMessages code, Throwable th, Object...args) {
        String msg = code.getCode() + ":" + code.getMessage(LANG, args);
        logger.error(msg, th);
        return msg;
    }

    public String error(String message, Throwable th, Object...args) {
        logger.error(message, th);
        return message;
    }
}
