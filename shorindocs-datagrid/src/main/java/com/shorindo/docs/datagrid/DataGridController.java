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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.datagrid.entity.CellEntity;
import com.shorindo.docs.datagrid.entity.RecordEntity;
import com.shorindo.docs.datagrid.entity.SchemaEntity;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.document.DocumentController.PartialView;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class DataGridController extends DocumentController {
    private static ActionLogger LOG = ActionLogger.getLogger(DataGridController.class);
    private DataGridService dataGridService;

    public DataGridController(DocumentService service,
        DataGridService dataGridService) {
        super(service);
        this.dataGridService = dataGridService;
    }

    @Override
    public View action(ActionContext context, Object...args) {
        return view(context, args);
    }

    private View view(ActionContext context, Object...args) {
        try {
            DocumentModel model = (DocumentModel)args[0];
            String action = context.getParameter("action");
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", model);
            context.addModel("recents", recents(context));

            if ("edit".equals(action)) {
                return XumlView.create("datagrid/xuml/datagrid-view.xuml");
            } else if ("save".equals(action)) {
                ((DocumentEntity)model).setContent(context.getParameter("content"));
                getDocumentService().save(model);
                return new RedirectView(model.getDocumentId());
            } else {
                SchemaEntity schema = dataGridService.loadSchema(model.getContent());
                context.addModel("schema", schema);
                List<RecordEntity> records = dataGridService.searchRecords(model.getDocumentId(), model.getContent());
                context.addModel("records", records);
                return XumlView.create("datagrid/xuml/datagrid-view.xuml");
            }
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9001, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public Object edit(ActionContext context) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(context.getPath().substring((1)));
        String version = context.getParameter("version");
        if (version == null) {
            version = "0";
        }
        entity.setVersion(Integer.parseInt(version));
        try {
            DocumentModel model = getDocumentService().edit(entity);
            context.addModel("document", model);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PartialView view = new PartialView();
        view.setName("datagrid/xuml/datagrid-edit.xuml#editor");
        view.setMethod("mod");
        view.setTarget("#main-pane");
        List<Object> resultList = new ArrayList<>();
        resultList.add(updateView(context, view));
        return resultList;
    }

    @ActionMethod
    public Object loadData(ActionContext context) {
        DocumentModel model = getModel(context);
        return dataGridService.searchRecords(model.getDocumentId(), model.getContent());

//        List<List<String>> result = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            List<String> record = new ArrayList<>();
//            record.add("2021/04/" + (i + 1));
//            result.add(record);
//        }
//        return result;
    }
}
