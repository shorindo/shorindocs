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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.apache.log4j.PropertyConfigurator;

/**
 * 
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DispatcherServlet.class);
    private static final Properties siteProperties = new Properties();

    @Override
    public void init(ServletConfig config) throws ServletException {
        String path = config.getServletContext().getRealPath("/WEB-INF/site.properties");
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            siteProperties.load(is);
            PropertyConfigurator.configure(siteProperties);
            DatabaseManager.init(siteProperties);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getServletPath().substring(1);
        String ext = req.getServletPath().replaceAll("^(.*?)(\\.([^\\.]+))?$", "$3");
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
            if (handler == null) {
                res.setStatus(500);
                return;
            }
            req.setAttribute("title", handler.getModel().getTitle());
            req.setAttribute("search", handler.search());
            req.setAttribute("content", handler.action(action, new Properties()));
            for (Entry<String,Object> entry : handler.getAttributes().entrySet()) {
                req.setAttribute(entry.getKey(), entry.getValue());
            }
            req.getRequestDispatcher("/jsp/layout.jsp").forward(req, res);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
