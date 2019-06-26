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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.InputStream;

import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.document.DocumentServiceFactory;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.RepositoryService;

/**
 * 
 */
public abstract class Plugin {
    private static ActionLogger LOG =
            ActionLogger.getLogger(Plugin.class);

    public abstract void initialize();

    public static final void addPlugin(Class<? extends Plugin> clazz) {
        try {
            Plugin plugin = clazz.newInstance();
            plugin.initialize();
        } catch (InstantiationException e) {
            LOG.error(DOCS_9999, e);
        } catch (IllegalAccessException e) {
            LOG.error(DOCS_9999, e);
        }
    }
    
    protected final void addSchema(InputStream is) {
        RepositoryService service = ServiceFactory.getService(RepositoryService.class);
        try {
            DatabaseSchema schema = service.loadSchema(is);
            service.validateSchema(schema);
        } catch (RepositoryException e) {
            LOG.error(DOCS_9999, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected final void addController(Class<?> clazz) {
        ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
        if (mapping != null && ActionController.class.isAssignableFrom(clazz)) {
            LOG.info(DOCS_0001, mapping.value(), clazz);
            DocumentServiceFactory.addController(
                    mapping.value(),
                    (Class<ActionController>)clazz);
        }
    }

    protected final <T> void addService(Class<T> itfc, Class<? extends T> impl) {
        ServiceFactory.addService(itfc, impl);
    }
}
