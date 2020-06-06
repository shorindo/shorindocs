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
package com.shorindo.docs.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.shorindo.docs.document.DocumentMessages;

/**
 * 
 */
public class ActionContext {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionContext.class);
    int status = 200;
    private String requestPath;
    private String contextPath;
    private String action = "view";
    private Locale locale;
    private ResourceBundle bundle;

    // URL
    private String id;
    private Map<String,String> requestHeader;
    // RequestParameter
    private Map<String,String[]> parameters;
    private Map<String,String> responseHeader;
    // ResponseResult
    // Request属性
    // Session属性

    public ActionContext() {        
        bundle = ResourceBundle.getBundle("messages", Locale.JAPANESE /*req.getLocale()*/);
        Map<String,String> application = new HashMap<String,String>();
        this.setAttribute("application", application);
        requestHeader = new HashMap<>();
        responseHeader = new HashMap<>();
    }

//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getMessage(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_5001, e);
            return key;
        }
    }

    public String getHeader(String name) {
        return requestHeader.get(name);
    }

    public void setHeader(String name, String value) {
        requestHeader.put(name, value);
    }

    private Map<String,Object> attributes = new HashMap<String,Object>();
    public Map<String,Object> getAttributes() {
        return attributes;
    }
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    public void setParameters(Map<String,String[]> params) {
        this.parameters = params;
    }
    public String getParameter(String name) {
        String[] params = parameters.get(name);
        if (params == null || params.length == 0) {
            return null;
        } else {
            return params[0];
        }
    }
    public String[] getParameters(String name) {
        return parameters.get(name);
    }

}
