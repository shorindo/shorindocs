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
package com.shorindo.docs;

import static com.shorindo.docs.DocumentMessages.*;
import java.lang.reflect.Method;

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.ErrorView;
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
        LapCounter lap = new LapCounter();
        LOG.debug(DOCS_1107, getClass().getSimpleName() + ".action()");
        try {
            Class<?> clazz = getClass();
            while (clazz != null) {
                Method method = clazz.getMethod(context.getAction(), ActionContext.class);
                if (method.getAnnotation(ActionMethod.class) != null &&
                        View.class.isAssignableFrom(method.getReturnType())) {
                    View view = (View)method.invoke(this, context);
                    LOG.debug(DOCS_1108, getClass().getSimpleName() + ".action()", lap.elapsed());
                    return view;
                }
                clazz = clazz.getSuperclass();
            }
            LOG.warn(DOCS_3003, context.getAction());
            return view(context);
        } catch (Exception e) {
            LOG.error(DOCS_3003, e, context.getAction());
            return new ErrorView(500);
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
