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
package com.shorindo.docs.markdown;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class MarkdownParserTest {
    private static MarkdownParser MD = new MarkdownParser();

    @Test
    public void testChar() throws Exception {
        assertMarkdown("abc", "abc\n");
        assertMarkdown("a b c", "a b c");
    }

    @Test
    public void testEscaped() throws Exception {
        assertMarkdown("\\", "\\\\");
        assertMarkdown("#", "\\#");
        assertMarkdown("rn", "\\r\\n");
    }

    @Test
    public void testSpecial() throws Exception {
        assertMarkdown("&lt;&amp;&gt;", "<&>");
    }

    @Test
    public void testItalic() throws Exception {
        assertMarkdown("<i>イタリック</i>", "_イタリック_");
        assertMarkdown("<i>イタリック</i>", "*イタリック*");
    }

    @Test
    public void testBold() throws Exception {
        assertMarkdown("<b>ボールド</b>", "__ボールド__");
        assertMarkdown("<b>ボールド</b>", "**ボールド**");
    }

    @Test
    public void testHead1() throws Exception {
        assertMarkdown("<h1>見出し１</h1>", "見出し１\n======\n");
        assertMarkdown("<h1>見出し１</h1>", "見出し１\n=");
        assertMarkdown("<h1>見出し１</h1>", "# 見出し１");
        assertMarkdown("<h1>見出し１</h1>", "# 見出し１ #");
    }

    @Test
    public void testHead2() throws Exception {
        assertMarkdown("<h2>見出し２</h2>", "見出し２\n------\n");
        assertMarkdown("<h2>見出し２</h2>", "見出し２\n--");
        assertMarkdown("<h2>見出し２</h2>", "## 見出し２\n");
        assertMarkdown("<h2>見出し２</h2>", "## 見出し２ ##");
        assertMarkdown("<h1>見出し１</h1><h2>見出し２</h2>", "見出し１\n======\n見出し２\n------\n");
    }

    @Test
    public void testPre() throws Exception {
        assertMarkdown("<pre>フォーマット済\n</pre>", "    フォーマット済\n");
        assertMarkdown("<pre>フォーマット済\n</pre>", "    フォーマット済");
        assertMarkdown("<pre>function foo() {\n\treturn 'bar';\n}\n</pre>", 
            "    function foo() {\n\t\treturn 'bar';\n    }\n");
    }
    
    @Test
    public void testAll() throws Exception {
        MD.parse(
            "見出し１\n" +
            "========\n" +
            "\n" +
            "見出し２\n" +
            "--------\n" +
            "\n" +
            "_整形済_の*テキスト*\n" +
            "    a\n" +
            "    b\n" +
            "    c\n" +
            "\n" +
            "__コード__**ブロック**\n" +
            "```\n" +
            "public interface Foo {\n" +
            "    public void bar();\n" +
            "}\n" +
            "```\n"
            );
    }

    private void assertMarkdown(String expect, String markdown) throws Exception {
        assertEquals(expect, MD.parse(markdown));
    }
}
