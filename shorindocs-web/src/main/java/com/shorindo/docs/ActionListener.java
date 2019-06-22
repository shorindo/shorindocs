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
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.AuthenticatePlugin;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentPlugin;
import com.shorindo.docs.document.DocumentServiceFactory;
import com.shorindo.docs.outlogger.OutloggerPlugin;
import com.shorindo.docs.repository.RepositoryPlugin;
import com.shorindo.docs.specout.SpecoutPlugin;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ActionListener implements ServletContextListener {
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionListener.class);

    public void contextInitialized(ServletContextEvent event) {
        LOG.trace("contextInitialized()");
        InputStream is = null;
        try {
            Properties siteProperties = new Properties();
            String path = event.getServletContext().getRealPath("/WEB-INF/site.properties");
            is = new FileInputStream(path);
            siteProperties.load(is);
            PropertyConfigurator.configure(siteProperties);

            ApplicationContext.init(siteProperties);
        } catch (IOException e) {
            LOG.error(DocumentMessages.DOCS_9000, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                LOG.error(DocumentMessages.DOCS_9000, e);
            }
        }

        Plugin.addPlugin(RepositoryPlugin.class);
        Plugin.addPlugin(AuthenticatePlugin.class);
        Plugin.addPlugin(DocumentPlugin.class);
        Plugin.addPlugin(OutloggerPlugin.class);
        Plugin.addPlugin(SpecoutPlugin.class);
//        ServiceFactory.addService(RepositoryService.class, RepositoryServiceImpl.class);
//        ServiceFactory.addService(DocumentService.class, DocumentServiceImpl.class);
        DocumentServiceFactory.init();
        XumlView.init(event.getServletContext().getRealPath("/WEB-INF/classes"));
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

}
