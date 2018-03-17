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

import java.sql.Connection;
import java.sql.SQLException;

import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseExecutor;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.Transactional;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
public class DocumentController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentController.class);
    private static final DatabaseService databaseService = DatabaseService.newInstance();
    private DocumentEntity model;

    /**
     * 呼ばれることはないので何もしない
     */
    @Override
    public String view(ActionContext context) {
        return null;
    }

    /**
     * 
     */
    protected DocumentEntity getModel() {
        return model;
    }

    /*
     * 
     */
    private static final DatabaseExecutor<Integer> UPDATE_EXEC = new Transactional<Integer>() {
        @Override
        public Integer run(Connection conn, Object... params)
                throws DatabaseException {
            DocumentEntity document = (DocumentEntity)params[0];
            UserEntity user = (UserEntity)params[1];
            return exec(
                "UPDATE DOCUMENT " +
                "SET TITLE=?, BODY=?, UPDATE_DATE=NOW() " +
                "WHERE DOCUMENT_ID=? AND OWNER_ID=?",
                document.getTitle(),
                document.getBody(),
                document.getDocumentId(),
                user.getUserId()
                );
        }
    };

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View save(ActionContext context) throws DocumentException {
        DocumentEntity model = getModel();
        model.setTitle(context.getParameter("title"));
        model.setBody(context.getParameter("body"));
        String id = model.getDocumentId();
        try {
            int result = databaseService.provide(UPDATE_EXEC, model, context.getUser());
            if (result > 0) {
                return new RedirectView(id + "?action=view", context);
            } else {
                return new ErrorView(404);
            }
        } catch (DatabaseException e) {
            LOG.error(DocsMessages.E_9002, id);
            return new ErrorView(500);
        }
    }

    /*
     * 
     */
    private static final DatabaseExecutor<Integer> CREATE_EXEC = new Transactional<Integer>() {
        @Override
        public Integer run(Connection conn, Object... params)
                throws DatabaseException {
            DocumentEntity document = (DocumentEntity)params[0];
            UserEntity user = (UserEntity)params[1];
            return exec(
                "INSERT INTO DOCUMENT " +
                "(DOCUMENT_ID,CONTENT_TYPE,STATUS,TITLE,BODY,CREATE_DATE,UPDATE_DATE,OWNER_ID) VALUES " +
                "VALUES (?,?,?,?,?,NOW(),NOW(),?)",
                document.getDocumentId(),
                document.getContentType(),
                document.getStatus(),
                document.getTitle(),
                document.getBody(),
                user.getUserId()
                );
        }
    };

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View create(ActionContext context) throws DocumentException {
        String id = String.valueOf(IdentityProvider.newId());
        DocumentEntity model = new DocumentEntity();
        model.setDocumentId(id);
        model.setContentType(context.getParameter("contentType"));
        model.setTitle(context.getParameter("title"));
        model.setBody(context.getParameter("body"));
        try {
            if (databaseService.provide(CREATE_EXEC, model, context.getUser()) >= 0) {
                return new RedirectView(id + "?action=edit", context);
            } else {
                return new ErrorView(404);
            }
        } catch (DatabaseException e) {
            LOG.error(DocsMessages.E_9002, id);
            return new ErrorView(500);
        }
    }

    /*
     * 
     */
    private static final DatabaseExecutor<Integer> REMOVE_EXEC = new Transactional<Integer>() {
        @Override
        public Integer run(Connection conn, Object... params)
                throws DatabaseException {
            DocumentEntity document = (DocumentEntity)params[0];
            return remove(document);
        }
    };

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View remove(ActionContext context) throws DocumentException {
        DocumentEntity model = getModel();
        String id = model.getDocumentId();
        if ("index".equals(id)) {
            return new RedirectView("/index", context);
        } else {
            try {
                if (databaseService.provide(REMOVE_EXEC, model) > 0) {
                    return new RedirectView("/index", context);
                } else {
                    LOG.error(DocsMessages.E_9003, id);
                    return new ErrorView(500);
                }
            } catch (DatabaseException e) {
                LOG.error(DocsMessages.E_9003, e, id);
                return new ErrorView(500);
            }
        }
    }

}
