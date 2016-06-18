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

import com.shorindo.docs.ContentHandler;
import com.shorindo.docs.ContentModel;

/**
 * 
 */
public class PlainTextHandler extends ContentHandler {
    private static final String[] actions = new String[] { "view" };
    private ContentModel model;

    public PlainTextHandler(ContentModel model) {
        this.model = model;
    }

    @Override
    public String[] getActions() {
        return actions;
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String view(Properties params) {
        return model.getBody();
    }

}
