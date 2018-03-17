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

import java.sql.Connection;
import java.sql.SQLException;

import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseExecutor;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.Transactionless;
import com.shorindo.docs.form.FormController;
import com.shorindo.docs.form.TemplateController;
import com.shorindo.docs.plaintext.PlainTextController;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/*")
public final class DocumentBroker extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentBroker.class);
    private static final DatabaseService databaseService = DatabaseService.newInstance();

    public static ActionController getController(DocumentEntity model) throws DocumentException {
        try {
            if ("text/plain".equals(model.getContentType())) {
                return new PlainTextController();
            } else if ("application/x-form".equals(model.getContentType())) {
                return new FormController();
            } else if ("application/x-form-template".equals(model.getContentType())) {
                return new TemplateController();
            } else {
                throw new DocumentException("controller not found:" + model.getContentType());
            }
        } catch (Exception e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    public static DocumentEntity getDocumentModel(String id) throws DatabaseException {
        return databaseService.provide(GET_DOCUMENT_EXEC, id);
    }
    private static DatabaseExecutor<DocumentEntity> GET_DOCUMENT_EXEC =
            new Transactionless<DocumentEntity>() {
        @Override
        public DocumentEntity run(Connection conn, Object...params) throws DatabaseException {
            DocumentEntity model = new DocumentEntity();
            model.setDocumentId((String)params[0]);
            return get(model);
        }
    };

    @Override
    public View action(ActionContext context) {
        LOG.debug("action()");
        try {
            String id = context.getRequest().getServletPath().substring(1);
            DocumentEntity model = getDocumentModel(id);
            context.setAttribute("document", model);
            return getController(model).action(context);
        } catch (DocumentException e) {
            LOG.error(DocsMessages.E_5008, e);
            return new ErrorView(500);
        } catch (DatabaseException e) {
            LOG.error(DocsMessages.E_5008, e);
            return new ErrorView(500);
        }
    }

    /**
     *
     */
    @Override
    public String view(ActionContext context) {
        return null;
    }

}
