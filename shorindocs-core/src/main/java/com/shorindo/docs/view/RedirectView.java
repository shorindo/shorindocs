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

import com.shorindo.docs.action.ActionContext;

/**
 * 
 */
public class RedirectView extends AbstractView {
    private int STATUS_MOVED = 302;
    private String location;

    public RedirectView(String location) {
        super();
        this.location = location;
        this.getMetaData().put("Location", location);
    }

    @Override
    public int getStatus() {
        return STATUS_MOVED;
    }

    @Override
    public void render(ActionContext context, OutputStream os) {
    }

}
