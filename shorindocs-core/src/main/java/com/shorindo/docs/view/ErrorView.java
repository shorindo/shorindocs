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

import static com.shorindo.xuml.XumlBuilder.*;
import static com.shorindo.docs.document.DocumentMessages.DOCS_9999;

import java.io.OutputStream;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.xuml.XumlBuilder;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ErrorView extends XumlView {
    private static final ActionLogger LOG = ActionLogger.getLogger(ErrorView.class);
    private int status;

    public ErrorView(int status) {
        this.status = status;
    }

    @Override
    public void render(ActionContext context, OutputStream os) {
        try {
            layout()
                .put("title", text(status + " - エラー"))
                .add(dialog()
                    .put("title", text("エラー"))
                    .put("body", text("description")))
                .render(os);
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        }
    }

}
