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
package com.shorindo.docs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public abstract class AbstractView {
    private Map<String,Object> attrs = new HashMap<String,Object>();

    public void setAttribute(String key, Object value) {
        attrs.put(key, value);
    }

    public Object getAttribute(String key) {
        return attrs.get(key);
    }

    public void setMessageResources() {
        //TODO
    }

    public abstract String getContentType();
    public abstract String getContent() throws IOException;
}
