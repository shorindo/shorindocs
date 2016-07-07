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
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class ActionContext {
    private static final Logger LOG = Logger.getLogger(ActionContext.class);
    private ResourceBundle bundle;
    private Map<String,Object> requestMap = new HashMap<String,Object>();
    private Map<String,Object> sessionMap = new HashMap<String,Object>();
    private Map<String,String> paramMap = new HashMap<String,String>();
    private String forward;

    public ActionContext(HttpServletRequest req) {
        req.getAuthType();
        req.getCharacterEncoding();
        req.getContentLength();
        req.getContentType();
        req.getContextPath();
        req.getLocale();
        req.getRemoteAddr();
        req.getServletPath();
        req.getUserPrincipal();
        //req.getCookies();

        bundle = ResourceBundle.getBundle("messages", req.getLocale());
        for (Enumeration<?> e = req.getAttributeNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            requestMap.put(key, req.getAttribute(key));
        }
        HttpSession session = req.getSession();
        for (Enumeration<?> e = session.getAttributeNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            sessionMap.put(key, session.getAttribute(key));
        }
        for (Enumeration<?> e = req.getParameterNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            paramMap.put(key, req.getParameter(key));
        }
    }
    public String getMessage(String key) {
        return bundle.getString(key);
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
    public void setForward(String forward) {
        this.forward = forward;
    }
    public String getForward() {
        return forward;
    }
}
