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

import com.shorindo.core.ActionContext;
import com.shorindo.core.annotation.ActionMethod;
import com.shorindo.core.annotation.ContentTypeReady;
import com.shorindo.core.view.ThymeLeafView;
import com.shorindo.core.view.View;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.DocumentModel;

/**
 * 
 */
@ContentTypeReady("application/x-form-template")
public class TemplateController extends DocumentController {

    /**
     * 
     */
    public TemplateController(DocumentModel model) {
        super(model);
    }

    @Override @ActionMethod
    public View view(ActionContext context) {
        context.setAttribute("document", getModel());
        return new ThymeLeafView(createClassPath("html/viewTemplate"), context);
    }

    @ActionMethod
    public View edit(ActionContext context) {
        context.setAttribute("document", getModel());
        return new ThymeLeafView(createClassPath("html/editTemplate"), context);
    }
}
