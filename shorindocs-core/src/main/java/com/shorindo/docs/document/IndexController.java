/*
 * Copyright 2016-2021 Shorindo, Inc.
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
package com.shorindo.docs.document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

/**
 * 
 */
public class IndexController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(IndexController.class);
    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    public IndexController() {
    }

    @Override
    public View action(ActionContext context, Object... args) {
        try {
            DocumentEntity entity = new DocumentEntity();
            entity.setTitle("README.md");
            context.addModel("document", entity);
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("html", render());
            return XumlView.create("xuml/index.xuml");
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    private String render() throws DocumentException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("README.md")) {
            Reader reader = new InputStreamReader(is, "UTF-8");
            StringBuilder sb = new StringBuilder();
            int len = 0;
            char[] buff = new char[4096];
            while ((len = reader.read(buff)) > 0) {
                sb.append(buff, 0, len);
            }
            Node document = parser.parse(sb.toString());
            return renderer.render(document);
        } catch (IOException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

}
