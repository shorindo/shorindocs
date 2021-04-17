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
package com.shorindo.docs.document;

import static com.shorindo.xuml.HTMLBuilder.*;

import java.util.List;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.xuml.DOMBuilder.Element;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class DocumentView extends XumlView {
    private DocumentService service = ApplicationContext.getBean(DocumentService.class);

    protected Element recents(String docId) {
        return div()
            .add(text("最近の更新"))
            .add(ul()
                .eval(docId, (self,id) -> {
                    List<DocumentModel> recents = service.recents(id);
                    for (DocumentModel m : recents) {
                        self.add(li()
                            .add(a()
                                .attr("href", m.getDocumentId())
                                .add(text(m.getTitle()))));
                    }
                }));
    }
}
