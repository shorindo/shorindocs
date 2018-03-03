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

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DatabaseManager;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.DocumentModel;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;

/**
 * 
 */
@ContentTypeReady("text/plain")
public class PlainTextController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(PlainTextController.class);

    public PlainTextController() {
    }

    @Override
    public String view(ActionContext context) {
        LOG.info("view()");
        DocumentModel model = (DocumentModel)context.getAttribute("document");
        String body = model.getBody() == null ? "" : model.getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        context.setAttribute("search_result",
                DatabaseManager.selectList("searchDocument", null));
        return ".xuml";
    }

    @ActionMethod
    public String edit(ActionContext context) {
        LOG.info("edit()");
        DocumentModel model = (DocumentModel)context.getAttribute("document");
        String body = model.getBody() == null ? "" : model.getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return ".xuml";
    }
}
