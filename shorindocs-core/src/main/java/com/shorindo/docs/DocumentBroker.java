/*
 * Copyright 2018 Shorindo, Inc.
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

import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/*")
public final class DocumentBroker extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentBroker.class);
    private static final RepositoryService repositoryService = RepositoryServiceFactory.repositoryService();

    public static ActionController getController(DocumentEntity model) throws DocumentException {
        try {
            String controller = model.getController();
            return (ActionController)Class.forName(controller).newInstance();
        } catch (Exception e) {
            throw new DocumentException(DOCS_9999, e);
        }
    }

    public static DocumentEntity getDocumentModel(String id) throws DatabaseException {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(id);
        return repositoryService.get(entity);
    }

    @Override
    public Object action(ActionContext context) {
        try {
            String id = ((String)context.getAttribute("requestPath")).substring(1);
            DocumentEntity model = getDocumentModel(id);
            if (model != null) {
                context.setAttribute("document", model);
                return getController(model).action(context);
            } else {
                LOG.warn(DOCS_3006, id);
                return null;
            }
        } catch (DocumentException e) {
            LOG.error(DOCS_5008, e);
            return null;
        } catch (DatabaseException e) {
            LOG.error(DOCS_5008, e);
            return null;
        }
    }

    /**
     *
     */
    @Override
    public View view(ActionContext context) {
        context.setAction("view");
        return (View)action(context);
    }

}
