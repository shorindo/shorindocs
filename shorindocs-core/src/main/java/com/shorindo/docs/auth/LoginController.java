/*
 * Copyright 2016-2018 Shorindo, Inc.
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
package com.shorindo.docs.auth;

import static com.shorindo.docs.document.DocumentMessages.DOCS_9999;

import java.util.Optional;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class LoginController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(LoginController.class);

    /**
     * TODO
     */
    @Override
    public View action(ActionContext context, Object...args) {
        try {
            StringBuilder sb = new StringBuilder()
                .append(context.getContextPath())
                .append(Optional.ofNullable(context.getParameter("referer"))
                    .orElse("/"));
            context.addModel("referer", sb.toString());
            return XumlView.create("auth/xuml/login.xuml");
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
            return new ErrorView(500);
        }
    }
}
