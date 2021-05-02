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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String,String[]> paramMap;
    private Map<String,Object> model;

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

    public String getParameter(String name) {
        return paramMap.containsKey(name) ?
            paramMap.get(name)[0] : null;
    }
    public String[] getParameters(String name) {
        return paramMap.get(name);
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void addModel(String name, Object value) {
        model.put(name, value);
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
}
