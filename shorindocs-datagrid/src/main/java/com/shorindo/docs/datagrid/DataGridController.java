/*
 * Copyright 2020 Shorindo, Inc.
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
package com.shorindo.docs.datagrid;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.DocType;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView2;

/**
 * 
 */
@DocType("datagrid")
public class DataGridController extends DocumentController {
    private static ActionLogger LOG = ActionLogger.getLogger(DataGridController.class);

    public DataGridController(DocumentService service) {
        super(service);
    }

    @Override
    public View action(ActionContext context, Object...args) {
        return view(context, args);
    }

    private View view(ActionContext context, Object...args) {
    	DocumentModel model = (DocumentModel)args[0];
        String action = context.getParameter("action");

        if ("edit".equals(action)) {
//            return new DataGridEdit(model);
            return XumlView2.create("datagrid/xuml/datagrid.xuml");
        } else if ("save".equals(action)) {
            ((DocumentEntity)model).setContent(context.getParameter("content"));
            getDocumentService().save(model);
            return new RedirectView(model.getDocumentId());
        } else {
            return XumlView2.create("datagrid/xuml/datagrid.xuml");
        }
    }

}
