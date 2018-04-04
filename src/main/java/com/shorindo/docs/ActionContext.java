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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.docs.auth.entity.UserEntity;

/**
 * 
 */
public class ActionContext {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionContext.class);
    private ViewModel viewModel;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;
    private Locale locale;
    private ResourceBundle bundle;

    public ActionContext(HttpServletRequest req, HttpServletResponse res, ServletContext ctx) {
        viewModel = new ViewModel(req);
        request = req;
        response = res;
        servletContext = ctx;
        locale = req.getLocale();
        bundle = ResourceBundle.getBundle("messages", req.getLocale());
        this.setAttribute("request", request);
        this.setAttribute("response", response);
        this.setAttribute("session", request.getSession());
        Map<String,String> application = new HashMap<String,String>();
        application.put("contextPath", servletContext.getContextPath());
        application.put("info", servletContext.getServerInfo());
        this.setAttribute("application", application);
    }

    public UserEntity getUser() {
        return new UserEntity();
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getApplication() {
        return servletContext;
    }

    public String getAction() {
        String action = getParameter("action");
        if (action == null || "".equals(action)) {
            return "view";
        } else {
            return action;
        }
    }

    public String getFormat() {
        String format = getParameter("format");
        if (format == null || "".equals(format)) {
            return "html";
        } else {
            return format;
        }
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

    public Map<String,Object> getAttributes() {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        for (Enumeration<?> e = request.getAttributeNames(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            resultMap.put(name, request.getAttribute(name));
        }
        return resultMap;
    }
    public void setAttribute(String key, Object value) {
        request.setAttribute(key, value);
    }
    public Object getAttribute(String key) {
        return request.getAttribute(key);
    }
    public String getParameter(String key) {
        //LOG.debug("getParameter(" + key + ")=>" + request.getParameter(key));
        return request.getParameter(key);
    }
}
