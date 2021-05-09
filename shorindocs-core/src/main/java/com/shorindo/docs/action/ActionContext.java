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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shorindo.docs.model.UserModel;

/**
 * 
 */
public class ActionContext {
    @SuppressWarnings("unused")
	private static final ActionLogger LOG = ActionLogger.getLogger(ActionContext.class);
    private String path;
    private String contextPath;
    private String method;
    private String contentType;
    private Map<String,Object> paramMap;
    private Map<String,Object> model;
    private UserModel user;

    public ActionContext() {
        paramMap = new HashMap<>();
        model = new HashMap<>();
    }

    public String getPath() {
        return path;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }

    public Object getParameter(String name) {
        return paramMap.get(name);
    }

    public String getParameterAsString(String name) {
        Object param = paramMap.get(name);
        if (param == null) {
            return null;
        } else if (Iterable.class.isAssignableFrom(param.getClass())) {
            return ((Iterable<?>)param).iterator().next().toString();
        } else if (param.getClass().isArray()) {
            return Array.get(param, 0).toString();
        } else {
            return param.toString();
        }
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void addModel(String name, Object value) {
        model.put(name, value);
    }

    public UserModel getUser() {
        return user;
    }

    public ActionContext path(String path) {
        this.path = path;
        return this;
    }
    public ActionContext contextPath(String contextPath) {
        this.contextPath = contextPath;
        addModel("context.path", contextPath);
        return this;
    }
    public ActionContext method(String method) {
        this.method = method;
        return this;
    }
    public ActionContext queryString(String queryString) {
        if (queryString != null) {
            Map<String,List<String>> map = new HashMap<>();
            for (String param : queryString.split("&")) {
                String keyval[] = param.split("=", 2);
                if (keyval.length < 2) {
                    continue;
                }
                List<String> vals = map.get(keyval[0]);
                if (vals == null) {
                    vals = new ArrayList<>();
                    map.put(keyval[0], vals);
                }
                vals.add(keyval[1]);
            }
            map.entrySet().stream()
            .forEach((e) -> {
                paramMap.put(
                    e.getKey(),
                    e.getValue().toArray(new String[] {}));
            });
        }
        return this;
    }
    public ActionContext contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
    public ActionContext user(UserModel user) {
        this.user = user;
        return this;
    }
    public ActionContext paramMap(Map<String,Object> paramMap) {
        this.paramMap = paramMap;
        return this;
    }
}
