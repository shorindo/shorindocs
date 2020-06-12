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
        assertMarkdown("<p>abc</p>", "abc\n");
        assertMarkdown("<p>a b c</p>", "a b c");
    }

    @Test
    public void testEscaped() throws Exception {
        assertMarkdown("<p>\\</p>", "\\\\");
        assertMarkdown("<p>#</p>", "\\#");
        assertMarkdown("<p>rn</p>", "\\r\\n");
        assertMarkdown("<p># 見出し１</p>", "\\# 見出し１");
    }

    @Test
    public void testSpecial() throws Exception {
        assertMarkdown("<p>&lt;&amp;&gt;</p>", "<&>");
    }

    @Test
    public void testItalic() throws Exception {
        assertMarkdown("<p>これは<i>イタリック</i>です</p>", "これは_イタリック_です");
        assertMarkdown("<p>これは<i>イタリック</i>です</p>", "これは*イタリック*です");
        assertMarkdown("<p>これは<i><b>イタリック</b></i>です</p>", "これは*__イタリック__*です");
        assertMarkdown("<p>これは<i><b>イタリック</b></i>です</p>", "これは_**イタリック**_です");
    }

    @Test
    public void testBold() throws Exception {
        assertMarkdown("<p><b>ボールド</b></p>", "__ボールド__");
        assertMarkdown("<p>これは<b>ボールド</b>です</p>", "これは__ボールド__です");
        assertMarkdown("<p>これは<b>ボールド</b>です</p>", "これは**ボールド**です");
        assertMarkdown("<p>これは<b><i>ボールド</i></b>です</p>", "これは**_ボールド_**です");
        assertMarkdown("<p>これは<b><i>ボールド</i></b>です</p>", "これは__*ボールド*__です");
    }

    @Test
    public void testHead1() throws Exception {
        assertMarkdown("<h1>見出し１</h1>", "見出し１\n======\n");
        assertMarkdown("<h1>見出し１</h1>", "見出し１\n=");
        assertMarkdown("<h1>見出し１</h1>", "# 見出し１");
        assertMarkdown("<h1>見出し１</h1>", "# 見出し１ #");
        assertMarkdown("<h1>&lt;見出し１&gt;</h1>", "# <見出し１>");
        assertMarkdown("<h1><b>見出し１</b></h1>", "# __見出し１__");
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
    public void testHead3() throws Exception {
        assertMarkdown("<h3>見出し</h3>", "### 見出し\n");
        assertMarkdown("<h3>見出し</h3>", "### 見出し ###");
    }

    @Test
    public void testHead4() throws Exception {
        assertMarkdown("<h4>見出し</h4>", "#### 見出し\n");
        assertMarkdown("<h4>見出し</h4>", "#### 見出し ####");
    }

    @Test
    public void testHead5() throws Exception {
        assertMarkdown("<h5>見出し</h5>", "##### 見出し\n");
        assertMarkdown("<h5>見出し</h5>", "##### 見出し #####");
    }

    @Test
    public void testHead6() throws Exception {
        assertMarkdown("<h6>見出し</h6>", "###### 見出し\n");
        assertMarkdown("<h6>見出し</h6>", "###### 見出し ######");
    }

    @Test
    public void testPre() throws Exception {
        assertMarkdown("<pre>フォーマット済\n</pre>", "    フォーマット済\n");
        assertMarkdown("<pre>フォーマット済\n</pre>", "    フォーマット済");
        assertMarkdown("<pre>function foo() {\n\treturn 'bar';\n}\n</pre>", 
            "    function foo() {\n\t\treturn 'bar';\n    }\n");
    }
    
    @Test
    public void testCode() throws Exception {
        assertMarkdown("<pre><code>コード</code></pre>", "```コード```");
        assertMarkdown("<pre><code>a {\n  b;\n}\n</code></pre>", "```a {\n  b;\n}\n```");
    }

    @Test
    public void testQuote() throws Exception {
        assertMarkdown("<blockquote>quote</blockquote>", "> quote");
        assertMarkdown("<blockquote>123</blockquote>", "> 1\n> 2\n> 3");
        assertMarkdown("<blockquote>123</blockquote>", "> 1\n2\n3");
        assertMarkdown("<blockquote>1<blockquote>2</blockquote>3</blockquote>", "> 1\n>> 2\n> 3");
    }

    @Test
    public void testList() throws Exception {
        assertMarkdown("<ul><li>リスト１</li></ul>", "- リスト１");
        assertMarkdown("<ul><li><b>リスト１</b></li></ul>", "- **リスト１**");
        assertMarkdown("<ul><li>リスト１</li><li>リスト２</li></ul>", "* リスト１\n*リスト２");
        assertMarkdown("<ul><li>リスト１</li><ul><li>リスト２</li></ul></ul>", "* リスト１\n  *リスト２");
        assertMarkdown("<ul><li>リスト１</li><ul><li>リスト２</li><ul><li>リスト３</li></ul></ul></ul>", "* リスト１\n  *リスト２\n    *リスト３");
        assertMarkdown("<ul><li>リスト１</li><ul><li>リスト２</li></ul><li>リスト３</li></ul>", "* リスト１\n  *リスト２\n*リスト３");

        assertMarkdown("<ol><li>番号１</li></ol>", "100. 番号１");
        assertMarkdown("<ol><li>番号１</li></ol><ul><li>リスト１</li></ul>", "1. 番号１\n* リスト１");
        assertMarkdown("<ol><li>番号１</li><ul><li>リスト１</li></ul></ol>", "1. 番号１\n  * リスト１");
        assertMarkdown("<ol><li>番号１</li><ul><li>リスト１</li></ul><li>番号２</li></ol>", "1. 番号１\n  * リスト１\n1. 番号２");
    }

    @Test
    public void testHorizontal() throws Exception {
        assertMarkdown("<hr>", "---");
        assertMarkdown("<hr>", "---\n");
        assertMarkdown("<hr>", "* * *");
        assertMarkdown("<hr>", "* _ -");
    }

    @Test
    public void testPara() throws Exception {
        assertMarkdown("<p>ab</p>", "a\nb");
        assertMarkdown("<p>a</p><p>b</p>", "a\n\nb");
    }

    @Test
    public void testLink() throws Exception {
        assertMarkdown("<p><a href=\"http://www.google.com\">Google</a></p>", "[Google](http://www.google.com)");
        assertMarkdown("<p><a href=\"http://www.google.com\">http://www.google.com</a></p>", "http://www.google.com");
        assertMarkdown("<p><a href=\"http://example.com:8080?query=name#mark\">http://example.com:8080?query=name#mark</a></p>", "http://example.com:8080?query=name#mark");
        assertMarkdown("<p><a href=\"http://shorindo.com/\">http://shorindo.com/</a></p>", "http://shorindo.com/");
        assertMarkdown("<p><a href=\"http://shorindo.com//research\">http://shorindo.com//research</a></p>", "http://shorindo.com//research");
    }

    @Test
    public void testImage() throws Exception {
        assertMarkdown("<p><img src=\"http://shorindo.com/lib/tpl/shorindo/images/shorindo-logo.png\" title=\"shorindo.com\"></p>",
            "![shorindo.com](http://shorindo.com/lib/tpl/shorindo/images/shorindo-logo.png)");
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
            "_整形済_の*テキスト*\n\n" +
            "    a\n" +
            "    b\n" +
            "    c\n" +
            "\n" +
            "__コード__**ブロック**\n\n" +
            "```\n" +
            "public interface Foo {\n" +
            "    public void bar();\n" +
            "}\n" +
            "```\n" +
            "\n" +
            "* リスト１\n" +
            "* リスト２\n" +
            "  * リスト２ー１\n" +
            "\n" +
            "1. 番号１\n" +
            "  1. 番号１ー１\n" +
            "    1. 番号１ー１ー１\n"
            );
    }

    private void assertMarkdown(String expect, String markdown) throws Exception {
        assertEquals(expect, MD.parse(markdown));
    }
}
