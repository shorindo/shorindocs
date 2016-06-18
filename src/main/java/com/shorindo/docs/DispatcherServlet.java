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
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        String path = req.getPathInfo();
        if (path.endsWith(".jsp")) {
            RequestDispatcher dispatcher = req.getServletContext().getNamedDispatcher("jsp");
            dispatcher.forward(req, res);
        } else {
            System.out.println("path=" + path);
            String id = path.substring(1);
            try {
                ContentModel model = DatabaseManager.selectOne(ContentModel.class, "SELECT * FROM CONTENT WHERE CONTENT_ID=?", id);
                ContentHandler handler = ContentHandler.getHandler(model);
                req.setAttribute("content", handler.view(new Properties()));
                req.getRequestDispatcher("/view.jsp").forward(req, res);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
