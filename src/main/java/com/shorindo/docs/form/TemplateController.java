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
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocumentMessages;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlException;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentTypeReady("application/x-form-template")
public class TemplateController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(TemplateController.class);

    /**
     * 
     */
    public TemplateController() {
    }

    @Override
    public View view(ActionContext context) {
        try {
            return XumlView.create(getClass());
        } catch (XumlException e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public String edit(ActionContext context) {
        return ".xuml";
    }
}
