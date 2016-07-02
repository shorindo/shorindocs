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

import com.shorindo.docs.Actionable;
import com.shorindo.docs.ContentHandler;
import com.shorindo.docs.ContentModel;

/**
 * 
 */
public class TextHandler extends ContentHandler {
    private static final Logger LOG = Logger.getLogger(TextHandler.class);

    public TextHandler(ContentModel model) {
        super(model);
    }

    @Override @Actionable
    public String view(Map<String,Object> params) {
        LOG.trace("view()");
        params.put("document", getModel());
        params.put("content", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\n", "<br/>"));
        return "text/viewer.xuml";
    }

    @Actionable
    public String edit(Map<String,Object> params) {
        LOG.trace("edit()");
        params.put("document", getModel());
        params.put("content", getModel().getBody()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;"));
        return "text/editor.xuml";
    }
}
