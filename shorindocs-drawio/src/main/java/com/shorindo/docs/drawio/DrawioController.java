/*
 * Copyright 2021 Shorindo, Inc.
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
package com.shorindo.docs.drawio;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

public class DrawioController extends DocumentController {

    public DrawioController(DocumentService documentService) {
        super(documentService);
    }

    @Override
    public View action(ActionContext context, Object... args) {
        return XumlView.create("docs-drawio/xuml/drawio.xuml");
    }

    @ActionMethod
    public Object load(ActionContext context) {
        DocumentModel model = getDocumentService().load(context.getDocumentId());
        Map<String,Object> result = new HashMap<>();
        result.put("title", model.getTitle());
        result.put("editable", false);
        result.put("data", model.getContent());
        return result; 
    }

    @ActionMethod
    public Object edit(ActionContext context) throws Exception {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(context.getDocumentId());
        DocumentModel model = getDocumentService().edit(entity);
        Map<String,Object> result = new HashMap<>();
        result.put("title", model.getTitle());
        result.put("editable", true);
        result.put("data", model.getContent());
        return result; 
    }

    @ActionMethod
    public DocumentModel save(ActionContext context) {
        String title = context.getParameter("title");
        String data = context.getParameter("data");
        DocumentModel model = getDocumentService().load(context.getDocumentId());
        DocumentEntity entity = new DocumentEntity(model);
        entity.setTitle(title);
        entity.setContent(data);
        return getDocumentService().save(entity);
    }

    @ActionMethod
    public Object commit(ActionContext context) throws DocumentException {
        DocumentModel draft = save(context);
        return getDocumentService().commit(context.getDocumentId(),
            draft.getVersion());
    }
}
