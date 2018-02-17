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
package com.shorindo.docs.form;

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionController;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.DocumentModel;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentTypeReady("application/x-form-template")
public class TemplateController extends ActionController {

    /**
     * 
     */
    public TemplateController() {
    }

    @Override
    public String view(ActionContext context) {
        return ".xuml";
    }

    @ActionMethod
    public String edit(ActionContext context) {
        return ".xuml";
    }
}
