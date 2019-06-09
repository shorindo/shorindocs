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

import com.shorindo.docs.admin.AdminSettings;
import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.auth.AuthenticateSettings;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.outlogger.OutloggerSettings;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;

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
            RepositoryServiceFactory.repositoryService();

    private static DocumentService documentService;
    private static ApplicationContext applicationContext;

    public static synchronized DocumentService documentService() {
        if (documentService == null) {
            documentService = new DocumentService();
        }
        return documentService;
    }

    /*
     * 
     */
    public static synchronized ApplicationContext applicationContext() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        return applicationContext;
    }

    public static synchronized void init() {
        // FIXME
        addSettings(AuthenticateSettings.class);
        addSettings(AdminSettings.class);
        addSettings(DocumentSettings.class);
        addSettings(OutloggerSettings.class);
    }

    public static void addSettings(Class<? extends PluginSettings> settingsClass) {
        try {
            PluginSettings settings = settingsClass.newInstance();

            for (ActionController controller : settings.getControllers()) {
                Class<?> clazz = controller.getClass();
                ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
                if (mapping != null && ActionController.class.isAssignableFrom(clazz)) {
                    LOG.info(DOCS_0001, mapping.value(), clazz);
                    controllerMap.put(mapping.value(), controller);
                }
            }

            for (DatabaseSchema schema : settings.getSchemas()) {
                repositoryService.validateSchema(schema);
            }
        } catch (InstantiationException e) {
            LOG.error(DOCS_9999, e);
        } catch (IllegalAccessException e) {
            LOG.error(DOCS_9999, e);
        } catch (DatabaseException e) {
            LOG.error(DOCS_9999, e);
        }
    }

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
                List<DocumentEntity> entityList = repositoryService.query(
                        "SELECT CONTROLLER FROM DOCS_DOCUMENT " +
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
