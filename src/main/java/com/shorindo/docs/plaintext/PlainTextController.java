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
import com.shorindo.docs.DocsMessages;
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
    @Override
    public String view(ActionContext context) {
        LOG.trace("view()");
        DocumentEntity model = (DocumentEntity)context.getAttribute("document");
        String body = model.getBody() == null ? "" : model.getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        try {
            context.setAttribute("search_result", recents());
        } catch (DatabaseException e) {
            LOG.error(DocsMessages.E_9001, e);
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
        String body = model.getBody() == null ? "" : model.getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return ".xuml";
    }

    /*
     * 
     */
    private static final Transactionless<List<DocumentEntity>> RECENTS_EXEC =
            new Transactionless<List<DocumentEntity>>() {            
        @Override
        public List<DocumentEntity> run(Connection conn, Object...params) throws DatabaseException {
            return query(
                "SELECT document_id,title,update_date " +
                "FROM   document " +
                "ORDER  BY update_date DESC " +
                "LIMIT  10",
                DocumentEntity.class);
        }
    };
    
    /**
     * 
     * @return
     * @throws SQLException
     */
    private List<DocumentEntity> recents() throws DatabaseException {
        return databaseService.provide(RECENTS_EXEC);
    }
}
