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

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocumentController;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentTypeReady;
import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.JsonView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentTypeReady("com.shorindo.docs.specout.SpecoutController")
public class SpecoutController extends DocumentController {
    private static final ActionLogger LOG = ActionLogger.getLogger(SpecoutController.class);

    public SpecoutController() {
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View view(ActionContext context) {
        try {
            DocumentEntity model = getModel(context);
            String content = model.getContent() == null ? "" : model.getContent();
            SpecoutEntity specout = JAXB.unmarshal(new StringReader(content), SpecoutEntity.class);
            context.setAttribute("specout", specout);
            context.setAttribute("recents", recents());
            return XumlView.create(getClass());
        } catch (Exception e) {
            LOG.error(SPEC_9001, e);
            return new ErrorView(500);
        }
    }

    /**
     * 
     * @param context
     * @return
     */
    @ActionMethod
    public View edit(ActionContext context) {
        DocumentEntity model = (DocumentEntity)context.getAttribute("document");
        SpecoutEntity specout = JAXB.unmarshal(new StringReader(model.getContent()), SpecoutEntity.class);
        return new JsonView(specout, context);
    }
}
