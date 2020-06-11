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

import static com.shorindo.xuml.DOMBuilder.*;
import static com.shorindo.xuml.HTMLBuilder.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.shorindo.xuml.CSSSelector.CSSException;
import com.shorindo.xuml.DOMBuilder.Element;

/**
 * U .. Universal Selector
 * E .. Element Selector
 * C .. Class Selector
 * I .. Id Selector
 * 
 * U
 * E
 * C
 * I
 * UU x
 * UE
 * UC
 * UI
 * EU x
 * EE x
 * EC
 * EI
 * CU x
 * CE x
 * CC
 * CI
 * IU x
 * IE x
 * IC
 * II
 * 
 * 
 * DESCENDANT_COMBINATOR
 * U U
 * U E
 * U C
 * U I
 * E U
 * E E
 * E C
 * E I
 * I U
 * I E
 * I C
 * I I
 * 
 * CHILD COMBINATOR
 * U > U
 * U > E
 * U > C
 * U > I
 * E > U
 * E > E
 * E > C
 * E > I
 * I > U
 * I > E
 * I > C
 * I > I
 * 
 */
public class CSSSelectorTest {
    @Test
    public void testElementSelector() throws Exception {
        assertEquals(
            "(CSS_SELECTOR (CSS_GROUP (DESCENDANT_COMBINATOR (UNIVERSAL_SELECTOR))))",
            CSSSelector.parseCSS("*").toString());
        assertEquals(
            "(CSS_SELECTOR (CSS_GROUP (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR))))",
            CSSSelector.parseCSS("a").toString());
        assertEquals(
            "(CSS_SELECTOR (CSS_GROUP (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR)) (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR)) (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR))))",
            CSSSelector.parseCSS("a b c").toString());
        assertEquals(
            "(CSS_SELECTOR (CSS_GROUP (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR)) (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR)) (DESCENDANT_COMBINATOR (ELEMENT_SELECTOR))))",
            CSSSelector.parseCSS("html body h1").toString());
        try {
            CSSSelector.parseCSS("html head-body");
            fail();
        } catch (CSSException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testClassSelector() throws Exception {
        CSSSelector.parse(".left");
        CSSSelector.parse(".top .main");
        CSSSelector.parse(".top.left");
        CSSSelector.parse("div.top");
        CSSSelector.parse("div.top.left");
        CSSSelector.parse("*.top.left");
    }

    @Test
    public void testIdSelector() throws Exception {
        CSSSelector.parse("#left");
        CSSSelector.parse("#top #main");
        CSSSelector.parse("div#main");
        CSSSelector.parse("*#main");
        CSSSelector.parse("html body *#menubar");
    }
    
    @Test
    public void testChildOperator() throws Exception {
        CSSSelector.parse("a b c");
        CSSSelector.parse("a > b");
        CSSSelector.parse("a b  >  c");
    }
    
    @Test
    public void testCombination() throws Exception {
        CSSSelector.parseCSS("a#id-name.class-name");
    }
    
    @Test
    public void testAttrSelector() throws Exception {
        CSSSelector.parseCSS("[name]");
        CSSSelector.parse("[name=\"foo\"]");
        CSSSelector.parse("[name~=\"foo bar baz\"]");
        CSSSelector.parse("[name|='foo']");
        CSSSelector.parse("[name^='foo']");
        CSSSelector.parse("[name$='foo']");
        CSSSelector.parse("[name*='foo']");
        CSSSelector.parse("[name=\"foo\" i]");
        CSSSelector.parse("div[name=\"foo\"]");
        
        assertSelector("link", "[type~='text/html text/css text/javascript']");
    }

    @Test
    public void testHtml() throws Exception {
        assertSelector("html", "html");
        assertSelector("div", "#header-pane");
        assertSelector("div", "div#menubar");
        assertSelector("body", ".xuml-width-fill");
        assertSelector("body", ".xuml-width-fill.xuml-height-fill");
        assertSelector("div", "div.xuml-menubar-left");
        assertSelector("div", "html body #menubar");
        assertSelector("div", "div#menubar.xuml-menubar");
        assertSelector("form", "form[name='editform']");
        assertSelector("input", "form[name='editform'] [name='age']");
        assertSelector("marker", "marker[name='meta']");
        assertSelector("body", "[class|='xuml']");
        assertSelector("link", "[rel^='style']");
        assertSelector("link", "link[rel$='sheet']");
        assertSelector("link", "*[rel*='les']");
        assertSelector("html", "*");
        assertSelector("div", "div");
        assertSelector("body", "* > body");
    }
    
    @Test
    public void testChild() throws Exception {
        assertSelector("title", "html > head > title");
        assertNotSelector("title", "html > title");
    }
    
    @Test
    public void testSibling() throws Exception {
        assertSelector("script", "title ~ script");
    }

    @Test
    public void testAdjacent() throws Exception {
        assertSelector("div", "#left-pane + #main-pane");
        assertNotSelector("div", "#left-pane + #right-pane");
    }

    @Test
    public void testGroup() throws Exception {
        Element document = createDocument();
        List<Element> result = document.findByCssSelector("title, marker[name='header']");
        assertEquals("title", result.get(0).getTagName());
        assertEquals("marker", result.get(1).getTagName());
    }

    private Element createDocument() {
        return document().add(html()
            .add(head()
                .add(meta()
                    .attr("http-equiv", "Content-Type")
                    .attr("content", "text/html; charset=UTF-8"))
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
                                .attr("style", "width:200px;")
                                .add(marker("left"))
                                )
                            .add(div()
                                .attr("id", "main-pane")
                                .attr("class", "xuml-vbox")
                                .attr("flex", "1")
                                .attr("style", "overflow:auto;")
                                .add(style())
                                .add(form()
                                    .attr("name", "editform")
                                    .add(input()
                                        .attr("name", "name"))
                                    .add(input()
                                        .attr("name", "age"))
                                    .add(input()
                                        .attr("type", "submit")
                                        .attr("value", "送信"))))
                            .add(div()
                                .attr("id", "right-pane")
                                .attr("class", "xuml-vbox")
                                .attr("style", "width:200px;")
                                .add(marker("right"))
                                ))
                        .add(div()
                            .attr("id", "footer-pane")
                            .add(marker("footer"))
                            ))));
    }

    private void assertSelector(String tagName, String selector) {
        Element document = createDocument();
        List<Element> result = document.findByCssSelector(selector);
        if (result.size() == 0) {
            fail("No match!");
        }
        assertEquals(tagName, result.get(0).getTagName());
    }

    private void assertNotSelector(String tagName, String selector) {
        Element document = createDocument();
        List<Element> result = document.findByCssSelector(selector);
        if (result.size() > 0) {
            fail();
        }
    }
}
