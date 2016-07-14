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
import java.util.Date;

import com.shorindo.core.ActionContext;
import com.shorindo.core.ActionController;
import com.shorindo.core.DatabaseManager;
import com.shorindo.core.Logger;
import com.shorindo.core.annotation.ActionMethod;
import com.shorindo.core.view.ErrorView;
import com.shorindo.core.view.RedirectView;
import com.shorindo.core.view.View;
import com.shorindo.docs.plaintext.PlainTextController;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final Logger LOG = Logger.getLogger(DocumentController.class);
    private DocumentModel model;

    public static DocumentController getController(final String id) throws DocumentException {
        try {
            DocumentModel model = getContentModel(id);
            if (model == null) {
                throw new DocumentException("model not found:" + id);
            } else if ("text/plain".equals(model.getContentType())) {
                return new PlainTextController(model);
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
        for (int i = 0; i < 10; i++) {
            //String id = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
            String id = String.valueOf(new Date().getTime() % 999999999999L);
            DocumentModel model = new DocumentModel();
            model.setDocumentId(id);
            model.setContentType(context.getParameter("contentType"));
            model.setTitle(context.getParameter("title"));
            model.setBody(context.getParameter("body"));
            if (DatabaseManager.insert("docs.createDocument", model) > 0) {
                return new RedirectView(id + "?action=edit", context);
            }
        }
        return new ErrorView(500, context);
    }

//    @ActionMethod
//    public View search(ActionContext context) throws DocumentException {
//        List<DocumentModel> list = DatabaseManager.selectList("searchDocument", null);
//        return new JsonView(list, context);
//    }
}
