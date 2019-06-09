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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.shorindo.docs.auth.entity.UserEntity;

/**
 * 
 */
public class ActionContext {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionContext.class);
    private ViewModel viewModel;
    private String action = "view";
    private Locale locale;
    private ResourceBundle bundle;

    public ActionContext() {        
        bundle = ResourceBundle.getBundle("messages", Locale.JAPANESE /*req.getLocale()*/);
        Map<String,String> application = new HashMap<String,String>();
        this.setAttribute("application", application);
    }

    public UserEntity getUser() {
        return new UserEntity();
    }

    public ViewModel getViewModel() {
        return viewModel;
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
//    public String getParameter(String key) {
//        //LOG.debug("getParameter(" + key + ")=>" + request.getParameter(key));
////        return request.getParameter(key);
//        return null;
//    }
}
