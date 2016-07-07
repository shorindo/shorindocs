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

import java.io.IOException;

import net.arnx.jsonic.JSON;

import com.shorindo.core.AbstractView;
import com.shorindo.core.ActionContext;

/**
 * 
 */
public class JsonView extends AbstractView {
    private Object bean;

    public JsonView(ActionContext context, Object bean) {
        super(context);
        this.bean = bean;
    }

    public String getContentType() {
        return "application/json; charset=UTF-8";
    }

    public String getContent() throws IOException {
        return JSON.encode(bean);
    }

//    @Override
//    public void setAttribute(String key, Object value) {
//        // TODO Auto-generated method stub
//    }

}
