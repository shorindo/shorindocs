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
package com.shorindo.core.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import net.arnx.jsonic.JSON;

import com.shorindo.core.ActionContext;
import com.shorindo.core.DocsLogger;
import com.shorindo.core.Messages;

/**
 * 
 */
public class JsonView extends View {
    private static final DocsLogger LOG = DocsLogger.getLogger(JsonView.class);
    Object bean;

    public JsonView(Object bean, ActionContext context) {
        super(context);
        this.bean = bean;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public InputStream getContent() {
        try {
            return new ByteArrayInputStream(JSON.encode(bean, true).getBytes("UTF-8"));
        } catch (Exception e) {
            LOG.error(Messages.E_9999, e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

}
