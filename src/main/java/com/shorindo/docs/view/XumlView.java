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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.shorindo.docs.View;

/**
 * 
 */
public class XumlView implements View {
    Map<String,Object> beans = new HashMap<String,Object>();

    public XumlView(String template) {
    }

    public void setProperty(String key, Object value) {
        beans.put(key, value);
    }

    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(((String)beans.get("_caller")).getBytes("UTF-8"));
    }

}
