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
package com.shorindo.docs.specout;

import static com.shorindo.xuml.DOMBuilder.text;
import static com.shorindo.xuml.HTMLBuilder.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.document.DocumentView;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.specout.SpecoutEntity.Change;
import com.shorindo.docs.specout.SpecoutEntity.Reference;
import com.shorindo.docs.specout.SpecoutEntity.Spec;

/**
 * 
 */
public class SpecoutView extends DocumentView {
    private DocumentModel model;

    public SpecoutView(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void render(ActionContext ctx, OutputStream os) throws IOException {
        SpecoutEntity specout = JAXB.unmarshal(new StringReader(model.getContent()), SpecoutEntity.class);
        layout()
            .put("header", text(model.getTitle()))
            .put("meta", meta())
            .put("menubar-left", button("新規"))
            .put("menubar-left", button("編集")
                .attr("onclick", "location='?action=edit'"))
            .put("left", recents(model.getDocumentId()))
            .put("main", specout(specout))
            .put("right", text("右側ペイン"))
            .put("footer", text("Powered by shorindo.com"))
            .render(os);
    }
    
    private Element meta() {
        return style()
            .attr("type", "text/css")
            .add(text(
            "table.specout {\n" +
            "  table-layout:fixed;\n" +
            "  border-collapse:collapse;\n" +
            "  border:2px solid gray;\n" +
            "  margin-left:5px;\n" +
            "  margin-right:20px;\n" +
            "  margin-bottom:20px;\n" +
            "  position:relative;\n" +
            "  width: calc(100%% - 40px);\n" +
            "}\n" +
            "tr.spec { line-height:1.2em; }\n" +
            "tr.spec td div.description { min-height: 40px; }\n" +
            "tr.spec td div.level-0 { margin-left:0px; }\n" +
            "tr.spec td div.level-1 { margin-left:20px; }\n" +
            "tr.spec td div.level-2 { margin-left:40px; }\n" +
            "tr.spec td div.level-3 { margin-left:60px; }\n" +
            "tr.spec td div.level-4 { margin-left:80px; }\n" +
            "tr.spec td div.level-5 { margin-left:100px; }\n" +
            "tr.spec td {\n" +
            "  border-right:1px solid gray;\n" +
            "  border-bottom:1px dashed gray;\n" +
            "  padding: 1px 3px 1px 3px;\n" +
            "  vertical-align:top;\n" +
            "}\n" +
            "tr.spec:nth-child(odd) {\n" +
            "  background:#F8F8F8;\n" +
            "}\n"));
    }
    
    private Element specout(SpecoutEntity entity) {
        return table()
            .attr("class", "specout")
            .add(col().attr("style", "width:10%; white-space:nowrap;"))
            .add(col().attr("style", "width:40%;"))
            .add(col().attr("style", "width:30%;"))
            .add(col().attr("style", "width:10%; white-space:nowrap;"))
            .add(col().attr("style", "width:5%; white-space:nowrap;"))
            .add(tbody()
                .add(tr()
                    .attr("style", "background:lightgray; text-align:center;")
                    .add(td()
                        .attr("style", "border-right:1px solid gray; border-bottom:1px solid gray;")
                        .add(text("仕様ID")))
                    .add(td()
                        .attr("style", "border-right:1px solid gray; border-bottom:1px solid gray;")
                        .add(text("仕様")))
                    .add(td()
                        .attr("style", "border-right:1px solid gray; border-bottom:1px solid gray;")
                        .add(text("目的・理由")))
                    .add(td()
                        .attr("style", "border-right:1px solid gray; border-bottom:1px solid gray;")
                        .add(text("要求元")))
                    .add(td()
                        .attr("style", "border-bottom:1px solid gray;")
                        .add(text("Ver."))))
                .eval(entity.getSpecList(), (self,specs) -> {
                    for (Spec spec : specs) {
                        self.add(tr()
                            .attr("class", "spec")
                            .add(td()
                                .attr("style", "text-align:center;")
                                .add(text(spec.getSpecId())))
                            .add(td()
                                .add(div()
                                    .attr("class", "description level-" + spec.getLevel())
                                    .add(text(spec.getDescription()))))
                            .add(td()
                                .add(text(spec.getReason())))
                            .add(td()
                                .add(text(spec.getSource())))
                            .add(td()
                                .add(text(spec.getVersion()))));
                    }
                })
                .add(tr()
                    .attr("style", "background:lightgray; border-top:1px solid gray; border-bottom:1px solid gray;")
                    .add(td()
                        .attr("colspan", "5")
                        .attr("style", "padding-left:5px;")
                        .add(text("関連資料"))))
                .add(tr()
                    .add(td()
                        .attr("colspan", "5")
                        .add(references(entity.getReferenceList()))))
                .add(tr()
                    .attr("style", "background:lightgray; border-top:1px solid gray; border-bottom:1px solid gray;")
                    .add(td()
                        .attr("colspan", "5")
                        .attr("style", "padding-left:5px;")
                        .add(text("改訂履歴"))))
                .add(tr()
                    .add(td()
                        .attr("colspan", "5")
                        .add(changes(entity.getChangeList())))));
    }
    
    private Element references(List<Reference> referenceList) {
        if (referenceList == null) return null;
        return table()
            .attr("style", "width:100%; border-collapse:collapse; background:white;")
            .add(col().attr("style", "width:40px; white-space:nowrap; text-align:right;"))
            .add(col().attr("style", "text-align:left"))
            .eval(referenceList, (self,refs) -> {
                for (Reference ref : refs) {
                    self.add(tr()
                        .add(td()
                            .attr("style", "text-align:right;min-height:1.2em;")
                            .add(text(ref.getId())))
                        .add(td()
                            .add(a()
                                .attr("href", ref.getLink())
                                .attr("target", "_blank")
                                .add(text(ref.getTitle())))));
                }
            })
            ;
    }

    private Element changes(List<Change> changeList) {
        if (changeList == null) return null;
        return table()
            .attr("style", "width:100%; border-collapse:collapse; background:white;")
            .add(col().attr("style", "width:15%; white-space:nowrap;"))
            .add(col().attr("style", "width:8%; white-space:nowrap;"))
            .add(col().attr("style", "width:8%; white-space:nowrap;"))
            .add(col().attr("style", "width:70%; white-space:nowrap;"))
            .eval(changeList, (self,changes) -> {
                for (int i = 0; i < changes.size(); i++) {
                    String bottom = i != changes.size() - 1 ?
                        "border-bottom:1px dashed gray;" : "";
                    Change change = changes.get(i);
                    self.add(tr()
                        .add(td()
                            .attr("style", "border-right:1px solid gray; text-align:center; " + bottom)
                            .add(text(change.getDate())))
                        .add(td()
                            .attr("style", "border-right:1px solid gray; text-align:center;" + bottom)
                            .add(text(change.getVersion())))
                        .add(td()
                            .attr("style", "border-right:1px solid gray; text-align:center;" + bottom)
                            .add(text(change.getPerson())))
                        .add(td()
                            .attr("style", bottom)
                            .add(text(change.getDescription()))));

                }
            })
            ;
    }
}
