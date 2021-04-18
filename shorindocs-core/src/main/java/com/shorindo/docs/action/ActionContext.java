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

import javax.servlet.http.HttpServletRequest;

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

    private ActionContext() {
    	paramMap = new HashMap<>();
    }

    public static ActionContextBuilder builder() {
    	return new ActionContextBuilder();
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
    	return null;
    }
    public String[] getParameters(String name) {
    	return null;
    }

    public static class ActionContextBuilder {
    	HttpServletRequest req;
		ActionContext actionContext;
    	private ActionContextBuilder() {
    		actionContext = new ActionContext();
    	}

    	public ActionContext build() {
    		return actionContext;
    	}
    	public ActionContextBuilder path(String path) {
    		actionContext.path = path;
    		return this;
    	}
    	public ActionContextBuilder contextPath(String contextPath) {
    		actionContext.contextPath = contextPath;
    		return this;
    	}
    	public ActionContextBuilder method(String method) {
    		actionContext.method = method;
    		return this;
    	}
    	public ActionContextBuilder queryString(String queryString) {
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
    					actionContext.paramMap.put(
    							e.getKey(),
    							e.getValue().toArray(new String[] {}));
    				});
    		}
    		return this;
    	}
    	public ActionContextBuilder contentType(String contentType) {
    		actionContext.contentType = contentType;
    		return this;
    	}
    }
}
