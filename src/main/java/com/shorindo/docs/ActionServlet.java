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

import static com.shorindo.docs.DocumentMessages.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ResourceFinder.ResourceMatcher;
import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.view.DefaultView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionServlet.class);
    private static final ActionMapper actionMap = new ActionMapper();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        File root = new File(config.getServletContext().getRealPath("/WEB-INF/classes"));
        ResourceFinder.find(root, new ResourceMatcher() {
            public boolean matches(Class<?> clazz) {
                ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
                if (mapping != null && ActionController.class.isAssignableFrom(clazz)) {
                    LOG.info(DOCS_0001, mapping.value(), clazz);
                    try {
                        actionMap.put(mapping.value(), (ActionController)clazz.newInstance());
                    } catch (InstantiationException e) {
                        LOG.error(DOCS_9004, e, mapping.value());
                    } catch (IllegalAccessException e) {
                        LOG.error(DOCS_9004, e, mapping.value());
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        LOG.info(DOCS_1105, req.getServletPath());
        long st = System.currentTimeMillis();
        String path = req.getServletPath();
        String id = path.substring(1);
        ActionContext context = new ActionContext(req, res, getServletContext());

        if (id == null || "".equals(id)) {
            output(context, res, new RedirectView("/index", context));
            return;
        }
        File file = new File(req.getSession().getServletContext().getRealPath(id));
        if (id.endsWith(".xuml") && file.exists()) {
            View view = new XumlView(file.getName(), new FileInputStream(file));
            output(context, res, view);
        } else if (!dispatch(context)) {
            LOG.error(DOCS_5003, path);
        }
        LOG.info(DOCS_1106, req.getServletPath(),
                (System.currentTimeMillis() - st));
    }

    protected boolean dispatch(ActionContext context)
            throws ServletException, IOException {
        HttpServletRequest req = context.getRequest();
        HttpServletResponse res = context.getResponse();
        File file = new File(getServletContext().getRealPath(req.getServletPath()));
        ActionController controller = actionMap.get(req.getServletPath());

        if (file.exists()) {
            output(context, res, new DefaultView(file, context));
            return true;
        } else if (controller != null) {
            output(context, res, controller.action(context));
            return true;
        } else {
            return false;
        }
    }

    protected final void output(ActionContext context, HttpServletResponse res, View view) throws IOException {
        for (Entry<String,String> entry : view.getMeta().entrySet()) {
            res.setHeader(entry.getKey(), entry.getValue());
        }
        res.setStatus(view.getStatus());
        res.setContentType(view.getContentType());
        view.render(context, res.getOutputStream());
    }
}
