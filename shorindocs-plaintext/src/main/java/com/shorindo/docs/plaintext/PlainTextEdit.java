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
package com.shorindo.docs.plaintext;

import static com.shorindo.xuml.HTMLBuilder.*;

import java.io.IOException;
import java.io.OutputStream;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.document.DocumentView;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public class PlainTextEdit extends DocumentView {
    private DocumentModel model;

    public PlainTextEdit(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void render(ActionContext context, OutputStream os)
        throws IOException {
        layout()
            .put("header", text(model.getTitle()))
            .put("menubar-left", button()
                .add("表示"))
            .put("menubar-left", button()
                .add("保存"))
            .put("left", recents(model.getDocumentId()))
            .put("main", textarea()
                .add(text(model.getContent())))
            .render(os);
    }

}
