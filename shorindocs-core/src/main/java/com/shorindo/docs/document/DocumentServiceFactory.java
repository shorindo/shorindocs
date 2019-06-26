/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.docs.document;

import static com.shorindo.docs.document.DocumentMessages.*;

import java.util.HashMap;
import java.util.Map;

import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.RepositoryService;

/**
 * 
 */
public abstract class DocumentServiceFactory {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(DocumentServiceFactory.class);
    private static final Map<String,ActionController> controllerMap =
            new HashMap<String,ActionController>();
    private static final Map<String,ActionController> classMap =
            new HashMap<String,ActionController>();
    private static final RepositoryService repositoryService =
            ServiceFactory.getService(RepositoryService.class);

    /**
     * 
     * @param path
     * @param clazz
     */
    public static synchronized void addController(String path, Class<? extends ActionController> clazz) {
        try {
            controllerMap.put(path, clazz.newInstance());
        } catch (InstantiationException e) {
            LOG.error(DOCS_9004, e, path);
        } catch (IllegalAccessException e) {
            LOG.error(DOCS_9004, e, path);
        }
    }

    /**
     * 
     * @param path
     * @return
     */
    public static synchronized ActionController getController(String path) {
        ActionController controller = controllerMap.get(path);
        if (controller == null) {
            try {
                DocumentEntity key = new DocumentEntity();
                key.setDocumentId(path.substring(1));
                key.setVersion(0);
                DocumentEntity entity = repositoryService.get(key);
                if (classMap.containsKey(entity.getController())) {
                    controller = classMap.get(entity.getController());    
                } else {
                    Class<?> clazz = Class.forName(entity.getController());
                    controller = (ActionController)clazz.newInstance();
                    classMap.put(entity.getController(), controller);
                }
            } catch (RepositoryException e) {
                LOG.error(DOCS_9999, e);
            } catch (ClassNotFoundException e) {
                LOG.error(DOCS_9999, e);
            } catch (InstantiationException e) {
                LOG.error(DOCS_9999, e);
            } catch (IllegalAccessException e) {
                LOG.error(DOCS_9999, e);
            }
        }
        return controller;
    }
}
