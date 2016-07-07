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
package com.shorindo.docs.text;

import org.apache.log4j.Logger;

import com.shorindo.core.AbstractView;
import com.shorindo.core.ActionContext;
import com.shorindo.core.ActionReady;
import com.shorindo.core.ContentTypeReady;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.DocumentModel;

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
    public AbstractView view(ActionContext context) {
        LOG.trace("view()");
        context.setAttribute("document", getModel());
        String body = getModel().getBody() == null ? "" : getModel().getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        context.setForward("text/viewer.xuml");
        return null;
    }

    @ActionReady
    public AbstractView edit(ActionContext context) {
        LOG.trace("edit()");
        context.setAttribute("document", getModel());
        String body = getModel().getBody() == null ? "" : getModel().getBody();
        context.setAttribute("content", body
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        context.setForward("text/editor.xuml");
        return null;
    }
}
