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

import java.util.Properties;

import org.apache.log4j.Logger;

import com.shorindo.docs.Actionable;
import com.shorindo.docs.ContentHandler;
import com.shorindo.docs.ContentModel;
import com.shorindo.docs.AbstractView;
import com.shorindo.docs.view.XumlView;

/**
 * 
 */
public class TextHandler extends ContentHandler {
    private static final Logger LOG = Logger.getLogger(TextHandler.class);

    public TextHandler(ContentModel model) {
        super(model);
    }

    @Override @Actionable
    public AbstractView view(Properties params) {
        LOG.trace("view()");
        AbstractView view = new XumlView("/WEB-INF/xuml/template.xuml");
        view.setAttribute("document", getModel());
//        view.setProperty("docs:abc", getModel().getBody()
//                .replaceAll("&", "&amp;")
//                .replaceAll("<", "&lt;")
//                .replaceAll(">", "&gt;")
//                .replaceAll("\"", "&quot;")
//                .replaceAll("\n", "<br>\n"));
        return view;
    }

    @Actionable
    public AbstractView edit(Properties params) {
        LOG.trace("edit()");
//        setAttribute("document", getModel());
//        setAttribute("document", getModel().getBody()
//                .replaceAll("&", "&amp;")
//                .replaceAll("<", "&lt;")
//                .replaceAll(">", "&gt;")
//                .replaceAll("\"", "&quot;"));
        return new XumlView("/WEB-INF/xuml/template.xuml");
    }
}
