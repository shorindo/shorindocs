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

import com.shorindo.docs.text.PlainTextController;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final Logger LOG = Logger.getLogger(DocumentController.class);
    private DocumentModel model;

    public static DocumentController getHandler(final String id) throws ContentException {

        try {
            DocumentModel model = getContentModel(id);
            if (model == null) {
                throw new ContentException("model not found:" + id);
            } else if ("text/plain".equals(model.getContentType())) {
                return new PlainTextController(model);
            } else {
                throw new ContentException("handler not found:" + model.getContentType());
            }
        } catch (Exception e) {
            throw new ContentException(e.getMessage(), e);
        }
    }

    public static DocumentModel getContentModel(String id) throws SQLException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("id", id);
        return DatabaseManager.selectOne("docs.getContent", map);
    }

    public DocumentController(DocumentModel model) {
        this.model = model;
    }

    public DocumentModel getModel() {
        return model;
    }

    public String save(Map<String,Object> params) throws ContentException {
        return null;
    }

    @ActionReady
    public String create(Map<String,Object> params) throws ContentException {
        for (int i = 0; i < 10; i++) {
            String id = String.valueOf(new Random().nextLong());
            DocumentModel model = new DocumentModel();
            model.setContentId(id);
            model.setContentType((String)params.get("contentType"));
            if (DatabaseManager.insert("docs.createContent", model) > 0) {
                return id + "?action=edit";
            }
            
        }
        return null;
    }

    @ActionReady
    public List<DocumentModel> search(Map<String,Object> params) throws ContentException {
        LOG.trace("search()");
        return DatabaseManager.selectList("searchContent", null);
    }
}
