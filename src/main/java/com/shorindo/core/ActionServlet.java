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

import com.shorindo.core.Messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.core.ActionContext;
import com.shorindo.core.DocsLogger;
import com.shorindo.core.ClassFinder.ClassMatcher;
import com.shorindo.core.annotation.ActionMapping;
import com.shorindo.core.view.DefaultView;
import com.shorindo.core.view.ErrorView;
import com.shorindo.core.view.View;

/**
 * 
 */
public class ActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final DocsLogger LOG = DocsLogger.getLogger(ActionServlet.class);
    private static final Map<String,Class<?>> actionMap = new HashMap<String,Class<?>>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        LOG.debug("init() start");
        super.init(config);

        File root = new File(config.getServletContext().getRealPath("/WEB-INF/classes"));
        ClassFinder.find(root, new ClassMatcher() {
            public boolean matches(Class<?> clazz) {
                ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
                if (mapping != null && ActionController.class.isAssignableFrom(clazz)) {
                    LOG.info(Messages.I_0001, mapping.value(), clazz);
                    actionMap.put(mapping.value(), clazz);
                    return true;
                } else {
                    return false;
                }
            }
        });

        LOG.debug("init() end");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        ActionContext context = new ActionContext(req, res, getServletContext());
        if (!dispatch(context)) {
            output(res, new ErrorView(404, context));
        }
    }

    protected boolean dispatch(ActionContext context)
            throws ServletException, IOException {
        HttpServletRequest req = context.getRequest();
        HttpServletResponse res = context.getResponse();
        File file = new File(getServletContext().getRealPath(req.getServletPath()));
        Class<?> action = actionMap.get(req.getServletPath());
        LOG.debug("path=" + action);

        if (action != null) {
            try {
                output(res, ((ActionController)action.newInstance()).action(context));
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        } else if (file.exists()) {
            output(res, new DefaultView(file, context));
            return true;
        } else {
            return false;
        }
    }

    protected final void output( HttpServletResponse res, View view) {
        for (Entry<String,String> entry : view.getOptions().entrySet()) {
            res.setHeader(entry.getKey(), entry.getValue());
        }
        res.setStatus(view.getStatus());
        res.setContentType(view.getContentType());
        int len = 0;
        byte[] buf = new byte[4096];
        InputStream is = view.getContent();
        try {
            while ((len = is.read(buf)) > 0) {
                res.getOutputStream().write(buf, 0, len);
            }
        } catch (IOException e) {
            LOG.error(Messages.E_9999, e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(Messages.E_9999, e);
                }
        }
    }
}
