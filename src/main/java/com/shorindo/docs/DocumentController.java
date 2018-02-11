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
package com.shorindo.docs;

import java.sql.SQLException;

import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.form.FormController;
import com.shorindo.docs.form.TemplateController;
import com.shorindo.docs.plaintext.PlainTextController;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/*")
public abstract class DocumentController extends ActionController {
    private static final DocsLogger LOG = DocsLogger.getLogger(DocumentController.class);
    private DocumentModel model;

    public static DocumentController getController(final String id) throws DocumentException {
        try {
            DocumentModel model = getContentModel(id);
            if (model == null) {
                throw new DocumentException("model not found:" + id);
            } else if ("text/plain".equals(model.getContentType())) {
                return new PlainTextController(model);
            } else if ("application/x-form".equals(model.getContentType())) {
                return new FormController(model);
            } else if ("application/x-form-template".equals(model.getContentType())) {
                return new TemplateController(model);
            } else {
                throw new DocumentException("controller not found:" + model.getContentType());
            }
        } catch (Exception e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    public static DocumentModel getContentModel(String id) throws SQLException {
        DocumentModel model = new DocumentModel();
        model.setDocumentId(id);
        return DatabaseManager.selectOne("docs.getDocument", model);
    }

    public DocumentController(DocumentModel model) {
        this.model = model;
    }

    public DocumentModel getModel() {
        return model;
    }

    @ActionMethod
    public View save(ActionContext context) throws DocumentException {
        DocumentModel model = getModel();
        model.setTitle(context.getParameter("title"));
        model.setBody(context.getParameter("body"));
        if (DatabaseManager.update("docs.updateDocument", model) > 0) {
            return new RedirectView(model.getDocumentId(), context);
        } else {
            return new ErrorView(500, context);
        }
    }

    @ActionMethod
    public View create(ActionContext context) throws DocumentException {
        String id = String.valueOf(IdGenerator.getId());
        DocumentModel model = new DocumentModel();
        model.setDocumentId(id);
        model.setContentType(context.getParameter("contentType"));
        model.setTitle(context.getParameter("title"));
        model.setBody(context.getParameter("body"));
        if (DatabaseManager.insert("docs.createDocument", model) > 0) {
            return new RedirectView(id + "?action=edit", context);
        } else {
            return new ErrorView(500, context);
        }
    }

    @ActionMethod
    public View remove(ActionContext context) throws DocumentException {
        DocumentModel model = getModel();
        if ("index".equals(model.getDocumentId())) {
            return new RedirectView("/index", context);
        } else if (DatabaseManager.update("docs.removeDocument", model) > 0) {
            return new RedirectView("/index", context);
        } else {
            return new ErrorView(500, context);
        }
    }

//    @ActionMethod
//    public View search(ActionContext context) throws DocumentException {
//        List<DocumentModel> list = DatabaseManager.selectList("searchDocument", null);
//        return new JsonView(list, context);
//    }
}
