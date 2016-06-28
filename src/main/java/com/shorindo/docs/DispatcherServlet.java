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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.docs.view.XumlView;

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
            ContentHandler handler = ContentHandler.getHandler(id);
            AbstractView view = handler.action(action, new Properties());
            view.setAttribute("application", req.getSession().getServletContext());
            view.setAttribute("request", req);
            view.setAttribute("response", res);
            view.setAttribute("session", req.getSession());
            res.setContentType(view.getContentType());
            byte[] b = new byte[4096];
            int len;
            InputStream is = new ByteArrayInputStream(view.getContent().getBytes("UTF-8"));
            while ((len = is.read(b)) > 0) {
                res.getOutputStream().write(b, 0, len);
            }
            is.close();
        } catch (ContentException e) {
            LOG.error(e.getMessage(), e);
            res.setStatus(500);
        }
    }

}
