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

import org.apache.log4j.Logger;

import com.shorindo.core.ActionContext;
import com.shorindo.core.annotation.ActionReady;
import com.shorindo.core.annotation.ContentTypeReady;
import com.shorindo.core.view.ThymeLeafView;
import com.shorindo.core.view.View;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
@ContentTypeReady("text/plain")
public class PlainTextController extends DocumentController {
    private static final Logger LOG = Logger.getLogger(PlainTextController.class);

    public PlainTextController(DocumentModel model) {
        super(model);
    }

    @Override @ActionReady
    public View view(ActionContext context) {
        LOG.info("view()");
        context.setAttribute("document", getModel());
        String body = getModel().getBody() == null ? "" : getModel().getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        return new ThymeLeafView(createClassPath("html/viewer"), context);
    }

    @ActionReady
    public View edit(ActionContext context) {
        LOG.info("edit()");
        context.setAttribute("document", getModel());
        String body = getModel().getBody() == null ? "" : getModel().getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return new ThymeLeafView(createClassPath("html/editor"), context);
    }
}
