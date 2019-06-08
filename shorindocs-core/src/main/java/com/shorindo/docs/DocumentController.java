/*
 * Copyright 2016-2018 Shorindo, Inc.
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

import java.sql.SQLException;
import java.util.List;

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentController.class);
    private static final RepositoryService repositoryService = RepositoryServiceFactory.repositoryService();
    private DocumentEntity model;

    public static void setup(List<Class<?>> clazzList) {
        for (Class<?> clazz : clazzList) {
            LOG.info(DOCS_1120, clazz.getName());
        }
    }

    /**
     * 
     */
    protected DocumentEntity getModel() {
        return model;
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View save(ActionContext context) throws DocumentException {
        DocumentEntity model = getModel();
        model.setTitle(context.getParameter("title"));
        model.setContent(context.getParameter("body"));
        String id = model.getDocumentId();
        try {
            int result = repositoryService.put(model);
            if (result > 0) {
                return new RedirectView(id + "?action=view", context);
            } else {
                return new ErrorView(404);
            }
        } catch (DatabaseException e) {
            LOG.error(DOCS_9002, id);
            return new ErrorView(500);
        }
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View create(ActionContext context) throws DocumentException {
        String id = String.valueOf(IdentityProvider.newId());

        try {
            DocumentEntity model = new DocumentEntity();
            model.setDocumentId(id);
            model.setController(getClass().getName());
            model.setTitle(context.getParameter("title"));
            model.setContent(context.getParameter("body"));

            if (repositoryService.put(model) >= 0) {
                return new RedirectView(id + "?action=edit", context);
            } else {
                return new ErrorView(404);
            }
        } catch (DatabaseException e) {
            LOG.error(DOCS_9002, e, id);
            return new ErrorView(500);
        }
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View remove(ActionContext context) throws DocumentException {
        DocumentEntity model = getModel();
        String id = model.getDocumentId();
        if ("index".equals(id)) {
            return new RedirectView("/index", context);
        } else {
            try {
                if (repositoryService.remove(model) > 0) {
                    return new RedirectView("/index", context);
                } else {
                    LOG.error(DOCS_9003, id);
                    return new ErrorView(500);
                }
            } catch (DatabaseException e) {
                LOG.error(DOCS_9003, e, id);
                return new ErrorView(500);
            }
        }
    }

    /**
     * 
     * @return
     * @throws SQLException
     */
    protected List<DocumentEntity> recents() throws DatabaseException {
        return repositoryService.query(
              "SELECT document_id,title,update_date " +
              "FROM   docs_document " +
              "ORDER  BY update_date DESC " +
              "LIMIT  10",
              DocumentEntity.class);
    }
    
}
