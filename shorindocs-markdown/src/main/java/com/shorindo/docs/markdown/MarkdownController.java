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

import static com.shorindo.docs.markdown.MarkdownMessages.MKDN_9000;
import static com.shorindo.xuml.DOMBuilder.cdata;
import static com.shorindo.xuml.DOMBuilder.text;
import static com.shorindo.xuml.HTMLBuilder.button;

import java.util.Locale;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.tools.MarkdownParser.MarkdownException;
import com.shorindo.xuml.XumlView2;

/**
 * 
 */
public class MarkdownController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownController.class);
    private DocumentService documentService = ApplicationContext.getBean(DocumentService.class);
    private MarkdownService markdownService = ApplicationContext.getBean(MarkdownService.class);

    public MarkdownController() {
    }

    /**
     * 
     */
    @Override
    public View action(ActionContext context, Object...args) {
        try {
        	DocumentModel model = (DocumentModel)args[0];
            switch (getAction(context)) {
            case "edit":
                return new MarkdownEdit(model);
            case "save":
                ((DocumentEntity)model).setContent(context.getParameter("content"));
                documentService.save(model);
                return new RedirectView(model.getDocumentId());
            default:
                context.addModel("lang", Locale.JAPANESE);
                context.addModel("document", model);
                context.addModel("html", markdownService.parse(model.getContent()));
                context.addModel("recents", recents(context));
                return XumlView2.create("markdown/xuml/markdown.xuml");
            }
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

}
