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
package com.shorindo.docs.outlogger;

import static com.shorindo.docs.outlogger.OutloggerMessages.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class OutloggerController extends DocumentController {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(OutloggerController.class);
    private OutloggerService outloggerService;

    public OutloggerController(
            DocumentService documentService,
            OutloggerService outloggerService) {
        super(documentService);
        this.outloggerService = outloggerService;
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View action(ActionContext context, Object...args) {
        try {
            DocumentEntity document = (DocumentEntity)args[0];
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", document);
            context.addModel("outlines", parse(document.getContent()));
            context.addModel("editable", false);
            return XumlView.create("outlogger/xuml/outlogger.xuml");
        } catch (Exception e) {
            LOG.error(OLOG_9999, e);
            return new ErrorView(500);
        }
    }

    /**
     * 
     */
    @Override
    @ActionMethod
    public Object edit(ActionContext context) throws Exception {
        DocumentEntity e = new DocumentEntity();
        e.setDocumentId(context.getDocumentId());
        DocumentModel model = getDocumentService().edit(e);
        context.addModel("document", model);
        context.addModel("lang", Locale.JAPANESE);
        context.addModel("outlines", parse(model.getContent()));
        context.addModel("editable", true);

        List<Object> resultList = new ArrayList<>();
        PartialView view = new PartialView();
        view.setMethod("mod");
        view.setName("outlogger/xuml/outlogger.xuml#edit");
        view.setTarget("#outlogger-pane");
        resultList.add(updateView(context, view));

        PartialView headView = new PartialView();
        headView.setMethod("mod");
        headView.setName("xuml/layout.xuml#HEAD");
        headView.setTarget("#header-pane");
        resultList.add(updateView(context, headView));

        context.addModel("message", "編集中です");
        PartialView messageView = new PartialView();
        messageView.setMethod("add");
        messageView.setName("xuml/layout.xuml#message");
        messageView.setTarget("body");
        resultList.add(updateView(context, messageView));

        return resultList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @ActionMethod
    public DocumentModel save(ActionContext context) {
        String title = context.getParameter("title");
        List<Object> outlines = (List<Object>)context.getParamMap().get("content");
        StringBuilder sb = new StringBuilder()
            .append("<?xml version='1.0'?>")
            .append("<outlogger>");
        for (Object outline : outlines) {
            sb.append(extract(outline));
        }
        sb.append("</outlogger>");
        DocumentModel model = getDocumentService().load(context.getDocumentId());
        DocumentEntity entity = new DocumentEntity(model);
        entity.setTitle(title);
        entity.setContent(sb.toString());
        return getDocumentService().save(entity);
    }

    @Override
    @ActionMethod
    public Object commit(ActionContext context) throws DocumentException {
        DocumentModel draft = save(context);
        return getDocumentService().commit(draft.getDocumentId(), draft.getVersion());
    }

    @SuppressWarnings("unchecked")
    private String extract(Object outline) {
        Map<String,Object> map = (Map<String,Object>)outline;
        StringBuilder sb = new StringBuilder()
            .append("<outline>")
            .append("<text>")
            .append(map.get("text"))
            .append("</text>");
        List<Object> children = (List<Object>)map.get("children");
        for (Object child : children) {
            sb.append(extract(child));
        }
        sb.append("</outline>");
        return sb.toString();
    }

    private List<OutlineViewModel> parse(String xml) {
        List<OutlineViewModel> result = new ArrayList<>();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(bais);
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if ("outline".equals(child.getNodeName())) {
                    result.add(load(child));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private OutlineViewModel load(Node node) {
        OutlineViewModel model = new OutlineViewModel();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if ("text".equals(child.getNodeName())) {
                model.setText(child.getTextContent());
            } else if ("outline".equals(child.getNodeName())) {
                model.addChild(load(child));
            }
        }
        return model;
    }
}
