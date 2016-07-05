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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.docs.xuml.XumlView;

/**
 * 
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DispatcherServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getServletPath().substring(1);
        String ext = req.getServletPath().replaceAll("^(.*?)(\\.([^\\.]+))?$", "$3");
        LOG.debug("service(" + id + ")");
        if ("xuml".equals(ext)) {
            doXuml(req, res);
            return;
        }
        if (ext != null && !"".equals(ext)) {
            RequestDispatcher dispatcher = req.getSession().getServletContext().getNamedDispatcher("default");
            dispatcher.forward(req, res);
            return;
        }
        String action = req.getParameter("action");
        if (action == null || "".equals(action)) {
            action = "view";
        }
        try {
            ActionMessage message = new ActionMessage(req);
            DocumentController handler = DocumentController.getHandler(id);
            handler.action(action, message);
            for (Entry<String,Object> entry : message.getAttributes().entrySet()) {
                req.setAttribute(entry.getKey(), entry.getValue());
            }
            req.getRequestDispatcher(message.getForward()).forward(req, res);
        } catch (ContentException e) {
            LOG.error(e.getMessage(), e);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void doXuml(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        InputStream is = getClass().getResourceAsStream(req.getServletPath());
        try {
            ActionMessage message = new ActionMessage(req);
            XumlView view = new XumlView(message, is);
            message.setAttribute("application", req.getSession().getServletContext());
            res.setContentType(view.getContentType());
            res.getOutputStream().write(view.getContent().getBytes());
        } finally {
            is.close();
        }
    }
}
