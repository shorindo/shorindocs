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
package com.shorindo.docs.document;

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionError;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.tools.MicroDOM;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentController.class);
    private static final Map<String,DocumentController> controllerMap = new TreeMap<>();
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    public static void addController(String docType, DocumentController controller) {
        if (controllerMap.containsKey(docType)) {
            LOG.warn("[{0}]は既に登録されているため無視します。", docType);
        } else {
            LOG.info("ドキュメントタイプ[{0}]に[{1}]を登録します。", docType, controller.getClass());
            controllerMap.put(docType, controller);
        }
    }

    public static DocumentController getController(String docType) {
        LOG.debug("docType={0}", docType);
        return controllerMap.get(docType);
    }

    /**
     * 
     */
    protected final DocumentModel getModel(ActionContext context) {
        return documentService.load(context.getPath().substring(1));
    }

    /**
     * 
     */
    @ActionMethod
    public DocumentModel load(String documentId) throws ActionError {
        try {
            return documentService.load(documentId);
        } catch (Exception e) {
            throw new ActionError(DOCS_9999, e);
        }
    }

    /**
     *
     */
    @ActionMethod
    public Object create(ActionContext context) throws ActionError {
//        LOG.debug("create({0}, {1})",
//            context.getParameterAsString("docType"),
//            context.getParameterAsString("title"));
        try {
            DocumentEntity model = new DocumentEntity();
            model.setDocType(context.getParameter("docType"));
            model.setTitle(context.getParameter("title"));
            DocumentEntity entity = (DocumentEntity) documentService.create(model);
            XumlView view = XumlView.create("xuml/layout.xuml#create");
            context.addModel("location", entity.getDocumentId() + "?version=" + entity.getVersion() + "&action=edit");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (Exception e) {
            throw new ActionError(DOCS_9002, e, context.getParameter("title"));
        }
    }

    /**
     *
     */
    @ActionMethod
    public Object select(ActionContext context) throws DocumentException {
        try {
            XumlView view = XumlView.create("xuml/layout.xuml#doctype-selector-dialog");
            context.addModel("size", controllerMap.size());
            context.addModel("docTypes", controllerMap.keySet());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (Exception e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    @ActionMethod
    public Object save(ActionContext context) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(context.getPath().substring(1));
        entity.setVersion(Integer.parseInt(context.getParameter("version")));
        entity.setDocType(context.getParameter("docType"));
        entity.setTitle(context.getParameter("title"));
        entity.setContent(context.getParameter("content"));
        return documentService.save(entity);
    }

    @ActionMethod
    public Object commit(ActionContext context) {
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(context.getPath().substring(1));
        entity.setVersion(0);
        entity.setDocType(context.getParameter("docType"));
        entity.setTitle(context.getParameter("title"));
        entity.setContent(context.getParameter("content"));
        return documentService.save(entity);
    }

    private MicroDOM convert(String xml) throws Exception {
        String root = "<div>" + xml + "</div>";
        ByteArrayInputStream bais = new ByteArrayInputStream(root.getBytes("UTF-8"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(bais);
        return new MicroDOM(document.getDocumentElement());
    }

    /**
     *
     */
    @ActionMethod
    public DocumentModel remove(String documentId) {
        if ("index".equals(documentId)) {
            return null;
        } else {
            try {
                return documentService.remove(documentId);
            } catch (Exception e) {
                LOG.error(DOCS_9003, e, documentId);
                return null;
            }
        }
    }

    /**
     *
     */
    protected List<DocumentModel> recents(ActionContext context) throws RepositoryException {
        return documentService.recents(context.getPath().substring(1));
    }
    
}
