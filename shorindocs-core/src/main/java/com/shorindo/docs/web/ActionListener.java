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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.plugin.PluginService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

/**
 * 
 */
public class ActionListener implements ServletContextListener {
	private static final ActionLogger LOG = ActionLogger.getLogger(ActionListener.class);

    /**
     * 
     */
    public void contextInitialized(ServletContextEvent event) {
        String configFile = event.getServletContext().getInitParameter("config");
        LOG.debug("config = {0}", configFile);
        if (configFile.startsWith("classpath:")) {
            try {
                String path = configFile.substring("classpath:".length());
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);
                ApplicationContext.init(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            File file = new File(event.getServletContext().getRealPath(configFile));
            try (InputStream is = new FileInputStream(file)) {
                ApplicationContext.init(is);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // プラグインを探す
        ApplicationContext.getBean(PluginService.class)
            .findPlugin(new File(event.getServletContext().getRealPath("/WEB-INF/lib")));

        // プラグインのDocumentControllerを登録する
        for (Entry<String,ApplicationContext> plugin : ApplicationContext.getPlugins().entrySet()) {
            for (DocumentController controller : plugin.getValue().findBeans(DocumentController.class)) {
                DocumentController.addController(plugin.getKey(), controller);
            }
        }
    }

    /**
     * 
     */
    public void contextDestroyed(ServletContextEvent event) {
    }

}
