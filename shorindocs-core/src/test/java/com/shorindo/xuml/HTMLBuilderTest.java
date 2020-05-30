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

import static com.shorindo.xuml.HTMLBuilder.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * 
 */
public class HTMLBuilderTest {

    @Test
    public void test() throws Exception {
        List<String> strList = Arrays.asList("A", "B", "C", "D", "E");
        
        Element html = html()
            .attr("lang", "ja")
            .add(head()
                .add(title("こんにちわ！"))
                .add(script()
                    .attr("type", "text/javascript")
                    .attr("src", "foo.js"))
                .add(style()
                    .attr("type", "text/stylesheet")
                    .add(text("body {"))
                    .add(text("  padding:0;"))
                    .add(text("}")))
                .add(link())
                .add(base()))
            .add(body()
                .attr("disabled")
                .add(div().add(text("&ようこそ、%sさん", "<名無し>")))
                .eval(strList, (self, list) -> {
                    for (String s : list) {
                        self.add(span().add("%%%s", s));
                    }
                })
                .add(form()
                    .attr("name", "form")
                    .add(input()
                        .attr("name", "text")
                        .attr("value", "text"))
                    .add(select()
                        .attr("name", "fruit")
                        .add(option()
                            .attr("value", "apple")
                            .eval(false, (self, b) -> {
                                if (b) self.attr("selected");
                            })
                            .add("リンゴ"))
                        .add(option()
                            .attr("value", "banana")
                            .eval("banana", (self, name) -> {
                                if (name.equals("banana")) self.attr("selected");  
                            })
                            .add("バナナ"))
                        .add(option()
                            .attr("value", "orange")
                            .eval(false, (self, b) -> {
                                if (b) self.attr("selected");  
                            })
                            .add("オレンジ"))
                            .on(EventType.CHANGE, (self,event) -> {
                                System.err.println(self + " -> " + event);
                            }))
                    .eval(true, (self, val) -> {
                        Element input = input()
                            .attr("type", "checkbox");
                        if (val) {
                            input.attr("checked");
                        }
                        self.add(input);
                    }))
                .eval(2, (self, val) -> {
                    switch (val) {
                    case 1: self.add(text("dog")); break;
                    case 2: self.add(text("cat")); break;
                    case 3: self.add(text("monkey")); break;
                    }
                })
                .eval(new String[] { "X", "Y",  "Z" }, (self, param) -> {
                    self.add(text(param[0]));
                    self.add(text(param[1]));
                    self.add(text(param[2]));
                }));
        html.render(System.out, true, 0);
    }
    
    @Test
    public void testComponent() throws IOException {
        dialog("タイトル").render(System.out, true, 0);
        dialog("タイトル").render(System.out, false, 0);
    }

    public Element dialog(String title, Element...child) {
        return div()
            .attr("class", "xuml-dialog-pane")
            .add(div()
                .attr("class", "xuml-dialog")
                .add(div()
                    .attr("class", "xuml-dialog-head")
                    .add(text(title)))
                .add(div()
                    .attr("class", "xuml-dialog-body"))
                    .eval(child, (self, elements) -> {
                        for (Element element : elements) {
                            self.add(element);
                        }
                    }));
    }
}
