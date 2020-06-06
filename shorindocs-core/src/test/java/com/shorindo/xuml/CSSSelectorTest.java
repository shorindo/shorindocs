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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class CSSSelectorTest {
    @Test
    public void testElementSelector() throws Exception {
        CSSSelector.parse("*");
        CSSSelector.parse("a");
        CSSSelector.parse("a b c ");
        CSSSelector.parse("html body h1");
        CSSSelector.parse("html head-body");
    }

    @Test
    public void testClassSelector() throws Exception {
        CSSSelector.parse(".left");
        CSSSelector.parse(".top .main");
        CSSSelector.parse(".top.left");
        CSSSelector.parse("div.top");
        CSSSelector.parse("div.top.left");
    }

    @Test
    public void testIdSelector() throws Exception {
        CSSSelector.parse("#left");
        CSSSelector.parse("#top #main");
        CSSSelector.parse("div#main");
    }
    
    @Test
    public void testChildOperator() throws Exception {
        CSSSelector.parse("a b c");
        CSSSelector.parse("a > b");
        CSSSelector.parse("a b  >  c");
    }
    
    @Test
    public void testCombination() throws Exception {
        CSSSelector.parse("a#id-name.class-name");
    }
    
    @Test
    public void testAttrSelector() throws Exception {
        CSSSelector.parse("[name]");
        CSSSelector.parse("[name=\"foo\"]");
        CSSSelector.parse("[name~=\"foo bar baz\"]");
        CSSSelector.parse("[name|='foo']");
        CSSSelector.parse("[name^='foo']");
        CSSSelector.parse("[name$='foo']");
        CSSSelector.parse("[name*='foo']");
        CSSSelector.parse("[name=\"foo\" i]");
        CSSSelector.parse("div[name=\"foo\"]");
    }
}
