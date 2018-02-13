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

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
public class DocumentController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentController.class);
    private DocumentModel model;

    @Override
    public View view(ActionContext context) {
        return new ErrorView(500, context);
    }

    protected DocumentModel getModel() {
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
