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
package com.shorindo.docs;

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentServiceFactory;
import com.shorindo.docs.view.DefaultView;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ActionLogger LOG = ActionLogger.getLogger(ActionServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        LOG.info(DOCS_1105, "GET " + req.getServletPath());
        long st = System.currentTimeMillis();
        String path = req.getServletPath();
        String documentId = path.substring(1);
        ActionContext context = new ActionContext();
        context.setAttribute("requestPath", req.getServletPath());
        context.setAttribute("contextPath", req.getServletContext().getContextPath());
        context.setAttribute("documentId", req.getServletPath().substring(1));
        context.setId(req.getServletPath().substring(1));

        try {
            if (documentId == null || "".equals(documentId)) {
                output(context, res, new RedirectView("/index", context));
                return;
            }

            File file = new File(getServletContext().getRealPath(documentId));
            if (documentId.endsWith(".xuml") && file.exists()) {
                View view = new XumlView(file.getName(), new FileInputStream(file));
                output(context, res, view);
            } else if (file.exists()) {
                output(context, res, new DefaultView(file, context));
            } else {
                ActionController controller = DocumentServiceFactory.getController((String)context.getAttribute("requestPath"));
                if (controller != null) {
                    output(context, res, controller.view(context));
                } else {
                    LOG.error(DOCS_5003, path);
                    output(context, res, new ErrorView(404));
                }
            }

            LOG.info(DOCS_1106, "GET " + req.getServletPath(),
                    (System.currentTimeMillis() - st));
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
            output(context, res, new ErrorView(500));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        long st = System.currentTimeMillis();
        LOG.info(DOCS_1105, "POST " + req.getServletPath());
        ActionContext context = new ActionContext();
        context.setAttribute("requestPath", req.getServletPath());
        context.setAttribute("contextPath", req.getContextPath());
        context.setId(req.getServletPath().substring(1));
        if ("application/json".equals(req.getContentType())) {
            res.setContentType("application/json");
            doRpc(context, req.getInputStream(), res.getOutputStream());
        } else {
            LOG.warn(DOCS_5009);
        }
        LOG.info(DOCS_1106, "POST " + req.getServletPath(),
                (System.currentTimeMillis() - st));
    }

    protected void doRpc(ActionContext context, InputStream is, OutputStream os) {
        ActionController controller = DocumentServiceFactory.getController((String)context.getAttribute("requestPath"));
        try {
            JsonRpcRequest req = JSON.decode(is, JsonRpcRequest.class);
            context.setAction(req.getMethod());
            context.setParameters(req.getParams());
            Object result = controller.action(context);
            JsonRpcResponse res = new JsonRpcResponse();
            res.setId(req.getId());
            res.setResult(result);
            JSON.encode(res, os);
        } catch (JSONException e) {
            LOG.error(DOCS_9999, e);
        } catch (IOException e) {
            LOG.error(DOCS_9999, e);
        } finally {
            try {
                is.close();
            } catch (Throwable th) {
                LOG.error(DOCS_9999, th);
            }
        }
    }

    protected boolean dispatch(ActionContext context, HttpServletResponse res)
            throws ServletException, IOException {
        String requestPath = (String)context.getAttribute("requestPath");
        File file = new File(getServletContext().getRealPath(requestPath));

        if (file.exists()) {
            output(context, res, new DefaultView(file, context));
            return true;
        }
        
        ActionController controller = DocumentServiceFactory.getController(requestPath);
        if (controller != null) {
            output(context, res, controller.view(context));
            return true;
        }
        
        return false;
    }

    protected final void output(ActionContext context, HttpServletResponse res, View view) throws IOException {
        for (Entry<String,String> entry : view.getMeta().entrySet()) {
            res.setHeader(entry.getKey(), entry.getValue());
        }
        res.setStatus(view.getStatus());
        res.setContentType(view.getContentType());
        view.render(context, res.getOutputStream());
    }

}
