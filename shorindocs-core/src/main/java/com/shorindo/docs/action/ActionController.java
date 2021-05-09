/*
 * Copyright 2016-2018 Shorindo, Inc.
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

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.View;

/**
 * 
 */
public abstract class ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionController.class);

    @ActionMethod
    public abstract View action(ActionContext context, Object...args);

    public String getAction(ActionContext context) {
        Object params = context.getParameter("action");
        if (params == null) {
            return "view";
        } else {
            return params.toString();
        }
    }

//    protected String createClassPath(String path) {
//        StringBuffer result = new StringBuffer();
//        result.append(getClass().getPackage().getName().replaceAll("\\.", "/"));
//        if (!path.startsWith("/")) {
//            result.append("/");
//        }
//        result.append(path);
//        return result.toString();
//    }

}
