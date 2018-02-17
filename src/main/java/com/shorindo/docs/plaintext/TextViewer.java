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

import com.shorindo.xuml.Component;
import com.shorindo.xuml.ComponentReady;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ComponentReady("text-viewer")
public class TextViewer extends Component {

    public TextViewer(XumlView view) {
        super(view);
    }

    private static final Logger LOG = Logger.getLogger(TextViewer.class);

    @Override
    public String getHtml() {
        return "<div class=\"text-viewer\">\n" +
                "@{content}\n" +
                "</div>\n";
    }

    public void setDataSource(String value) {
        LOG.trace("setDataSource(" + value + ")");
    }
}