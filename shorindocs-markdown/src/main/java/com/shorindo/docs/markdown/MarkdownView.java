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
package com.shorindo.docs.markdown;

import static com.shorindo.xuml.DOMBuilder.text;
import static com.shorindo.xuml.HTMLBuilder.*;
import static com.shorindo.docs.markdown.MarkdownMessages.*;

import java.io.IOException;
import java.io.OutputStream;

import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentView;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.tools.MarkdownParser.MarkdownException;

/**
 * 
 */
public class MarkdownView extends DocumentView {
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownView.class);
    private DocumentModel model;

    public MarkdownView(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void render(ActionContext context, OutputStream os)
        throws IOException {
        MarkdownService service = ServiceFactory.getService(MarkdownService.class);
        try {
            String html = service.parse(model.getContent());
            layout()
            .put("header", text(model.getTitle()))
            .put("menubar-left",button()
                    .add(text("新規")))
            .put("menubar-left", button()
                    .attr("onclick", "location='?action=edit'")
                    .add(text("編集")))
            .put("left", recents(model.getDocumentId()))
            .put("main", cdata(html))
            .render(os);
        } catch (MarkdownException e) {
            LOG.error(MKDN_9000);
        }
    }

}
