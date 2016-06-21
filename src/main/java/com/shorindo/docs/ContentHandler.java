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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.shorindo.docs.plaintext.PlainTextHandler;

/**
 * 
 */
public abstract class ContentHandler {
    private static final Logger LOG = Logger.getLogger(ContentHandler.class);
    private Map<String,Object> attributes = new HashMap<String,Object>();
    private ContentModel model;
    public abstract String getContentType();
    public abstract String view(Properties params);

    public static ContentHandler getHandler(ContentModel model) {
        if ("text/plain".equals(model.getContentType())) {
            return new PlainTextHandler(model);
        } else {
            return null;
        }
    }

    public static ContentHandler getHandler(String id) throws IOException {
        try {
            ContentModel model = DatabaseManager.selectOne(
                    ContentModel.class,
                    "SELECT * FROM CONTENT WHERE CONTENT_ID=? AND STATUS=0",
                    id);
            if ("text/plain".equals(model.getContentType())) {
                return new PlainTextHandler(model);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static ContentModel getContentModel(String id) throws SQLException {
        return DatabaseManager.selectOne(
                ContentModel.class,
                "SELECT * FROM CONTENT WHERE CONTENT_ID=? AND STATUS=0",
                id);
    }

    public ContentHandler(ContentModel model) {
        this.model = model;
    }

    public ContentModel getModel() {
        return model;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    public String action(String name, Properties params) {
        try {
            Method method = getClass().getMethod(name, Properties.class);
            if (method.getAnnotation(Action.class) != null &&
                    method.getReturnType().isAssignableFrom(String.class)) {
                return (String)method.invoke(this, params);
            } else {
                LOG.warn("no suitable method '" + name + "' exists");
            }
        } catch (SecurityException e) {
            LOG.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    public ContentModel save(ContentModel model) throws IOException {
        return null;
    }

    @Action
    public List<ContentModel> search() throws SQLException {
        LOG.trace("search()");
        return DatabaseManager.select(
                ContentModel.class,
                "SELECT CONTENT_ID, CONTENT_TYPE, TITLE, UPDATE_DATE " +
                "FROM   CONTENT " +
                "ORDER BY UPDATE_DATE DESC " +
                "LIMIT 10");
    }
}
