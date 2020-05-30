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
package com.shorindo.xuml;

import static com.shorindo.xuml.HTMLBuilder.*;
import static com.shorindo.xuml.XumlMessages.*;

import java.io.IOException;
import java.io.OutputStream;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.DOMBuilder.Element;

/**
 * 
 */
public class XumlView implements View {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlView.class);
    private Element element;

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public void render(ActionContext context, OutputStream os) throws IOException {
        long st = System.currentTimeMillis();
        LOG.debug(XUML_1001, "render");
        try {
            element.render(os, false, 0);
        } catch (IOException e) {
            LOG.error(XUML_5200);
        }
        LOG.debug(XUML_1002, "render", System.currentTimeMillis() - st);
    }

    public static final Element layout() {
        return html()
            .add(head()
                .add(meta()
                    .attr("htt-equiv", "Content-Type")
                    .attr("content", "text/html; charset=UTF-8"))
                .add(title()
                    .add(include("title")))
                .add(link()
                    .attr("rel", "stylesheet")
                    .attr("type", "text/css")
                    .attr("href", "/docs/css/xuml.css"))
                .add(script()
                    .attr("type", "text/javascript")
                    .attr("src", "/docs/js/xuml.js"))
                .add(script()
                    .attr("type", "text/javascript")
                    .add(include("script")))
                .add(style()
                    .attr("type", "text/css")
                    .add(include("style"))))
            .add(body()
                .attr("class", "xuml-width-fill xuml-height-fill")
//                .eval(getAttrs(), (self,attrs) -> {
//                    Set<String> classes = new HashSet<>();
//                    String height = attrs.get("height");
//                    if ("fill".equals(height)) {
//                        classes.add("xuml-height-fill");
//                    } else if ("auto".equals(height)) {
//                        classes.add("xuml-height-auto");
//                    }
//                    String width = attrs.get("width");
//                    if ("fill".equals(width)) {
//                        classes.add("xuml-width-fill");
//                    } else if ("auto".equals(width)) {
//                        classes.add("xuml-width-auto");
//                    }
//                    self.attr("class", String.join(" ", classes));
//                })
                .add(div()
                    .attr("class", "xuml-vbox")
                    .add(div()
                        .add(include("header")))
                        .add(div()
                            .attr("class", "xuml-hbox")
                            .add(div()
                                .attr("class", "xuml-vbox")
                                .attr("style", "width:200px;")
                                //.add(text("left"))
                                .add(include("left")))
                                .add(div()
                                    .attr("class", "xuml-vbox")
                                    .attr("flex", "1")
                                    .attr("style", "overflow:auto;")
                                    //.add(text("main"))
                                    .add(style())
                                    .add(include("main")))
//                                .add(div()
//                                    .attr("class", "xuml-vbox")
//                                    .attr("style", "width:200px;")
//                                    .add(text("right"))
//                                    .add(include("right"))))
//                                .add(div()
//                                    .add(text("footer"))))
                            )));
    }
    
    public final Element dialog() {
        return div()
            .attr("class", "xuml-dialog-pane")
            .add(div()
                .attr("class", "xuml-dialog")
                .add(div()
                    .attr("class", "xuml-dialog-head")
                    .add(include("title")))
                .add(div()
                    .attr("class", "xuml-dialog-body")
                    .add(include("body"))));
    }
}
