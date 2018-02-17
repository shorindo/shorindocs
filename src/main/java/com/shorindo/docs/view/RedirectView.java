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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.shorindo.docs.ActionContext;

/**
 * 
 */
public class RedirectView extends View {

    public RedirectView(String location, ActionContext context) {
        super();
        setStatus(302);
        if (!location.startsWith("/")) {
            location = "/" + location;
        }
        location = context.getRequest().getContextPath() + location;
        getMeta().put("Location", location);
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public void render(ActionContext context, OutputStream os) {
    }

}
