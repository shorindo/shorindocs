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
package com.shorindo.docs.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class MarkdownController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownController.class);
    private MarkdownService markdownService;

    public MarkdownController(DocumentService documentService,
            MarkdownService markdownService) {
        super(documentService);
        this.markdownService = markdownService;
    }

    /**
     * 
     */
    @Override
    public View action(ActionContext context, Object...args) {
        try {
            DocumentModel model = (DocumentModel)args[0];
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", model);
            context.addModel("favicon", context.getContextPath() + "/markdown/img/markdown-icon.ico");
            if ("edit".equals(context.getParameter("action"))) {
                return XumlView.create("markdown/xuml/markdown-edit.xuml");
            } else {
                context.addModel("html", markdownService.parse(Optional.ofNullable(model.getContent()).orElse("")));
                context.addModel("recents", recents(context));
                return XumlView.create("markdown/xuml/markdown-view.xuml");
            }
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public Object edit(ActionContext context) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(context.getPath().substring((1)));
        String version = context.getParameter("version");
        if (version == null) {
            version = "0";
        }
        entity.setVersion(Integer.parseInt(version));
        try {
            DocumentModel model = getDocumentService().edit(entity);
            context.addModel("document", model);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PartialView view = new PartialView();
        view.setName("markdown/xuml/markdown-edit.xuml#editor");
        view.setMethod("mod");
        view.setTarget("#main-pane");
        List<Object> resultList = new ArrayList<>();
        resultList.add(updateView(context, view));
        return resultList;
    }
}
