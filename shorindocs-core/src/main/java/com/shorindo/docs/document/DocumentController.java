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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.shorindo.docs.IdentityManager;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionError;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.BeanParameter;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

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

    public final List<String> getControllerNames() {
        return new ArrayList<String>(controllerMap.keySet());
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
    public DocumentModel save(@BeanParameter(DocumentEntity.class) DocumentModel model)
            throws ActionError {
        try {
            return documentService.save(model);
        } catch (Exception e) {
            throw new ActionError(DOCS_9002, e, model.getDocumentId());
        }
    }

    /**
     *
     */
    @ActionMethod
    public View create(ActionContext context) throws DocumentException {
        String id = String.valueOf(IdentityManager.newId());

        try {
            DocumentEntity model = new DocumentEntity();
            model.setDocumentId(id);
            model.setController(getClass().getName());
            //model.setTitle(context.getParameter("title"));
            //model.setContent(context.getParameter("body"));

            if (documentService.save(model) != null) {
                return new RedirectView(id + "?action=edit");
            } else {
                return new ErrorView(404);
            }
        } catch (Throwable th) {
            LOG.error(DOCS_9002, th, id);
            return new ErrorView(500);
        }
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
