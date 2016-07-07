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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.shorindo.core.AbstractView;
import com.shorindo.core.ActionContext;
import com.shorindo.core.ActionController;
import com.shorindo.core.ActionReady;
import com.shorindo.core.DatabaseManager;
import com.shorindo.core.Logger;
import com.shorindo.docs.text.PlainTextController;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final Logger LOG = Logger.getLogger(DocumentController.class);
    private DocumentModel model;

    public static DocumentController getHandler(final String id) throws DocumentException {

        try {
            DocumentModel model = getContentModel(id);
            if (model == null) {
                throw new DocumentException("model not found:" + id);
            } else if ("text/plain".equals(model.getContentType())) {
                return new PlainTextController(model);
            } else {
                throw new DocumentException("handler not found:" + model.getContentType());
            }
        } catch (Exception e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    public static DocumentModel getContentModel(String id) throws SQLException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("id", id);
        return DatabaseManager.selectOne("docs.getDocument", map);
    }

    public DocumentController(DocumentModel model) {
        this.model = model;
    }

    public DocumentModel getModel() {
        return model;
    }

    public String save(Map<String,Object> params) throws DocumentException {
        return null;
    }

    @ActionReady
    public AbstractView create(ActionContext context) throws DocumentException {
        for (int i = 0; i < 10; i++) {
            String id = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
            DocumentModel model = new DocumentModel();
            model.setDocumentId(id);
            model.setContentType((String)context.getParameter("contentType"));
            if (DatabaseManager.insert("docs.createDocument", model) > 0) {
                context.setForward("redirect:" + id + "?action=edit");
                break;
            }
        }
        return null;
    }

    @ActionReady
    public List<DocumentModel> search(Map<String,Object> params) throws DocumentException {
        LOG.trace("search()");
        return DatabaseManager.selectList("searchDocument", null);
    }
}
