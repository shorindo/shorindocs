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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocumentMessages;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.DocumentEntity;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.Transactionless;

/**
 * 
 */
@ContentTypeReady("text/plain")
public class PlainTextController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(PlainTextController.class);
    private static final DatabaseService databaseService = DatabaseService.newInstance();

    public PlainTextController() {
    }

    /**
     * 
     */
    @Override @ActionMethod
    public String view(ActionContext context) {
        LOG.trace("view()");
        try {
            DocumentEntity model = (DocumentEntity)context.getAttribute("document");
            String content = model.getContent() == null ? "" : model.getContent();
            context.setAttribute("content", content
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
            context.setAttribute("recents", recents());
        } catch (DatabaseException e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
        }
        return ".xuml";
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
