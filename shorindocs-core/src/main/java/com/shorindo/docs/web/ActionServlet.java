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
package com.shorindo.docs.web;

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionError;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.view.DefaultView;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;

/**
 * 
 */
public class ActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionServlet.class);
    private static RepositoryService repositoryService = ApplicationContext.getBean(RepositoryService.class);
    private static AuthenticateService authenticateService = ApplicationContext.getBean(AuthenticateService.class);
    private static DocumentService documentService = ApplicationContext.getBean(DocumentService.class);

    /**
     * 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        LOG.info(DOCS_1105, req.getMethod() + " " + req.getServletPath());
        long st = System.currentTimeMillis();
        ActionContext context = new ActionContext()
            .method(req.getMethod())
            .path(req.getServletPath())
            .contextPath(req.getContextPath())
            .contentType(req.getHeader("Conetnt-Type"))
            .queryString(req.getQueryString())
            .user(authenticateService.getUser());;

        try {
            doContent(context, req, res);
        } catch (Throwable e) {
            LOG.error(DOCS_9999, e);
            output(context, res, new ErrorView(500));
        } finally {
            LOG.info(DOCS_1106, req.getMethod() + " " + req.getServletPath(),
                (System.currentTimeMillis() - st));
        }
    }

    /**
     * 
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        long st = System.currentTimeMillis();
        LOG.info(DOCS_1105, "POST " + req.getServletPath());
        ActionContext context = new ActionContext()
            .method(req.getMethod())
            .path(req.getServletPath())
            .contextPath(req.getContextPath())
            .contentType(req.getHeader("Conetnt-Type"))
            .queryString(req.getQueryString())
            .user(authenticateService.getUser());;

        try {
            DocumentEntity key = new DocumentEntity();
            String path = req.getServletPath();
            key.setDocumentId(path.substring(1));
            String version = context.getParameter("version");
            if (version == null || !version.matches("^\\-[0-9]+$")) {
                version = "0";
            }
            key.setVersion(Integer.parseInt(version));
            DocumentEntity entity = repositoryService.get(key);
            if (entity == null) {
                output(context, res, new ErrorView(404));
                return;
            }

            ActionController controller = DocumentController.getController(entity.getDocType());
            if ("application/json".equals(req.getContentType())) {
                res.setContentType("application/json");
                doRpc(context, controller, req.getInputStream(), res.getOutputStream());
            } else {
                LOG.warn(DOCS_5009);
            }
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        }

        LOG.info(DOCS_1106, "POST " + req.getServletPath(),
                (System.currentTimeMillis() - st));
    }

    private void doContent(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            doFile(context, req, res);
            doResource(context, req, res);
            doMapping(context, req, res);
            doDocument(context, req, res);
            doNotFound(context, req, res);
        } catch (SuccessException e) {
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        }
    }
    private static SuccessException SUCCESS = new SuccessException();
    private void doFile(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws SuccessException, IOException {
        File file = new File(getServletContext().getRealPath(req.getServletPath()));
        if (file.exists() && file.canRead() && file.isFile()) {
            res.setHeader("Cache-Control", "public, max-age=604800, immutable");
            output(context, res, new DefaultView(file, context));
            throw SUCCESS;
        }
    }
    private void doResource(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws SuccessException, IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(req.getServletPath())) {
            if (is != null) {
                res.setHeader("Cache-Control", "public, max-age=604800, immutable");
                output(context, res, new DefaultView(req.getServletPath(), is, context));
                throw SUCCESS;
            }
        }
    }
    private void doMapping(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws SuccessException, IOException {
        ActionController controller = (ActionController)ApplicationContext.getAction(req.getServletPath());
        if (controller != null) {
            output(context, res, controller.action(context));
            throw SUCCESS;
        }
    }
    private void doDocument(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws SuccessException, IOException, RepositoryException {
        // キャッシュ
//        ActionController controller = DocumentServiceFactory.getController(context.getPath());
//        if (controller != null) {
//            output(context, res, controller.action(context));
//            throw SUCCESS;
//        }

        DocumentModel model = documentService.load(req.getServletPath().substring(1));
//        DocumentEntity key = new DocumentEntity();
//        key.setDocumentId(req.getServletPath().substring(1));
//        key.setVersion(0);
//        String version = context.getParameter("version");
//        if (version != null && version.matches("^\\-?[0-9]+$")) {
//            key.setVersion(Integer.parseInt(version));
//        }
//        DocumentEntity entity = repositoryService.get(key);
        // NOT FOUND
        if (model == null) {
            return;
        }

        // namespace
        ActionController controller = DocumentController.getController(model.getDocType());
        if (controller != null) {
            output(context, res, controller.action(context, model));
            throw SUCCESS;
        }
    }
    private void doNotFound(ActionContext context, HttpServletRequest req, HttpServletResponse res) throws SuccessException, IOException {
        LOG.error(DOCS_5003, req.getServletPath());
        output(context, res, new ErrorView(404));
    }
    public static class SuccessException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    protected void doRpc(ActionContext context, ActionController controller, InputStream is, OutputStream os) {
        try {
            JsonRpcRequest req = JSON.decode(is, JsonRpcRequest.class);
            Method method = controller.getClass().getMethod(req.getMethod(), ActionContext.class);
            if (method.getAnnotation(ActionMethod.class) != null) {
                context.paramMap(req.getParam());
                Object result = method.invoke(controller, context);
                JsonRpcResponse res = new JsonRpcResponse();
                res.setId(req.getId());
                res.setResult(result);
                JSON.encode(res, os);
            }
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
            try {
                JsonRpcResponse res = new JsonRpcResponse();
                res.setError(e.getClass().getSimpleName());
                JSON.encode(res, os);
            } catch (Exception ex) {
                throw new ActionError(DOCS_9999, e);
            }
        } finally {
            try {
                is.close();
            } catch (Throwable th) {
                LOG.error(DOCS_9999, th);
            }
        }
    }

    /**
     * 
     */
    protected final void output(ActionContext context, HttpServletResponse res, View view) throws IOException {
        res.setStatus(view.getStatus());
        if (view.getMetaData() != null) { // FIXME
            for (Entry<String,String> entry : view.getMetaData().entrySet()) {
                res.addHeader(entry.getKey(), entry.getValue());
            }
        }
        view.render(context, res.getOutputStream());
    }

}
