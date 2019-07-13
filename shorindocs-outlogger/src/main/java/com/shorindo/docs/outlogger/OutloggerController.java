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

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.annotation.ContentType;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ContentType("application/x-outlogger")
public class OutloggerController extends DocumentController {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(OutloggerController.class);
    private OutloggerServiceImpl outloggerService =
            OutloggerFactory.outloggerService();

    public OutloggerController() {
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View view(ActionContext context) {
        try {
            DocumentModel model = getModel(context);
            String content = model.getContent() == null ? "" : model.getContent();
            OutloggerMetaData metaData = JAXB.unmarshal(new StringReader(content), OutloggerMetaData.class);
            context.setAttribute("document", model);
            context.setAttribute("outlogger", metaData);
            context.setAttribute("recents", recents(context));
            OutloggerEntity key = new OutloggerEntity();
            key.setDocumentId(model.getDocumentId());
            context.setAttribute("logs", outloggerService.listLog(key));
            return XumlView.create(getClass());
        } catch (Exception e) {
            LOG.error(OLOG_9999, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public List<OutloggerEntity> listLog(ActionContext context) throws DocumentException {
        LOG.debug("listLog");
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(context.getId());
        return outloggerService.listLog(entity);
    }

    @ActionMethod
    public void putLog(ActionContext context) throws DocumentException {
        LOG.debug("putLog");
//        entity.setDocumentId(context.getId());
//        outloggerService.putLog(entity);
    }

    @ActionMethod
    public void removeLog(ActionContext context) throws DocumentException {
        LOG.debug("removeLog");
//        OutloggerEntity entity;
//        return outloggerService.removeLog(entity);
    }
}
