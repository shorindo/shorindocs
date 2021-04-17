/*
 * Copyright 2020 Shorindo, Inc.
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

import static com.shorindo.xuml.HTMLBuilder.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentView;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public class OutloggerView extends DocumentView {
    private OutloggerService service = ApplicationContext.getBean(OutloggerService.class);

    @Override
    public void render(ActionContext ctx, OutputStream os) throws IOException {
        DocumentModel model = service.load(ctx.getId());
        OutloggerMetaData metaData = JAXB.unmarshal(new StringReader(model.getContent()), OutloggerMetaData.class);
        layout()
            .put("menubar-left", button("新規"))
            .put("menubar-left", button("編集"))
            .put("left", text("左"))
            .put("left", recents(ctx.getId()))
            .put("main", getLogs(ctx.getId()))
            .render(os);
    }

    private Element getLogs(String docId) throws IOException {

        try {
            OutloggerEntity key = new OutloggerEntity();
            key.setDocumentId(docId);
            List<OutloggerEntity> logs = service.listLog(key);
            return ul()
                .eval(logs, (self,entities) -> {
                    for (OutloggerEntity entity : entities) {
                        self.add(li()
                            .add(text(entity.getContent())));
                    }
                });
        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

}
