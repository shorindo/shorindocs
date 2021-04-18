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
package com.shorindo.docs.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.action.ActionPlugin;
//import com.shorindo.docs.admin.AdminPlugin;
import com.shorindo.docs.auth.AuthenticatePlugin;
//import com.shorindo.docs.datagrid.DataGridPlugin;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentPlugin;
//import com.shorindo.docs.markdown.MarkdownPlugin;
//import com.shorindo.docs.outlogger.OutloggerPlugin;
//import com.shorindo.docs.plaintext.PlainTextPlugin;
import com.shorindo.docs.plugin.PluginService;
import com.shorindo.docs.plugin.PluginServiceImpl;
import com.shorindo.docs.repository.RepositoryDataSource;
import com.shorindo.docs.repository.RepositoryPlugin;
//import com.shorindo.docs.specout.SpecoutPlugin;

/**
 * 
 */
public class ActionListener implements ServletContextListener {
	private static final ActionLogger LOG = ActionLogger.getLogger(ActionListener.class);

    /**
     * 
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
        	File base = new File(event.getServletContext().getRealPath("/WEB-INF"));
            File xml = new File(base, "application-config.xml");
            ApplicationContext.load(xml);
        } catch (IOException e) {
        	// FIXME 初期化に失敗したらうまくログが出ない
            LOG.error(DocumentMessages.DOCS_9000, e);
        }

        //XumlView.init(event.getServletContext().getRealPath("/WEB-INF/classes"));

        PluginService pluginService = ApplicationContext.getBean(PluginService.class);
        pluginService.findPlugin(new File(event.getServletContext().getRealPath("/WEB-INF/lib")));
    }

    /**
     * 
     */
    public void contextDestroyed(ServletContextEvent event) {
    }

}
