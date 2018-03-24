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

import static com.shorindo.docs.DocsMessages.*;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public abstract class ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionController.class);

    public ActionController() {
    }

    @ActionMethod
    public abstract String view(ActionContext context);

    public View action(ActionContext context) {
        long st = System.currentTimeMillis();
        LOG.debug(DOCS_1107, getClass().getSimpleName() + ".action()");
        try {
            Class<?> clazz = getClass();
            while (clazz != null) {
                Method method = clazz.getDeclaredMethod(context.getAction(), ActionContext.class);
                if (method.getAnnotation(ActionMethod.class) != null &&
                        String.class.isAssignableFrom(method.getReturnType())) {
                    LOG.debug(DOCS_1108, getClass().getSimpleName() + ".action()", (System.currentTimeMillis() - st));
                    return getView((String)method.invoke(this, context), context);
                }
                clazz = clazz.getSuperclass();
            }
            LOG.warn(DOCS_3003, context.getAction());
            return getView(view(context), context);
        } catch (Exception e) {
            LOG.error(DOCS_3003, e, context.getAction());
            return new ErrorView(500);
        }
    }

    protected View getView(String viewName, ActionContext context) {
        if (viewName == null) {
            return new ErrorView(404);
        } else if (".xuml".equals(viewName)) {
            InputStream is = getClass().getResourceAsStream(getClass().getSimpleName() + viewName);
            return new XumlView(getClass().getSimpleName() + viewName, is);
        } else if (viewName.startsWith("/")) {
            return new RedirectView(viewName, context);
        } else {
            return new ErrorView(404);
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
