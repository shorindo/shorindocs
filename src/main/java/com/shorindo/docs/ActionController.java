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

import java.lang.reflect.Method;

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.View;

/**
 * 
 */
public abstract class ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionController.class);

    public ActionController() {
    }

    @ActionMethod
    public abstract View view(ActionContext context);

    public View action(ActionContext context) {
        LOG.debug(this.getClass().getSimpleName() + ".action()");
        try {
            Class<?> clazz = getClass();
            while (clazz != null) {
                Method method = clazz.getDeclaredMethod(context.getAction(), ActionContext.class);
                if (method.getAnnotation(ActionMethod.class) != null &&
                        View.class.isAssignableFrom(method.getReturnType())) {
                    return (View)method.invoke(this, context);
                }
                clazz = clazz.getSuperclass();
            }
            LOG.warn(ActionMessages.W1003, context.getAction());
            return view(context);
        } catch (Exception e) {
            LOG.warn(ActionMessages.W1003, context.getAction());
            return view(context);
        }
    }

    protected String createClassPath(String path) {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getPackage().getName().replaceAll("\\.", "/"));
        if (!path.startsWith("/")) {
            result.append("/");
        }
        result.append(path);
        return result.toString();
    }

}
