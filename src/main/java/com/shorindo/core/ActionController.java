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

import java.lang.reflect.Method;

import com.shorindo.core.annotation.ActionReady;
import com.shorindo.core.view.View;

/**
 * 
 */
public abstract class ActionController {
    private static final Logger LOG = Logger.getLogger(ActionController.class);

    public abstract View view(ActionContext context);

    public final View action(String name, ActionContext context) {
        try {
            if (name == null || "".equals(name)) {
                name = "view";
            }
            Method method = getClass().getMethod(name, ActionContext.class);
            if (method.getAnnotation(ActionReady.class) != null &&
                    View.class.isAssignableFrom(method.getReturnType())) {
                return (View)method.invoke(this, context);
            } else {
                LOG.warn("no suitable method '" + name + "' exists");
                return view(context);
            }
        } catch (Exception e) {
            LOG.warn("no suitable method '" + name + "' exists:" + e.getMessage());
            return view(context);
        }
    }
}
