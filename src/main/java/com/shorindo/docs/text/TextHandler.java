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

import java.util.Map;

import org.apache.log4j.Logger;

import com.shorindo.docs.ActionMessage;
import com.shorindo.docs.ActionReady;
import com.shorindo.docs.ContentController;
import com.shorindo.docs.ContentModel;
import com.shorindo.docs.ContentTypeReady;

/**
 * 
 */
@ContentTypeReady("text/plain")
public class TextHandler extends ContentController {
    private static final Logger LOG = Logger.getLogger(TextHandler.class);

    public TextHandler(ContentModel model) {
        super(model);
    }

    @Override @ActionReady
    public void view(ActionMessage message) {
        LOG.trace("view()");
        message.setAttribute("document", getModel());
        message.setAttribute("content", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        message.setForward("text/viewer.xuml");
    }

    @ActionReady
    public void edit(ActionMessage message) {
        LOG.trace("edit()");
        message.setAttribute("document", getModel());
        message.setAttribute("content", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        message.setForward("text/editor.xuml");
    }
}
