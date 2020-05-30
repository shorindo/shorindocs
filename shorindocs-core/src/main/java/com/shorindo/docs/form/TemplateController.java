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

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentType;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.AbstractView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlException;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentType("application/x-form-template")
public class TemplateController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(TemplateController.class);

    /**
     * 
     */
    public TemplateController() {
    }

    @Override
    public View view(ActionContext context) {
        return null;
    }

    @ActionMethod
    public String edit(ActionContext context) {
        return ".xuml";
    }
}
