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
package com.shorindo.docs.plaintext;

import java.util.Locale;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class PlainTextController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(PlainTextController.class);
    private AuthenticateService authenticateService;

    public PlainTextController(DocumentService documentService, AuthenticateService authenticateService) {
        super(documentService);
        this.authenticateService = authenticateService;
    }

    /**
     * 
     */
    @Override
    public View action(ActionContext context, Object...args) {
        //LOG.debug("action()->" + context.getParameter("action"));
        try {
            DocumentModel model = (DocumentModel)args[0];
            context.addModel("user", authenticateService.getUser());
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", model);
            if ("edit".equals(context.getParameterAsString("action"))) {
                return XumlView.create("plaintext/xuml/plaintext-edit.xuml");
            } else {
                context.addModel("recents", recents(context));
                return XumlView.create("plaintext/xuml/plaintext-view.xuml");
            }
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public void save(ActionContext context) {
        LOG.debug("save()");
    }
}
