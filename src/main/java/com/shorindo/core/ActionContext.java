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
package com.shorindo.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class ActionContext {
    private static final Logger LOG = Logger.getLogger(ActionContext.class);
    private String contextPath;
    private String servletPath;
    private Locale locale;
    private ResourceBundle bundle;
    private Map<String,Object> requestMap = new HashMap<String,Object>();
    private Map<String,Object> serverMap = new HashMap<String,Object>();
    private Map<String,Object> clientMap = new HashMap<String,Object>();
    private Map<String,String> paramMap = new HashMap<String,String>();

    public ActionContext(HttpServletRequest req) {
        contextPath = req.getContextPath();
        servletPath = req.getServletPath();
        locale = req.getLocale();

        bundle = ResourceBundle.getBundle("messages", req.getLocale());
        for (Enumeration<?> e = req.getAttributeNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            requestMap.put(key, req.getAttribute(key));
        }
        HttpSession session = req.getSession();
        for (Enumeration<?> e = session.getAttributeNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            serverMap.put(key, session.getAttribute(key));
        }
        for (Enumeration<?> e = req.getParameterNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            paramMap.put(key, req.getParameter(key));
        }
    }
    
    public String getContextPath() {
        return contextPath;
    }

    public String getServletPath() {
        return servletPath;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getMessage(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            LOG.error("message[" + key + "] not found by " + e.getMessage());
            return key;
        }
    }
    public Map<String,Object> getAttributes() {
        return requestMap;
    }
    public void setAttribute(String key, Object value) {
        requestMap.put(key, value);
    }
    public Object getAttribute(String key) {
        return requestMap.get(key);
    }
    public String getParameter(String key) {
        return paramMap.get(key);
    }
}
