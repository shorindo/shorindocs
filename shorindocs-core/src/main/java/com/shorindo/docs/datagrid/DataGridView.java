/*
 * Copyright 2020 Shorindo, Inc.
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
package com.shorindo.docs.datagrid;

import static com.shorindo.xuml.DOMBuilder.text;
import static com.shorindo.xuml.HTMLBuilder.*;
import static com.shorindo.xuml.XumlMessages.XUML_1001;
import static com.shorindo.xuml.XumlMessages.XUML_1002;

import java.io.IOException;
import java.io.OutputStream;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentView;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public class DataGridView extends DocumentView {
    private static final ActionLogger LOG = ActionLogger.getLogger(DataGridView.class);
    private DocumentModel model;
    
    public DataGridView(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void render(ActionContext context, OutputStream os) throws IOException {
        long st = System.currentTimeMillis();
        LOG.debug(XUML_1001, "render");
        layout()
            .put("meta", script()
                .attr("type", "text/javascript")
                .attr("src", "/docs/js/handsontable.full.min.js"))
            .put("meta", script()
                .attr("type", "text/javascript")
                .add(text("window.onload = function() { tbl = new Handsontable(document.getElementById('datagrid'), " + model.getContent() + "); };")))
            .put("meta", link()
                .attr("rel", "stylesheet")
                .attr("type", "text/css")
                .attr("href", "/docs/css/handsontable.full.min.css"))
            .put("header", text(model.getTitle()))
            .put("menubar-left", button("新規"))
            .put("menubar-left", button("編集")
                    .attr("onclick", "location='?action=edit'"))
            .put("left", recents(model.getDocumentId()))
            .put("main", div()
                .attr("class", "datagrid-wrapper")
                .add(div()
                .attr("id", "datagrid")))
            .render(os);
        LOG.debug(XUML_1002, "render", System.currentTimeMillis() - st);
    }

}
