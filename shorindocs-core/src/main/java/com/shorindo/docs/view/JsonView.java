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
package com.shorindo.docs.view;

import java.io.OutputStream;

import net.arnx.jsonic.JSON;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;

/**
 * 
 */
public class JsonView extends View {
    private static final ActionLogger LOG = ActionLogger.getLogger(JsonView.class);
    Object bean;

    public JsonView(Object bean, ActionContext context) {
        init();
        this.bean = bean;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void render(ActionContext context, OutputStream os) {
        try {
            os.write(JSON.encode(bean, true).getBytes("UTF-8"));
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9999, e);
            new ErrorView(500).render(context, os);
        }
    }

}
