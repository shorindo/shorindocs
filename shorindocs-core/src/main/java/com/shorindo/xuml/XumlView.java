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
import com.shorindo.docs.view.AbstractView;
import com.shorindo.xuml.DOMBuilder.Element;

/**
 * 
 */
public class XumlView extends AbstractView {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlView.class);
    private static final int STATUS_OK = 200;
    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    private Element element;

    public XumlView() {
        super();
        getMetaData().put("Content-Type", CONTENT_TYPE);
    }

    @Override
    public int getStatus() {
        return STATUS_OK;
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
        return document().add(html()
            .add(head()
                .add(meta()
                    .attr("http-equiv", "Content-Type")
                    .attr("content", CONTENT_TYPE))
                .add(title()
                    .add(marker("title")))
                .add(link()
                    .attr("rel", "stylesheet")
                    .attr("type", "text/css")
                    .attr("href", "/docs/css/xuml.css"))
                .add(script()
                    .attr("type", "text/javascript")
                    .attr("src", "/docs/js/xuml.js"))
                .add(marker("meta")))
            .add(body()
                .attr("class", "xuml-width-fill xuml-height-fill")
                .add(div()
                    .attr("class", "xuml-vbox")
                    .add(div()
                        .attr("id", "header-pane")
                        .add(marker("header")))
                        .add(div()
                            .attr("id", "menubar")
                            .attr("class", "xuml-menubar")
                            .add(div()
                                .attr("class", "xuml-menubar-left")
                                .add(marker("menubar-left")))
                            .add(div()
                                .attr("class", "xuml-menubar-right")
                                .add(input()
                                    .attr("type", "text"))
                                .add(button("検索"))
                                .add(marker("menubar-right"))))
                        .add(div()
                            .attr("class", "xuml-hbox")
                            .add(div()
                                .attr("id", "left-pane")
                                .attr("class", "xuml-vbox")
                                .attr("style", "width:25%;")
                                .add(marker("left"))
                                .on("RENDER_BEFORE", evt -> {
                                    for (Element e : evt.getTarget().findByCssSelector(MarkerElement.TAG + "[name='left']")) {
                                        if (e.getChildList().size() > 0) {
                                            return true;
                                        }
                                    }
                                    return false;
                                }))
                            .add(div()
                                .attr("id", "main-pane")
                                .attr("class", "xuml-vbox")
                                .attr("flex", "1")
                                .attr("style", "overflow:auto;")
                                .add(style())
                                .add(marker("main")))
                            .add(div()
                                .attr("id", "right-pane")
                                .attr("class", "xuml-vbox")
                                .attr("style", "width:25%;")
                                .add(marker("right"))
                                .on("RENDER_BEFORE", evt -> {
                                    for (Element e : evt.getTarget().findByCssSelector(MarkerElement.TAG + "[name='right']")) {
                                        if (e.getChildList().size() > 0) {
                                            return true;
                                        }
                                    }
                                    return false;
                                })))
                        .add(div()
                            .attr("id", "footer-pane")
                            .add(marker("footer"))
                            .on("RENDER_BEFORE", evt -> {
                                for (Element e : evt.getTarget().findByCssSelector(MarkerElement.TAG + "[name='footer']")) {
                                    if (e.getChildList().size() > 0) {
                                        return true;
                                    }
                                }
                                return false;
                            })))));
    }
    
    public final Element dialog() {
        return div()
            .attr("class", "xuml-dialog-pane")
            .add(div()
                .attr("class", "xuml-dialog")
                .add(div()
                    .attr("class", "xuml-dialog-head")
                    .add(marker("title")))
                .add(div()
                    .attr("class", "xuml-dialog-body")
                    .add(marker("body"))));
    }

}
