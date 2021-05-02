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
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public abstract class AbstractView implements View {
    private static final ActionLogger LOG = ActionLogger.getLogger(AbstractView.class);
    private static final int STATUS_OK = 200;
    private Map<String,String> metaData;

    public abstract void render(ActionContext context, OutputStream os) throws IOException;

    public AbstractView() {
        metaData = new TreeMap<String,String>();
    }
    
    @Override
    public int getStatus() {
        return STATUS_OK;
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }
}
