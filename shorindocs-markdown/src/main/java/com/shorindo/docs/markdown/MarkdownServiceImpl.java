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

import static com.shorindo.docs.markdown.MarkdownMessages.*;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.tools.MarkdownParser.MarkdownException;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

/**
 * 
 */
public class MarkdownServiceImpl implements MarkdownService {
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownServiceImpl.class);
    private Parser parser = Parser.builder().build();
    private HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public String parse(String text) throws MarkdownException {
        LOG.debug(MKDN_1000);
        long st = System.currentTimeMillis();
        try {
            Node document = parser.parse(text);
            return renderer.render(document);
        } finally {
            LOG.debug(MKDN_1001, System.currentTimeMillis() - st);
        }
    }

}
