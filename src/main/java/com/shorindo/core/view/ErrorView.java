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

import java.io.InputStream;

import com.shorindo.core.ActionContext;

/**
 * 
 */
public class ErrorView extends View {

    public ErrorView(int status, ActionContext context) {
        super(context);
        setStatus(status);
    }

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public InputStream getContent() {
        context.setAttribute("status", getStatus());
        context.setAttribute("message", "message");
        return new ThymeLeafView("html/error", context).getContent();
    }

}
