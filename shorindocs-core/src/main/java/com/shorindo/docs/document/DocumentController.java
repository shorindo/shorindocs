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

    public static void addController(String namespace, DocumentController controller) {
        if (controllerMap.containsKey(namespace)) {
            LOG.warn("[{0}]は既に登録されているため無視します。", namespace);
        } else {
            LOG.info("ドキュメントタイプ[{0}]に[{1}]を登録します。", namespace, controller.getClass());
            controllerMap.put(namespace, controller);
        }
    }

    public static DocumentController getController(String namespace) {
        LOG.debug("namespace={0}", namespace);
        return controllerMap.get(namespace);
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
        LOG.debug("create({0}, {1})",
            context.getParameterAsString("namespace"),
            context.getParameterAsString("title"));
        try {
            DocumentEntity model = new DocumentEntity();
            model.setDocType(context.getParameterAsString("docType"));
            model.setTitle(context.getParameterAsString("title"));
            DocumentEntity entity = (DocumentEntity) documentService.create(model);
            XumlView view = XumlView.create("xuml/layout.xuml#create");
            context.addModel("location", entity.getDocumentId());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            view.render(context, baos);
            return convert(baos.toString("UTF-8"));
        } catch (Exception e) {
            throw new ActionError(DOCS_9002, e, context.getParameterAsString("title"));
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
        } catch (Throwable th) {
            //LOG.error(DOCS_9002, th, id);
            //return new ErrorView(500);
            return null;
        }
    }

    private MicroDOM convert(String xml) throws Exception {
        String root = "<div>" + xml + "</div>";
        ByteArrayInputStream bais = new ByteArrayInputStream(root.getBytes("UTF-8"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(bais);
//        return convert(document.getDocumentElement());
        return new MicroDOM(document.getDocumentElement());
    }

//    private String convert(Node node) {
//        if ("#text".equals(node.getNodeName())) {
//            return "'" + node.getNodeValue() + "'";
//        } else if ("#comment".equals(node.getNodeName())) {
//            return "'" + node.getNodeValue() + "'";
//        }
//        StringBuilder sb = new StringBuilder("{'name':'" + node.getNodeName() + "'");
//        NodeList childNodes = node.getChildNodes();
//        if (childNodes.getLength() > 0) {
//            String sep = "";
//            sb.append(",'child':[");
//            for (int i = 0; i < childNodes.getLength(); i++) {
//                if (isEmpty(childNodes.item(i))) {
//                    continue;
//                }
//                sb.append(sep + convert(childNodes.item(i)));
//                sep = ",";
//            }
//            sb.append("]");
//        }
//        sb.append("}");
//        return sb.toString();
//    }

//    private boolean isEmpty(Node node) {
//        if ("#text".equals(node.getNodeName())) {
//            if (node.getNodeValue().matches("^\\s+$")) {
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }

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
