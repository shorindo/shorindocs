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

import java.util.List;
import java.util.Locale;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.document.DocumentEntity;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.view.ErrorView;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlView2;

/**
 * 
 */
public class OutloggerController extends DocumentController {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(OutloggerController.class);
    private OutloggerService outloggerService;

    public OutloggerController(
            DocumentService documentService,
            OutloggerService outloggerService) {
        super(documentService);
        this.outloggerService = outloggerService;
    }

    /**
     * 
     */
    @Override @ActionMethod
    public View action(ActionContext context, Object...args) {
        try {
            DocumentEntity document = (DocumentEntity)args[0];
//            OutloggerMetaData metaData = JAXB.unmarshal(new StringReader(document.getContent()), OutloggerMetaData.class);
            OutloggerEntity entity = new OutloggerEntity();
            entity.setDocumentId(document.getDocumentId());
            List<OutloggerEntity> entityList = outloggerService.listLog(entity);
            context.addModel("lang", Locale.JAPANESE);
            context.addModel("document", document);
            context.addModel("logList", entityList);
            context.addModel("recents", recents(context));
            return XumlView2.create("outlogger/xuml/outlogger.xuml");
            //return new OutloggerView();
        } catch (Exception e) {
            LOG.error(OLOG_9999, e);
            return new ErrorView(500);
        }
    }

    @ActionMethod
    public List<OutloggerEntity> listLog(ActionContext context) throws DocumentException {
        LOG.debug("listLog");
        OutloggerEntity entity = new OutloggerEntity();
        entity.setDocumentId(context.getPath().substring(1));
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
