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
package com.shorindo.docs;

import static com.shorindo.docs.DocumentMessages.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;

/**
 * 
 */
public class DocumentControllerFactory {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(DocumentControllerFactory.class);
    private static final Map<String,ActionController> controllerMap =
            new HashMap<String,ActionController>();
    private static final Map<String,ActionController> classMap =
            new HashMap<String,ActionController>();
    private static final RepositoryService repositoryService =
            RepositoryServiceFactory.repositoryService();

    public static synchronized void addController(String path, Class<? extends ActionController> clazz) {
        try {
            controllerMap.put(path, clazz.newInstance());
        } catch (InstantiationException e) {
            LOG.error(DOCS_9004, e, path);
        } catch (IllegalAccessException e) {
            LOG.error(DOCS_9004, e, path);
        }
    }

    public static synchronized ActionController getController(String path) {
        ActionController controller = controllerMap.get(path);
        if (controller == null) {
            try {
                List<DocumentEntity> entityList = repositoryService.query(
                        "SELECT CONTROLLER FROM DOCS_DOCUMENT" +
                        "WHERE DOCUMENT_ID=? AND VERSION=0",
                        DocumentEntity.class,
                        path.substring(1));
                if (entityList.size() > 0) {
                    DocumentEntity entity = entityList.get(0);
                    if (classMap.containsKey(entity.getController())) {
                        controller = classMap.get(entity.getController());    
                    } else {
                        Class<?> clazz = Class.forName(entity.getController());
                        controller = (ActionController)clazz.newInstance();
                        classMap.put(entity.getController(), controller);
                    }
                } else {
                    LOG.warn(DOCS_5010, path);
                }
            } catch (DatabaseException e) {
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
