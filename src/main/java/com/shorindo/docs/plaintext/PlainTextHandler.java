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

import java.util.Properties;

import org.apache.log4j.Logger;

import com.shorindo.docs.Action;
import com.shorindo.docs.ContentHandler;
import com.shorindo.docs.ContentModel;

/**
 * 
 */
public class PlainTextHandler extends ContentHandler {
    private static final Logger LOG = Logger.getLogger(PlainTextHandler.class);

    public PlainTextHandler(ContentModel model) {
        super(model);
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override @Action
    public String view(Properties params) {
        LOG.trace("view()");
        setAttribute("body", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br>\n"));
        return "/jsp/view.jsp";
    }

    @Action
    public String edit(Properties params) {
        LOG.trace("edit()");
        setAttribute("body", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return "/jsp/edit.jsp";
    }
}
