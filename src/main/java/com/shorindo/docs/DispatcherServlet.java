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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shorindo.core.ActionContext;
import com.shorindo.core.ActionServlet;
import com.shorindo.core.Logger;
import com.shorindo.core.view.ErrorView;
import com.shorindo.core.view.RedirectView;
import com.shorindo.core.view.View;

/**
 * 
 */
public class DispatcherServlet extends ActionServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DispatcherServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String id = req.getServletPath().substring(1);
        ActionContext context = new ActionContext(req, res, getServletContext());
        LOG.debug("service(" + req.getServletPath() + ")");

        if (id == null || "".equals(id)) {
            output(res, new RedirectView("/index", context));
            return;
        }
        if (!dispatch(context)) {
            try {
                DocumentController controller = DocumentController.getController(id);
                View view = controller.action(context);
                output(res, view);
            } catch (DocumentException e) {
                output(res, new ErrorView(404, context));
            } catch (Throwable th) {
                LOG.error(th.getMessage(), th);
                output(res, new ErrorView(500, context));
            }
        }
    }
}
