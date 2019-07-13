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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSON;

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

    public Object action(ActionContext context) {
        LOG.debug(DOCS_1107, getClass().getSimpleName() + ".action()");
        try {
            String actionName = context.getAction();
            Object reqParams = context.getParameter();
            int paramSize = reqParams instanceof List ?
                    ((List<?>)reqParams).size() : 0;
            Class<?> clazz = getClass();
            while (clazz != null) {
                for (Method method : clazz.getMethods()) {
                    if (!method.getName().equals(actionName) ||
                            paramSize != method.getParameterCount()) {
                        continue;
                    }
                    List<Object> callParams = new ArrayList<Object>();
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        Parameter decParam = method.getParameters()[i];
                        callParams.add(
                                JSON.decode(JSON.encode(reqParams), decParam.getClass()));
                    }
                    return method.invoke(this, callParams.toArray());
                }
//                Method method = clazz.getMethod(context.getAction(), ActionContext.class);
//                if (method.getAnnotation(ActionMethod.class) != null) {
//                    Object result = method.invoke(this, context);
//                    LOG.debug(DOCS_1108, getClass().getSimpleName() + ".action()", lap.elapsed());
//                    return result;
//                }
                clazz = clazz.getSuperclass();
            }
            LOG.warn(DOCS_3003, context.getAction());
        } catch (Throwable th) {
            LOG.error(DOCS_3003, th, context.getAction());
        }
        return null;
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
