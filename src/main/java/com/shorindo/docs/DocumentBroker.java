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

import java.sql.SQLException;

import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.form.FormController;
import com.shorindo.docs.form.TemplateController;
import com.shorindo.docs.plaintext.PlainTextController;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/*")
public final class DocumentBroker extends ActionController {
    private DocsLogger LOG = DocsLogger.getLogger(DocumentBroker.class);

    public static ActionController getController(DocumentModel model) throws DocumentException {
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

    public static DocumentModel getDocumentModel(String id) throws SQLException {
        DocumentModel model = new DocumentModel();
        model.setDocumentId(id);
        return DatabaseManager.selectOne("docs.getDocument", model);
    }

    @Override
    public View action(ActionContext context) {
        LOG.debug("action()");
        try {
            String id = context.getRequest().getServletPath().substring(1);
            DocumentModel model = getDocumentModel(id);
            context.setAttribute("document", model);
            return getController(model).action(context);
        } catch (DocumentException e) {
            LOG.error(Messages.E9999, e);
            return null;
        } catch (SQLException e) {
            LOG.error(Messages.E9999, e);
            return null;
        }
    }

    /**
     *
     */
    @Override
    public View view(ActionContext context) {
        return null;
    }

}
