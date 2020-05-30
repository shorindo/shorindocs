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
package com.shorindo.xuml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class XumlBuilder extends DOMBuilder {
    private static HTMLBuilder html = new HTMLBuilder();

    public Element window() {
        return new WindowElement();
    }

//    public Element dialog() {
//        return new DialogElement();
//    }
    
    public Element vbox() {
        return new VBoxElement();
    }
    
    public Element hbox() {
        return new HBoxElement();
    }

    public Element listbox() {
        return new ListBoxElement();
    }

    public Element listitem() {
        return new ListItemElement();
    }
    
    public Element link() {
        return new LinkElement();
    }
    
    public Element description(String message) {
        return html.div()
            .attr("class", "xuml-description")
            .add(html.text(message));
    }
    
    protected class WindowElement extends Element {
        protected WindowElement() {
            super("window");
        }
        public void render(OutputStream os) throws IOException {
            html.html()
                .add(html.head()
                    .add(html.meta()
                        .attr("htt-equiv", "Content-Type")
                        .attr("content", "text/html; charset=UTF-8"))
                        .add(html.title(getAttr("title")))
                        .add(html.link()
                            .attr("rel", "stylesheet")
                            .attr("type", "text/css")
                            .attr("href", "/docs/css/xuml.css"))
                        .add(html.script()
                            .attr("type", "text/javascript")
                            .attr("src", "/docs/js/xuml.js")))
                .add(html.body()
                    .eval(getAttrs(), (self,attrs) -> {
                        Set<String> classes = new HashSet<>();
                        String height = attrs.get("height");
                        if ("fill".equals(height)) {
                            classes.add("xuml-height-fill");
                        } else if ("auto".equals(height)) {
                            classes.add("xuml-height-auto");
                        }
                        String width = attrs.get("width");
                        if ("fill".equals(width)) {
                            classes.add("xuml-width-fill");
                        } else if ("auto".equals(width)) {
                            classes.add("xuml-width-auto");
                        }
                        self.attr("class", String.join(" ", classes));
                    })
                    .eval(getChildList(), (self, elements) -> {
                        for (Element el : elements) {
                            self.add(el);
                        }
                    }))
                    .render(os);
        }
    }
    
//    protected static class DialogElement extends Element {
//        protected DialogElement() {
//            super("dialog");
//        }
//        public void render(OutputStream os) throws IOException {
//            html.div()
//                .attr("class", "xuml-dialog-pane")
//                .add(html.div()
//                    .attr("class", "xuml-dialog")
//                    .add(html.div()
//                        .attr("class", "xuml-dialog-head")
//                        .add(html.text(getAttr("title"))))
//                    .add(html.div()
//                        .attr("class", "xuml-dialog-body")
//                        .eval(getChildList(), (self, elements) -> {
//                            for (Element el : elements) {
//                                self.add(el);
//                            }
//                        })))
//               .render(os);
//        }
//    }
    
    public static class VBoxElement extends Element {

        protected VBoxElement() {
            super("vbox");
        }
        public void render(OutputStream os) throws IOException {
            html.div()
                .attr("class", "xuml-vbox")
                .eval(getAttrs(), (self,attrs) -> {
                    String width = attrs.get("width");
                    if (width != null) {
                        self.attr("style", "width:" + width);
                    }
                })
                .eval(getChildList(), (self, elements) -> {
                    for (Element el : elements) {
                        self.add(el);
                    }
                })
                .render(os);
        }
    }
    
    public static class HBoxElement extends Element {

        protected HBoxElement() {
            super("hbox");
        }
        public void render(OutputStream os) throws IOException {
            html.div()
                .attr("class", "xuml-hbox")
                .eval(getAttrs(), (self,attrs) -> {
                    String width = attrs.get("width");
                    if (width != null) {
                        self.attr("style", "width:" + width);
                    }
                })
                .eval(getChildList(), (self, elements) -> {
                    for (Element el : elements) {
                        self.add(el);
                    }
                })
                .render(os);
        }
    }
    
    public static class ListBoxElement extends Element {

        protected ListBoxElement() {
            super("listbox");
        }
        public void render(OutputStream os) throws IOException {
            html.ul()
                .attr("class", "xuml-listbox")
                .eval(getChildList(), (self, elements) -> {
                    for (Element el : elements) {
                        self.add(el);
                    }
                })
                .render(os);
        }
    }

    public static class ListItemElement extends Element {

        protected ListItemElement() {
            super("listitem");
        }
        public void render(OutputStream os) throws IOException {
            html.li()
                .attr("class", "xuml-listitem")
                .eval(getChildList(), (self, elements) -> {
                    for (Element el : elements) {
                        self.add(el);
                    }
                })
                .render(os);
        }
    }

    public static class LinkElement extends Element {

        protected LinkElement() {
            super("link");
        }
        public void render(OutputStream os) throws IOException {
            html.a()
                .attr("href", getAttr("href"))
                .eval(getChildList(), (self, elements) -> {
                    for (Element el : elements) {
                        self.add(el);
                    }
                })
                .render(os);
        }
    }
}
