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

import com.shorindo.docs.ServiceFactory;
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

/**
 * 
 */
public class MarkdownController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownController.class);
    private DocumentService service = ServiceFactory.getService(DocumentService.class);

    public MarkdownController() {
    }

    /**
     * 
     */
    @Override
    public View action(ActionContext context) {
        try {
            DocumentModel model = (DocumentModel)context.getAttribute("document");
            switch (getAction(context)) {
            case "edit":
                return new MarkdownEdit(model);
            case "save":
                ((DocumentEntity)model).setContent(context.getParameter("content"));
                service.save(model);
                return new RedirectView(model.getDocumentId());
            default:
                return new MarkdownView(model);
            }
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

}
