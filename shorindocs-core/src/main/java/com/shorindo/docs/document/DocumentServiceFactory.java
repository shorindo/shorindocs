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

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionError;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public abstract class DocumentServiceFactory {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(DocumentServiceFactory.class);
    private static final Map<String,ActionController> controllerMap = new HashMap<>();
    private static final Map<String,ActionController> classMap = new HashMap<>();

    /**
     * 
     * @param path
     * @param clazz
     */
//    public static synchronized void addController(String path, Class<? extends ActionController> clazz) {
//    	ApplicationContext.addBean(clazz);
//    	controllerMap.put(path, ApplicationContext.getBean(clazz));
//    }

    public static synchronized ActionController getController(DocumentModel model) throws ActionError {
        if (classMap.containsKey(model.getController())) {
            return classMap.get(model.getController());    
        } else {
            try {
                Class<?> clazz = Class.forName(model.getController());
                ActionController controller = (ActionController)ApplicationContext.getBean(clazz);
                return controller;
            } catch (Exception e) {
                throw new ActionError(DOCS_9999, e);
            }
        }
    }

    /**
     * 
     * @param path
     * @return
     */
    public static synchronized ActionController getController(String path) {
    	return controllerMap.get(path);
    }
}
