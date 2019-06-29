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

import java.sql.SQLException;
import java.util.List;

import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionError;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
public abstract class DocumentController extends ActionController {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(DocumentController.class);
    private final DocumentService documentService =
            ServiceFactory.getService(DocumentService.class);

    public static void setup(List<Class<?>> clazzList) {
        for (Class<?> clazz : clazzList) {
            LOG.info(DOCS_1120, clazz.getName());
        }
    }

    /**
     * 
     */
    protected DocumentModel getModel(ActionContext context) {
        return documentService.get(context.getId());
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public String load(ActionContext context) throws ActionError {
        try {
            DocumentModel model = getModel(context);
            if (model != null) {
                return model.getContent();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ActionError(DOCS_9999, e);
        }
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View save(ActionContext context) {
        try {
            DocumentModel model = getModel(context);
            documentService.put(model);
            return new RedirectView(context.getId(), context);
        } catch (Exception e) {
            LOG.error(DOCS_9002, context.getId());
            return new ErrorView(500);
        }
    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
//    @ActionMethod
//    public View create(ActionContext context) throws DocumentException {
//        String id = String.valueOf(IdentityProvider.newId());
//
//        try {
//            DocumentEntity model = new DocumentEntity();
//            model.setDocumentId(id);
//            model.setController(getClass().getName());
//            //model.setTitle(context.getParameter("title"));
//            //model.setContent(context.getParameter("body"));
//
//            if (repositoryService.put(model) >= 0) {
//                return new RedirectView(id + "?action=edit", context);
//            } else {
//                return new ErrorView(404);
//            }
//        } catch (RepositoryException e) {
//            LOG.error(DOCS_9002, e, id);
//            return new ErrorView(500);
//        }
//    }

    /**
     * 
     * @param context
     * @return
     * @throws DocumentException
     */
    @ActionMethod
    public View remove(ActionContext context) {
        if ("index".equals(context.getId())) {
            return new RedirectView("/index", context);
        } else {
            try {
                documentService.remove(context.getId());
                return new RedirectView("/index", context);
            } catch (Exception e) {
                LOG.error(DOCS_9003, e, context.getId());
                return new ErrorView(500);
            }
        }
    }

    /**
     * 
     * @return
     * @throws SQLException
     */
    protected List<DocumentModel> recents(ActionContext context) throws RepositoryException {
        return documentService.recents(context.getId());
    }
    
}
