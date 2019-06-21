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
package com.shorindo.docs.plaintext;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceFactory;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentTypeReady("text/plain")
public class PlainTextController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(PlainTextController.class);
    private static final RepositoryService repositoryService = RepositoryServiceFactory.repositoryService();

    public PlainTextController() {
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View view(ActionContext context) {
        LOG.trace("view()");
        try {
            DocumentEntity entity = getModel(context);
            String content = entity.getContent() == null ? "" : entity.getContent();
            context.setAttribute("content", content
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
            context.setAttribute("recents", recents());
            return XumlView.create(getClass());
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    /**
     * 
     * @param context
     * @return
     */
    @ActionMethod
    public String edit(ActionContext context) {
        LOG.trace("edit()");
        DocumentEntity model = (DocumentEntity)context.getAttribute("document");
        String body = model.getContent() == null ? "" : model.getContent();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return ".xuml";
    }

}
