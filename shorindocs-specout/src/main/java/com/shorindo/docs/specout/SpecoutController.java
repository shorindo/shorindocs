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
package com.shorindo.docs.specout;

import static com.shorindo.docs.specout.SpecoutMessages.*;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentType;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.JsonView;
import com.shorindo.docs.view.AbstractView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentType("application/x-specout")
public class SpecoutController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(SpecoutController.class);

    public SpecoutController() {
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View action(ActionContext context) {
        long st = System.currentTimeMillis();
        LOG.debug(SPEC_9003);
        try {
            //DocumentModel model = getModel(context);
            //String content = model.getContent() == null ? "" : model.getContent();
            //SpecoutEntity specout = JAXB.unmarshal(new StringReader(content), SpecoutEntity.class);
            //context.setAttribute("document", model);
            //context.setAttribute("specout", specout);
            //context.setAttribute("recents", recents(context));
            DocumentModel model = (DocumentModel)context.getAttribute("document");
            return new SpecoutView(model);
        } catch (Exception e) {
            LOG.error(SPEC_9001, e);
            return new ErrorView(500);
        } finally {
            LOG.debug(SPEC_9004, System.currentTimeMillis() - st);
        }
    }

    /**
     * 
     * @param context
     * @return
     */
    @ActionMethod
    public AbstractView edit(ActionContext context) {
        DocumentEntity model = (DocumentEntity)context.getAttribute("document");
        SpecoutEntity specout = JAXB.unmarshal(new StringReader(model.getContent()), SpecoutEntity.class);
        return new JsonView(specout, context);
    }
}
