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

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * https://github.github.com/gfm/
 */
public class GFMTest {
    private static MarkdownParser MD = new MarkdownParser();

    @Test
    public void example001() throws Exception {
        GFM("\tfoo\tbaz\t\tbim", "<pre><code>foo\tbaz\t\tbim\n</code></pre>");
    }

    @Test
    public void example002() throws Exception {
        GFM("  \tfoo\tbaz\t\tbim", "<pre><code>foo\tbaz\t\tbim\n</code></pre>");
    }

    @Test
    public void example003() throws Exception {
        GFM("    a\ta\n    u\ta", "<pre><code>a\ta\nu\ta\n</code></pre>");
    }

    @Test
    public void example004() throws Exception {
        GFM("  - foo\n\n\tbar", "<ul><li><p>foo</p><p>bar</p></li></ul>");
    }

    @Test
    public void example005() throws Exception {
        GFM("- foo\n\n\t\tbar",
            "<ul><li><p>foo</p><pre><code>  bar\n</code></pre></li></ul>");
    }

    @Test
    public void example006() throws Exception {
        GFM(">\t\tfoo",
            "<blockquote><pre><code>  foo\n</code></pre></blockquote>");
    }

    @Test
    public void example007() throws Exception {
        GFM("-\t\tfoo", "<ul><li><pre><code>  foo\n</code></pre></li></ul>");
    }

    @Test
    public void example008() throws Exception {
        GFM("    foo\n\tbar", "<pre><code>foo\nbar\n</code></pre>");
    }

    @Test
    public void example009() throws Exception {
        GFM(" - foo\n   - bar\n\t - baz",
            "<ul><li>foo<ul><li>bar<ul><li>baz</li></ul></li></ul></li></ul>");
    }

    @Test
    public void example010() throws Exception {
        GFM("#\tFoo", "<h1>Foo</h1>");
    }

    @Test
    public void example011() throws Exception {
        GFM("*\t*\t*\t", "<hr>");
    }

    @Test
    public void example012() throws Exception {
        GFM("- `one\n- two`", "<ul><li>`one</li><li>two`</li></ul>");
    }

    @Test
    public void example013() throws Exception {
        GFM("***\n---\n___", "<hr><hr><hr>");
    }

    @Test
    public void example014() throws Exception {
        GFM("+++", "<p>+++</p>");
    }

    @Test
    public void example015() throws Exception {
        GFM("===", "<p>===</p>");
    }

    @Test
    public void example016() throws Exception {
        GFM("--\n**\n__", "<p>--\n**\n__</p>");
    }

    @Test
    public void example017() throws Exception {
        GFM(" ***\n  ***\n   ***", "<hr><hr><hr>");
    }

    @Test
    public void example018() throws Exception {
        GFM("    ***", "<pre><code>***\n</code></pre>");
    }

    @Test
    public void example019() throws Exception {
        GFM("Foo\n    ***", "<p>Foo\n***</p>");
    }

    @Test
    public void example020() throws Exception {
        GFM("_____________________________________", "<hr>");
    }

    @Test
    public void example021() throws Exception {
        GFM(" - - -", "<hr>");
    }

    @Test
    public void example022() throws Exception {
        GFM(" **  * ** * ** * **", "<hr>");
    }

    @Test
    public void example023() throws Exception {
        GFM("-     -      -      -", "<hr>");
    }

    @Test
    public void example024() throws Exception {
        GFM("- - - -    ", "<hr>");
    }

    @Test
    public void example025() throws Exception {
        GFM("_ _ _ _ a\n\na------\n\n---a---",
            "<p>_ _ _ _ a</p><p>a------</p><p>---a---</p>");
    }

    @Test
    public void example026() throws Exception {
        GFM(" *-*", "<p><em>-</em></p>");
    }

    @Test
    public void example027() throws Exception {
        GFM("- foo\n***\n- bar",
            "<ul><li>foo</li></ul><hr><ul><li>bar</li></ul>");
    }

    @Test
    public void example028() throws Exception {
        GFM("Foo\n***\nbar", "<p>Foo</p><hr><p>bar</p>");
    }

    @Test
    public void example029() throws Exception {
        GFM("Foo\n---\nbar", "<h2>Foo</h2><p>bar</p>");
    }

    @Test
    public void example030() throws Exception {
        GFM("* Foo\n* * *\n* Bar",
            "<ul><li>Foo</li></ul><hr><ul><li>Bar</li></ul>");
    }

    @Test
    public void example031() throws Exception {
        GFM("- Foo\n- * * *", "<ul><li>Foo</li><li><hr></li></ul>");
    }

    @Test
    public void example032() throws Exception {
        GFM("# foo\n## foo\n### foo\n#### foo\n##### foo\n###### foo",
            "<h1>foo</h1><h2>foo</h2><h3>foo</h3><h4>foo</h4><h5>foo</h5><h6>foo</h6>");
    }

    @Test
    public void example033() throws Exception {
        GFM("####### foo", "<p>####### foo</p>");
    }

    @Test
    public void example034() throws Exception {
        GFM("#5 bolt\n\n#hashtag", "<p>#5 bolt</p><p>#hashtag</p>");
    }

    @Test
    public void example035() throws Exception {
        GFM("\\## foo", "<p>## foo</p>");
    }

    @Test
    public void example036() throws Exception {
        GFM("# foo *bar* \\*baz\\*", "<h1>foo <em>bar</em> *baz*</h1>");
    }

    @Test
    public void example037() throws Exception {
        GFM("#                  foo                     ", "<h1>foo</h1>");
    }

    @Test
    public void example038() throws Exception {
        GFM(" ### foo\n  ## foo\n   # foo",
            "<h3>foo</h3><h2>foo</h2><h1>foo</h1>");
    }

    @Test
    public void example039() throws Exception {
        GFM("    # foo", "<pre><code># foo\n</code></pre>");
    }

    @Test
    public void example040() throws Exception {
        GFM("foo\n    # bar", "<p>foo\n# bar</p>");
    }

    @Test
    public void example041() throws Exception {
        GFM("## foo ##\n  ###   bar    ###", "<h2>foo</h2><h3>bar</h3>");
    }

    @Test
    public void example042() throws Exception {
        GFM("# foo ##################################\n##### foo ##\n",
            "<h1>foo</h1><h5>foo</h5>");
    }

    @Test
    public void example043() throws Exception {
        GFM("### foo ###     \n", "<h3>foo</h3>");
    }

    @Test
    public void example044() throws Exception {
        GFM("### foo ### b\n", "<h3>foo ### b</h3>");
    }

    @Test
    public void example045() throws Exception {
        GFM("# foo#\n", "<h1>foo#</h1>");
    }

    @Test
    public void example046() throws Exception {
        GFM("### foo \\###\n## foo #\\##\n# foo \\#\n",
            "<h3>foo ###</h3><h2>foo ###</h2><h1>foo #</h1>");
    }

    @Test
    public void example047() throws Exception {
        GFM("****\n## foo\n****\n", "<hr><h2>foo</h2><hr>");
    }

    @Test
    public void example048() throws Exception {
        GFM("Foo bar\n# baz\nBar foo\n",
            "<p>Foo bar</p><h1>baz</h1><p>Bar foo</p>");
    }

    @Test
    public void example049() throws Exception {
        GFM("## \n#\n### ###\n", "<h2></h2><h1></h1><h3></h3>");
    }

    @Test
    public void example050() throws Exception {
        GFM("Foo *bar*\n=========\n\nFoo *bar*\n---------\n",
            "<h1>Foo <em>bar</em></h1><h2>Foo <em>bar</em></h2>");
    }

    @Test
    public void example051() throws Exception {
        GFM("Foo *bar\nbaz*\n====\n", "<h1>Foo <em>bar\nbaz</em></h1>");
    }

    @Test
    public void example052() throws Exception {
        GFM("  Foo *bar\nbaz*\t\n====\n", "<h1>Foo <em>bar\nbaz</em></h1>");
    }

    @Test
    public void example053() throws Exception {
        GFM("Foo\n-------------------------\n\nFoo\n=\n",
            "<h2>Foo</h2><h1>Foo</h1>");
    }

    @Test
    public void example054() throws Exception {
        GFM("   Foo\n---\n\n  Foo\n-----\n\n  Foo\n  ===\n",
            "<h2>Foo</h2><h2>Foo</h2><h1>Foo</h1>");
    }

    @Test
    public void example055() throws Exception {
        GFM("    Foo\n    ---\n\n    Foo\n---\n",
            "<pre><code>Foo\n---\n\nFoo\n</code></pre><hr>");
    }

    @Test
    public void example056() throws Exception {
        GFM("Foo\n   ----      \n", "<h2>Foo</h2>");
    }

    @Test
    public void example057() throws Exception {
        GFM("Foo\n    ---\n", "<p>Foo\n---</p>");
    }

    @Test
    public void example058() throws Exception {
        GFM("Foo\n= =\n\nFoo\n--- -\n", "<p>Foo\n= =</p><p>Foo</p><hr>");
    }

    @Test
    public void example059() throws Exception {
        GFM("Foo  \n-----\n", "<h2>Foo</h2>");
    }

    @Test
    public void example060() throws Exception {
        GFM("Foo\\\n----\n", "<h2>Foo\\</h2>");
    }

    @Test
    public void example061() throws Exception {
        GFM("`Foo\n----\n`\n\n<a title=\"a lot\n---\nof dashes\"/>",
            "<h2>`Foo</h2><p>`</p><h2>&lt;a title=&quot;a lot</h2><p>of dashes&quot;/&gt;</p>");
    }

    @Test
    public void example062() throws Exception {
        GFM("> Foo\n---\n", "<blockquote><p>Foo</p></blockquote><hr>");
    }

    @Test
    public void example063() throws Exception {
        GFM("> foo\nbar\n===\n",
            "<blockquote><p>foo\nbar\n===</p></blockquote>");
    }

    @Test
    public void example064() throws Exception {
        GFM("- Foo\n---\n", "<ul><li>Foo</li></ul><hr>");
    }

    @Test
    public void example065() throws Exception {
        GFM("Foo\nBar\n---\n", "<h2>Foo\nBar</h2>");
    }

    @Test
    public void example066() throws Exception {
        GFM("---\nFoo\n---\nBar\n---\nBaz\n",
            "<hr><h2>Foo</h2><h2>Bar</h2><p>Baz</p>");
    }

    @Test
    public void example067() throws Exception {
        GFM("\n====\n", "<p>====</p>");
    }

    @Test
    public void example068() throws Exception {
        GFM("---\n---\n", "<hr><hr>");
    }

    @Test
    public void example069() throws Exception {
        GFM("- foo\n-----\n", "<ul><li>foo</li></ul><hr>");
    }

    @Test
    public void example070() throws Exception {
        GFM("    foo\n---\n", "<pre><code>foo\n</code></pre><hr>");
    }

    @Test
    public void example071() throws Exception {
        GFM("> foo\n-----\n", "<blockquote><p>foo</p></blockquote><hr>");
    }

    @Test
    public void example072() throws Exception {
        GFM("\\> foo\n------\n", "<h2>&gt; foo</h2>");
    }

    @Test
    public void example073() throws Exception {
        GFM("Foo\n\nbar\n---\nbaz\n", "<p>Foo</p><h2>bar</h2><p>baz</p>");
    }

    @Test
    public void example074() throws Exception {
        GFM("Foo\nbar\n\n---\n\nbaz\n", "<p>Foo\nbar</p><hr><p>baz</p>");
    }

    @Test
    public void example075() throws Exception {
        GFM("Foo\nbar\n* * *\nbaz\n", "<p>Foo\nbar</p><hr><p>baz</p>");
    }

    @Test
    public void example076() throws Exception {
        GFM("Foo\nbar\n\\---\nbaz\n", "<p>Foo\nbar\n---\nbaz</p>");
    }

    @Test
    public void example077() throws Exception {
        GFM("    a simple\n      indented code block\n",
            "<pre><code>a simple\n  indented code block\n</code></pre>");
    }

    @Test
    public void example078() throws Exception {
        GFM("  - foo\n\n    bar\n", "<ul><li><p>foo</p><p>bar</p></li></ul>");
    }

    @Test
    public void example079() throws Exception {
        GFM("1.  foo\n\n    - bar\n",
            "<ol><li><p>foo</p><ul><li>bar</li></ul></li></ol>");
    }

    @Test
    public void example080() throws Exception {
        GFM("    <a/>\n    *hi*\n\n    - one\n",
            "<pre><code>&lt;a/&gt;\n*hi*\n\n- one\n</code></pre>");
    }

    @Test
    public void example081() throws Exception {
        GFM("    chunk1\n\n    chunk2\n  \n \n \n    chunk3\n",
            "<pre><code>chunk1\n\nchunk2\n\n\n\nchunk3\n</code></pre>");
    }

    @Test
    public void example082() throws Exception {
        GFM("    chunk1\n      \n      chunk2\n",
            "<pre><code>chunk1\n  \n  chunk2\n</code></pre>");
    }

    @Test
    public void example083() throws Exception {
        GFM("Foo\n    bar\n", "<p>Foo\nbar</p>");
    }

    @Test
    public void example084() throws Exception {
        GFM("    foo\nbar\n", "<pre><code>foo\n</code></pre><p>bar</p>");
    }

    @Test
    public void example085() throws Exception {
        GFM("# Heading\n    foo\nHeading\n------\n    foo\n----\n",
            "<h1>Heading</h1><pre><code>foo\n</code></pre><h2>Heading</h2><pre><code>foo\n</code></pre><hr>");
    }

    @Test
    public void example086() throws Exception {
        GFM("        foo\n    bar\n", "<pre><code>    foo\nbar\n</code></pre>");
    }

    @Test
    public void example087() throws Exception {
        GFM("\n    \n    foo\n    \n", "<pre><code>foo\n</code></pre>");
    }

    @Test
    public void example088() throws Exception {
        GFM("    foo  \n", "<pre><code>foo  \n</code></pre>");
    }

    @Test
    public void example089() throws Exception {
        GFM("```\n<\n >\n```\n", "<pre><code>&lt;\n &gt;\n</code></pre>");
    }

    @Test
    public void example090() throws Exception {
        GFM("~~~\n<\n >\n~~~\n", "<pre><code>&lt;\n &gt;\n</code></pre>");
    }

    @Test
    public void example091() throws Exception {
        GFM("``\nfoo\n``\n", "<p><code>foo</code></p>");
    }

    @Test
    public void example092() throws Exception {
        GFM("```\naaa\n~~~\n```\n", "<pre><code>aaa\n~~~\n</code></pre>");
    }

    @Test
    public void example093() throws Exception {
        GFM("~~~\naaa\n```\n~~~\n", "<pre><code>aaa\n```\n</code></pre>");
    }

    @Test
    public void example094() throws Exception {
        GFM("````\naaa\n```\n``````\n", "<pre><code>aaa\n```\n</code></pre>");
    }

    @Test
    public void example095() throws Exception {
        GFM("~~~~\naaa\n~~~\n~~~~\n", "<pre><code>aaa\n~~~\n</code></pre>");
    }

    @Test
    public void example096() throws Exception {
        GFM("```\n", "<pre><code></code></pre>");
    }

    @Test
    public void example097() throws Exception {
        GFM("`````\n\n```\naaa\n", "<pre><code>```\naaa\n</code></pre>");
    }

    @Test
    public void example098() throws Exception {
        GFM("> ```\n> aaa\n\nbbb\n",
            "<blockquote><pre><code>aaa\n</code></pre></blockquote><p>bbb</p>");
    }

    @Test
    public void example099() throws Exception {
        GFM("```\n\n  \n```\n", "<pre><code>  \n</code></pre>");
    }

    @Test
    public void example100() throws Exception {
        GFM("```\n```\n", "<pre><code></code></pre>");
    }

    @Test
    public void example101() throws Exception {
        GFM(" ```\n aaa\naaa\n```\n", "<pre><code>aaa\naaa\n</code></pre>");
    }

    @Test
    public void example102() throws Exception {
        GFM("  ```\naaa\n  aaa\naaa\n  ```\n",
            "<pre><code>aaa\naaa\naaa\n</code></pre>");
    }

    @Test
    public void example103() throws Exception {
        GFM("   ```\n   aaa\n    aaa\n  aaa\n   ```\n",
            "<pre><code>aaa\n aaa\naaa\n</code></pre>");
    }

    @Test
    public void example104() throws Exception {
        GFM("    ```\n    aaa\n    ```\n",
            "<pre><code>```\naaa\n```\n</code></pre>");
    }

    @Test
    public void example105() throws Exception {
        GFM("```\naaa\n  ```\n", "<pre><code>aaa\n</code></pre>");
    }

    @Test
    public void example106() throws Exception {
        GFM("   ```\naaa\n  ```\n", "<pre><code>aaa\n</code></pre>");
    }

    @Test
    public void example107() throws Exception {
        GFM("```\naaa\n    ```\n", "<pre><code>aaa\n    ```\n</code></pre>");
    }

    @Test
    public void example108() throws Exception {
        GFM("``` ```\naaa\n", "<p><code> </code>\naaa</p>");
    }

    @Test
    public void example109() throws Exception {
        GFM("~~~~~~\naaa\n~~~ ~~\n", "<pre><code>aaa\n~~~ ~~\n</code></pre>");
    }

    @Test
    public void example110() throws Exception {
        GFM("foo\n```\nbar\n```\nbaz\n",
            "<p>foo</p><pre><code>bar\n</code></pre><p>baz</p>");
    }

    @Test
    public void example111() throws Exception {
        GFM("foo\n---\n~~~\nbar\n~~~\n# baz\n",
            "<h2>foo</h2><pre><code>bar\n</code></pre><h1>baz</h1>");
    }

    @Test
    public void example112() throws Exception {
        GFM("```ruby\ndef foo(x)\n  return 3\nend\n```\n",
            "<pre><code class=\"language-ruby\">def foo(x)\n  return 3\nend\n</code></pre>");
    }

    @Test
    public void example113() throws Exception {
        GFM("~~~~    ruby startline=3 $%@#$\ndef foo(x)\n  return 3\nend\n~~~~~~~\n",
            "<pre><code class=\"language-ruby\">def foo(x)\n  return 3\nend\n</code></pre>");
    }

    @Test
    public void example114() throws Exception {
        GFM("````;\n````\n", "<pre><code class=\"language-;\"></code></pre>");
    }

    @Test
    public void example115() throws Exception {
        GFM("``` aa ```\nfoo\n", "<p><code>aa</code>\nfoo</p>");
    }

    @Test
    public void example116() throws Exception {
        GFM("~~~ aa ``` ~~~\nfoo\n~~~\n",
            "<pre><code class=\"language-aa\">foo\n</code></pre>");
    }

    @Test
    public void example117() throws Exception {
        GFM("```\n``` aaa\n```\n", "<pre><code>``` aaa\n</code></pre>");
    }

    @Test
    public void example118() throws Exception {
        GFM("<table><tr><td>\n<pre>\n**Hello**,\n\n_world_.\n</pre>\n</td></tr></table>",
            "<table><tr><td>\n<pre>\n**Hello**,\n<p><em>world</em>.\n</pre></p>\n</td></tr></table>");
    }

    @Test
    public void example119() throws Exception {
        GFM("<table>\n  <tr>\n    <td>\n           hi\n    </td>\n  </tr>\n</table>\n\nokay.\n",
            "<table>\n  <tr>\n    <td>\n           hi\n    </td>\n  </tr>\n</table>\n<p>okay.</p>");
    }

    @Test
    public void example120() throws Exception {
        GFM(" <div>\n  *hello*\n         <foo><a>",
            " <div>\n  *hello*\n         <foo><a>");
    }

    @Test
    public void example121() throws Exception {
        GFM("</div>\n*foo*\n", "</div>\n*foo*\n");
    }

    @Test
    public void example122() throws Exception {
        GFM("<DIV CLASS=\"foo\">\n\n*Markdown*\n\n</DIV>",
            "<DIV CLASS=\"foo\">\n<p><em>Markdown</em></p></DIV>");
    }

    @Test
    public void example123() throws Exception {
        GFM("<div id=\"foo\"\n  class=\"bar\">\n</div>",
            "<div id=\"foo\"\n  class=\"bar\">\n</div>");
    }

    @Test
    public void example124() throws Exception {
        GFM("<div id=\"foo\" class=\"bar\n  baz\">\n</div>",
            "<div id=\"foo\" class=\"bar\n  baz\">\n</div>");
    }

    @Test
    public void example125() throws Exception {
        GFM("<div>\n*foo*\n\n*bar*\n", "<div>\n*foo*\n<p><em>bar</em></p>");
    }

    @Test
    public void example126() throws Exception {
        GFM("<div id=\"foo\"\n*hi*\n", "<div id=\"foo\"\n*hi*\n");
    }

    @Test
    public void example127() throws Exception {
        GFM("<div class\nfoo\n", "<div class\nfoo\n");
    }

    @Test
    public void example128() throws Exception {
        GFM("<div *???-&&&-<---\n*foo*\n", "<div *???-&&&-<---\n*foo*\n");
    }

    @Test
    public void example129() throws Exception {
        GFM("<div><a href=\"bar\">*foo*</a></div>",
            "<div><a href=\"bar\">*foo*</a></div>");
    }

    @Test
    public void example130() throws Exception {
        GFM("<table><tr><td>\nfoo\n</td></tr></table>",
            "<table><tr><td>\nfoo\n</td></tr></table>");
    }

    @Test
    public void example131() throws Exception {
        GFM("<div></div>\n``` c\nint x = 33;\n```\n",
            "<div></div>\n``` c\nint x = 33;\n```\n");
    }

    @Test
    public void example132() throws Exception {
        GFM("<a href=\"foo\">\n*bar*\n</a>", "<a href=\"foo\">\n*bar*\n</a>");
    }

    @Test
    public void example133() throws Exception {
        GFM("<Warning>\n*bar*\n</Warning>", "<Warning>\n*bar*\n</Warning>");
    }

    @Test
    public void example134() throws Exception {
        GFM("<i class=\"foo\">\n*bar*\n</i>", "<i class=\"foo\">\n*bar*\n</i>");
    }

    @Test
    public void example135() throws Exception {
        GFM("</ins>\n*bar*", "</ins>\n*bar*");
    }

    @Test
    public void example136() throws Exception {
        GFM("<del>\n*foo*\n</del>", "<del>\n*foo*\n</del>");
    }

    @Test
    public void example137() throws Exception {
        GFM("<del>\n\n*foo*\n\n</del>", "<del>\n<p><em>foo</em></p></del>");
    }

    @Test
    public void example138() throws Exception {
        GFM("<del>*foo*</del>", "<p><del><em>foo</em></del></p>");
    }

    @Test
    public void example139() throws Exception {
        GFM("<pre language=\"haskell\"><code>import Text.HTML.TagSoup\n\nmain :: IO ()\nmain = print $ parseTags tags\n</code></pre>okay\n",
            "<pre language=\"haskell\"><code>import Text.HTML.TagSoup\n\nmain :: IO ()\nmain = print $ parseTags tags\n</code></pre><p>okay</p>");
    }

    @Test
    public void example140() throws Exception {
        GFM("<script type=\"text/javascript\">// JavaScript example\n\ndocument.getElementById(\"demo\").innerHTML = \"Hello JavaScript!\";\n</script>okay\n",
            "<script type=\"text/javascript\">// JavaScript example\n\ndocument.getElementById(\"demo\").innerHTML = \"Hello JavaScript!\";\n</script><p>okay</p>");
    }

    @Test
    public void example141() throws Exception {
        GFM("<style\n  type=\"text/css\">h1 {color:red;}\n\np {color:blue;}\n</style>okay\n",
            "<style\n  type=\"text/css\">h1 {color:red;}\n\np {color:blue;}\n</style><p>okay</p>");
    }

    @Test
    public void example142() throws Exception {
        GFM("<style\n  type=\"text/css\">\nfoo\n",
            "<style\n  type=\"text/css\">\nfoo\n");
    }

    @Test
    public void example143() throws Exception {
        GFM("> <div>> foo\n\nbar\n",
            "<blockquote><div>foo\n</blockquote><p>bar</p>");
    }

    @Test
    public void example144() throws Exception {
        GFM("- <div>\n- foo\n", "<ul><li><div></li><li>foo</li></ul>");
    }

    @Test
    public void example145() throws Exception {
        GFM("<style>p{color:red;}</style>*foo*\n",
            "<style>p{color:red;}</style><p><em>foo</em></p>");
    }

    @Test
    public void example146() throws Exception {
        GFM("<!-- foo -->*bar*\n*baz*\n",
            "<!-- foo -->*bar*\n<p><em>baz</em></p>");
    }

    @Test
    public void example147() throws Exception {
        GFM("<script>foo\n</script>1. *bar*\n",
            "<script>foo\n</script>1. *bar*\n");
    }

    @Test
    public void example148() throws Exception {
        GFM("<!-- Foo\n\nbar\n   baz -->okay\n",
            "<!-- Foo\n\nbar\n   baz --><p>okay</p>");
    }

    @Test
    public void example149() throws Exception {
        GFM("<?php\n\n  echo '>';\n\n?>okay\n",
            "<?php\n\n  echo '>';\n\n?><p>okay</p>");
    }

    @Test
    public void example150() throws Exception {
        GFM("<!DOCTYPE html>", "<!DOCTYPE html>");
    }

    @Test
    public void example151() throws Exception {
        GFM("<![CDATA[\nfunction matchwo(a,b)\n{\n  if (a < b && a < 0) then {\n    return 1;\n\n  } else {\n\n    return 0;\n  }\n}\n]]>okay\n",
            "<![CDATA[\nfunction matchwo(a,b)\n{\n  if (a < b && a < 0) then {\n    return 1;\n\n  } else {\n\n    return 0;\n  }\n}\n]]><p>okay</p>");
    }

    @Test
    public void example152() throws Exception {
        GFM("  <!-- foo -->\n    <!-- foo -->",
            "  <!-- foo -->\n<pre><code>&lt;!-- foo --&gt;\n</code></pre>");
    }

    @Test
    public void example153() throws Exception {
        GFM("  <div>\n    <div>",
            "  <div>\n<pre><code>&lt;div&gt;\n</code></pre>");
    }

    @Test
    public void example154() throws Exception {
        GFM("Foo\n<div>bar\n</div>", "<p>Foo</p><div>bar\n</div>");
    }

    @Test
    public void example155() throws Exception {
        GFM("<div>bar\n</div>*foo*\n", "<div>bar\n</div>*foo*\n");
    }

    @Test
    public void example156() throws Exception {
        GFM("Foo\n<a href=\"bar\">baz\n", "<p>Foo\n<a href=\"bar\">baz</p>");
    }

    @Test
    public void example157() throws Exception {
        GFM("<div>\n*Emphasized* text.\n\n</div>",
            "<div><p><em>Emphasized</em> text.</p></div>");
    }

    @Test
    public void example158() throws Exception {
        GFM("<div>*Emphasized* text.\n</div>",
            "<div>*Emphasized* text.\n</div>");
    }

    @Test
    public void example159() throws Exception {
        GFM("<table>\n\n<tr>\n\n<td>\nHi\n</td>\n\n</tr>\n\n</table>",
            "<table>\n<tr>\n<td>\nHi\n</td>\n</tr>\n</table>");
    }

    @Test
    public void example160() throws Exception {
        GFM("<table>\n\n  <tr>\n\n    <td>\n      Hi\n    </td>\n\n  </tr>\n\n</table>",
            "<table>\n  <tr>\n<pre><code>&lt;td&gt;\n  Hi\n&lt;/td&gt;\n</code></pre>  </tr>\n</table>");
    }

    @Test
    public void example161() throws Exception {
        GFM("[foo]: /url \"title\"\n\n[foo]\n",
            "<p><a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example162() throws Exception {
        GFM("   [foo]: \n      /url  \n           'the title'  \n\n[foo]\n",
            "<p><a href=\"/url\" title=\"the title\">foo</a></p>");
    }

    @Test
    public void example163() throws Exception {
        GFM("[Foo*bar\\]]:my_(url) 'title (with parens)'\n\n[Foo*bar\\]]\n",
            "<p><a href=\"my_(url)\" title=\"title (with parens)\">Foo*bar]</a></p>");
    }

    @Test
    public void example164() throws Exception {
        GFM("[Foo bar]:\n<my url>'title'\n\n[Foo bar]\n",
            "<p><a href=\"my%20url\" title=\"title\">Foo bar</a></p>");
    }

    @Test
    public void example165() throws Exception {
        GFM("[foo]: /url '\ntitle\nline1\nline2\n'\n\n[foo]\n",
            "<p><a href=\"/url\" title=\"\ntitle\nline1\nline2\n\">foo</a></p>");
    }

    @Test
    public void example166() throws Exception {
        GFM("[foo]: /url 'title\n\nwith blank line'\n\n[foo]\n",
            "<p>[foo]: /url 'title</p><p>with blank line'</p><p>[foo]</p>");
    }

    @Test
    public void example167() throws Exception {
        GFM("[foo]:\n/url\n\n[foo]\n", "<p><a href=\"/url\">foo</a></p>");
    }

    @Test
    public void example168() throws Exception {
        GFM("[foo]:\n\n[foo]\n", "<p>[foo]:</p><p>[foo]</p>");
    }

    @Test
    public void example169() throws Exception {
        GFM("[foo]: <>\n[foo]\n", "<p><a href=\"\">foo</a></p>");
    }

    @Test
    public void example170() throws Exception {
        GFM("[foo]: <bar>(baz)\n\n[foo]\n",
            "<p>[foo]: <bar>(baz)</p><p>[foo]</p>");
    }

    @Test
    public void example171() throws Exception {
        GFM("[foo]: /url\\bar\\*baz \"foo\\\"bar\\baz\"\n\n[foo]\n",
            "<p><a href=\"/url%5Cbar*baz\" title=\"foo&quot;bar\\baz\">foo</a></p>");
    }

    @Test
    public void example172() throws Exception {
        GFM("[foo]\n\n[foo]: url\n", "<p><a href=\"url\">foo</a></p>");
    }

    @Test
    public void example173() throws Exception {
        GFM("[foo]\n\n[foo]: first\n[foo]: second\n",
            "<p><a href=\"first\">foo</a></p>");
    }

    @Test
    public void example174() throws Exception {
        GFM("[FOO]: /url\n\n[Foo]\n", "<p><a href=\"/url\">Foo</a></p>");
    }

    @Test
    public void example175() throws Exception {
        GFM("[ΑΓΩ]: /φου\n\n[αγω]\n",
            "<p><a href=\"/%CF%86%CE%BF%CF%85\">αγω</a></p>");
    }

    @Test
    public void example176() throws Exception {
        GFM("[foo]: /url\n", "");
    }

    @Test
    public void example177() throws Exception {
        GFM("[\nfoo\n]: /url\nbar\n", "<p>bar</p>");
    }

    @Test
    public void example178() throws Exception {
        GFM("[foo]: /url \"title\" ok\n",
            "<p>[foo]: /url &quot;title&quot; ok</p>");
    }

    @Test
    public void example179() throws Exception {
        GFM("[foo]: /url\n\"title\" ok\n", "<p>&quot;title&quot; ok</p>");
    }

    @Test
    public void example180() throws Exception {
        GFM("    [foo]: /url \"title\"\n\n[foo]\n",
            "<pre><code>[foo]: /url &quot;title&quot;\n</code></pre><p>[foo]</p>");
    }

    @Test
    public void example181() throws Exception {
        GFM("```\n[foo]: /url\n```\n\n[foo]\n",
            "<pre><code>[foo]: /url\n</code></pre><p>[foo]</p>");
    }

    @Test
    public void example182() throws Exception {
        GFM("Foo\n[bar]: /baz\n\n[bar]\n",
            "<p>Foo\n[bar]: /baz</p><p>[bar]</p>");
    }

    @Test
    public void example183() throws Exception {
        GFM("# [Foo]\n[foo]: /url\n> bar\n",
            "<h1><a href=\"/url\">Foo</a></h1><blockquote><p>bar</p></blockquote>");
    }

    @Test
    public void example184() throws Exception {
        GFM("[foo]: /url\nbar\n===\n[foo]\n",
            "<h1>bar</h1><p><a href=\"/url\">foo</a></p>");
    }

    @Test
    public void example185() throws Exception {
        GFM("[foo]: /url\n===\n[foo]\n", "<p>===\n<a href=\"/url\">foo</a></p>");
    }

    @Test
    public void example186() throws Exception {
        GFM("[foo]: /foo-url \"foo\"\n[bar]: /bar-url\n  \"bar\"\n[baz]: /baz-url\n\n[foo],\n[bar],\n[baz]\n",
            "<p><a href=\"/foo-url\" title=\"foo\">foo</a>,\n<a href=\"/bar-url\" title=\"bar\">bar</a>,\n<a href=\"/baz-url\">baz</a></p>");
    }

    @Test
    public void example187() throws Exception {
        GFM("[foo]\n\n> [foo]: /url\n",
            "<p><a href=\"/url\">foo</a></p><blockquote></blockquote>");
    }

    @Test
    public void example188() throws Exception {
        GFM("[foo]: /url\n", "");
    }

    @Test
    public void example189() throws Exception {
        GFM("aaa\n\nbbb\n", "<p>aaa</p><p>bbb</p>");
    }

    @Test
    public void example190() throws Exception {
        GFM("aaa\nbbb\n\nccc\nddd\n", "<p>aaa\nbbb</p><p>ccc\nddd</p>");
    }

    @Test
    public void example191() throws Exception {
        GFM("aaa\n\n\nbbb\n", "<p>aaa</p><p>bbb</p>");
    }

    @Test
    public void example192() throws Exception {
        GFM("  aaa\n bbb\n", "<p>aaa\nbbb</p>");
    }

    @Test
    public void example193() throws Exception {
        GFM("aaa\n             bbb\n                                       ccc\n",
            "<p>aaa\nbbb\nccc</p>");
    }

    @Test
    public void example194() throws Exception {
        GFM("   aaa\nbbb\n", "<p>aaa\nbbb</p>");
    }

    @Test
    public void example195() throws Exception {
        GFM("    aaa\nbbb\n", "<pre><code>aaa\n</code></pre><p>bbb</p>");
    }

    @Test
    public void example196() throws Exception {
        GFM("aaa     \nbbb     \n", "<p>aaa<br />\nbbb</p>");
    }

    @Test
    public void example197() throws Exception {
        GFM("  \n\naaa\n  \n\n# aaa\n\n  \n", "<p>aaa</p><h1>aaa</h1>");
    }

    @Test
    public void example198() throws Exception {
        GFM("| foo | bar |\n| --- | --- |\n| baz | bim |\n",
            "<table><thead><tr><th>foo</th><th>bar</th></tr></thead><tbody><tr><td>baz</td><td>bim</td></tr></tbody></table>");
    }

    @Test
    public void example199() throws Exception {
        GFM("| abc | defghi |\n:-: | -----------:\nbar | baz\n",
            "<table><thead><tr><th align=\"center\">abc</th><th align=\"right\">defghi</th></tr></thead><tbody><tr><td align=\"center\">bar</td><td align=\"right\">baz</td></tr></tbody></table>");
    }

    @Test
    public void example200() throws Exception {
        GFM("| f\\|oo  |\n| ------ |\n| b `\\|` az |\n| b **\\|** im |\n",
            "<table><thead><tr><th>f|oo</th></tr></thead><tbody><tr><td>b <code>|</code> az</td></tr><tr><td>b <strong>|</strong> im</td></tr></tbody></table>");
    }

    @Test
    public void example201() throws Exception {
        GFM("| abc | def |\n| --- | --- |\n| bar | baz |\n> bar\n",
            "<table><thead><tr><th>abc</th><th>def</th></tr></thead><tbody><tr><td>bar</td><td>baz</td></tr></tbody></table><blockquote><p>bar</p></blockquote>");
    }

    @Test
    public void example202() throws Exception {
        GFM("| abc | def |\n| --- | --- |\n| bar | baz |\nbar\n\nbar\n",
            "<table><thead><tr><th>abc</th><th>def</th></tr></thead><tbody><tr><td>bar</td><td>baz</td></tr><tr><td>bar</td><td></td></tr></tbody></table><p>bar</p>");
    }

    @Test
    public void example203() throws Exception {
        GFM("| abc | def |\n| --- |\n| bar |\n",
            "<p>| abc | def |\n| --- |\n| bar |</p>");
    }

    @Test
    public void example204() throws Exception {
        GFM("| abc | def |\n| --- | --- |\n| bar |\n| bar | baz | boo |\n",
            "<table><thead><tr><th>abc</th><th>def</th></tr></thead><tbody><tr><td>bar</td><td></td></tr><tr><td>bar</td><td>baz</td></tr></tbody></table>");
    }

    @Test
    public void example205() throws Exception {
        GFM("| abc | def |\n| --- | --- |\n",
            "<table><thead><tr><th>abc</th><th>def</th></tr></thead></table>");
    }

    @Test
    public void example206() throws Exception {
        GFM("> # Foo\n> bar\n> baz\n",
            "<blockquote><h1>Foo</h1><p>bar\nbaz</p></blockquote>");
    }

    @Test
    public void example207() throws Exception {
        GFM("># Foo\n>bar\n> baz\n",
            "<blockquote><h1>Foo</h1><p>bar\nbaz</p></blockquote>");
    }

    @Test
    public void example208() throws Exception {
        GFM("   > # Foo\n   > bar\n > baz\n",
            "<blockquote><h1>Foo</h1><p>bar\nbaz</p></blockquote>");
    }

    @Test
    public void example209() throws Exception {
        GFM("    > # Foo\n    > bar\n    > baz\n",
            "<pre><code>&gt; # Foo\n&gt; bar\n&gt; baz\n</code></pre>");
    }

    @Test
    public void example210() throws Exception {
        GFM("> # Foo\n> bar\nbaz\n",
            "<blockquote><h1>Foo</h1><p>bar\nbaz</p></blockquote>");
    }

    @Test
    public void example211() throws Exception {
        GFM("> bar\nbaz\n> foo\n",
            "<blockquote><p>bar\nbaz\nfoo</p></blockquote>");
    }

    @Test
    public void example212() throws Exception {
        GFM("> foo\n---\n", "<blockquote><p>foo</p></blockquote><hr>");
    }

    @Test
    public void example213() throws Exception {
        GFM("> - foo\n- bar\n",
            "<blockquote><ul><li>foo</li></ul></blockquote><ul><li>bar</li></ul>");
    }

    @Test
    public void example214() throws Exception {
        GFM(">     foo\n    bar\n",
            "<blockquote><pre><code>foo\n</code></pre></blockquote><pre><code>bar\n</code></pre>");
    }

    @Test
    public void example215() throws Exception {
        GFM("> ```\nfoo\n```\n",
            "<blockquote><pre><code></code></pre></blockquote><p>foo</p><pre><code></code></pre>");
    }

    @Test
    public void example216() throws Exception {
        GFM("> foo\n    - bar\n", "<blockquote><p>foo\n- bar</p></blockquote>");
    }

    @Test
    public void example217() throws Exception {
        GFM(">", "<blockquote></blockquote>");
    }

    @Test
    public void example218() throws Exception {
        GFM(">>  \n> \n", "<blockquote></blockquote>");
    }

    @Test
    public void example219() throws Exception {
        GFM(">> foo\n>  \n", "<blockquote><p>foo</p></blockquote>");
    }

    @Test
    public void example220() throws Exception {
        GFM("> foo\n\n> bar\n",
            "<blockquote><p>foo</p></blockquote><blockquote><p>bar</p></blockquote>");
    }

    @Test
    public void example221() throws Exception {
        GFM("> foo\n> bar\n", "<blockquote><p>foo\nbar</p></blockquote>");
    }

    @Test
    public void example222() throws Exception {
        GFM("> foo\n>> bar\n", "<blockquote><p>foo</p><p>bar</p></blockquote>");
    }

    @Test
    public void example223() throws Exception {
        GFM("foo\n> bar\n", "<p>foo</p><blockquote><p>bar</p></blockquote>");
    }

    @Test
    public void example224() throws Exception {
        GFM("> aaa\n***\n> bbb\n",
            "<blockquote><p>aaa</p></blockquote><hr><blockquote><p>bbb</p></blockquote>");
    }

    @Test
    public void example225() throws Exception {
        GFM("> bar\nbaz\n", "<blockquote><p>bar\nbaz</p></blockquote>");
    }

    @Test
    public void example226() throws Exception {
        GFM("> bar\n\nbaz\n", "<blockquote><p>bar</p></blockquote><p>baz</p>");
    }

    @Test
    public void example227() throws Exception {
        GFM("> bar\n>baz\n", "<blockquote><p>bar</p></blockquote><p>baz</p>");
    }

    @Test
    public void example228() throws Exception {
        GFM("> > > foo\nbar\n",
            "<blockquote><blockquote><blockquote><p>foo\nbar</p></blockquote></blockquote></blockquote>");
    }

    @Test
    public void example229() throws Exception {
        GFM(">>> foo\n> bar\n>>baz\n",
            "<blockquote><blockquote><blockquote><p>foo\nbar\nbaz</p></blockquote></blockquote></blockquote>");
    }

    @Test
    public void example230() throws Exception {
        GFM(">     code\n\n>    not code\n",
            "<blockquote><pre><code>code\n</code></pre></blockquote><blockquote><p>not code</p></blockquote>");
    }

    @Test
    public void example231() throws Exception {
        GFM("A paragraph\nwith two lines.\n\n    indented code\n\n> A block quote.\n",
            "<p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote>");
    }

    @Test
    public void example232() throws Exception {
        GFM("1.  A paragraph\n    with two lines.\n\n        indented code\n\n    > A block quote.\n",
            "<ol><li><p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote></li></ol>");
    }

    @Test
    public void example233() throws Exception {
        GFM("- one\n\n two\n", "<ul><li>one</li></ul><p>two</p>");
    }

    @Test
    public void example234() throws Exception {
        GFM("- one\n\n  two\n", "<ul><li><p>one</p><p>two</p></li></ul>");
    }

    @Test
    public void example235() throws Exception {
        GFM(" -    one\n\n     two\n",
            "<ul><li>one</li></ul><pre><code> two\n</code></pre>");
    }

    @Test
    public void example236() throws Exception {
        GFM(" -    one\n\n      two\n",
            "<ul><li><p>one</p><p>two</p></li></ul>");
    }

    @Test
    public void example237() throws Exception {
        GFM("   > > 1.  one\n>>>>     two\n",
            "<blockquote><blockquote><ol><li><p>one</p><p>two</p></li></ol></blockquote></blockquote>");
    }

    @Test
    public void example238() throws Exception {
        GFM(">>- one\n>>  >  > two\n",
            "<blockquote><blockquote><ul><li>one</li></ul><p>two</p></blockquote></blockquote>");
    }

    @Test
    public void example239() throws Exception {
        GFM("-one\n\n2.two\n", "<p>-one</p><p>2.two</p>");
    }

    @Test
    public void example240() throws Exception {
        GFM("- foo\n\n\n  bar\n", "<ul><li><p>foo</p><p>bar</p></li></ul>");
    }

    @Test
    public void example241() throws Exception {
        GFM("1.  foo\n\n    ```\n    bar\n    ```\n\n    baz\n\n    > bam\n",
            "<ol><li><p>foo</p><pre><code>bar\n</code></pre><p>baz</p><blockquote><p>bam</p></blockquote></li></ol>");
    }

    @Test
    public void example242() throws Exception {
        GFM("- Foo\n\n      bar\n\n\n      baz\n",
            "<ul><li><p>Foo</p><pre><code>bar\n\n\nbaz\n</code></pre></li></ul>");
    }

    @Test
    public void example243() throws Exception {
        GFM("123456789. ok\n", "<ol start=\"123456789\"><li>ok</li></ol>");
    }

    @Test
    public void example244() throws Exception {
        GFM("1234567890. not ok\n", "<p>1234567890. not ok</p>");
    }

    @Test
    public void example245() throws Exception {
        GFM("0. ok\n", "<ol start=\"0\"><li>ok</li></ol>");
    }

    @Test
    public void example246() throws Exception {
        GFM("003. ok\n", "<ol start=\"3\"><li>ok</li></ol>");
    }

    @Test
    public void example247() throws Exception {
        GFM("-1. not ok\n", "<p>-1. not ok</p>");
    }

    @Test
    public void example248() throws Exception {
        GFM("- foo\n\n      bar\n",
            "<ul><li><p>foo</p><pre><code>bar\n</code></pre></li></ul>");
    }

    @Test
    public void example249() throws Exception {
        GFM("  10.  foo\n\n           bar\n",
            "<ol start=\"10\"><li><p>foo</p><pre><code>bar\n</code></pre></li></ol>");
    }

    @Test
    public void example250() throws Exception {
        GFM("    indented code\n\nparagraph\n\n    more code\n",
            "<pre><code>indented code\n</code></pre><p>paragraph</p><pre><code>more code\n</code></pre>");
    }

    @Test
    public void example251() throws Exception {
        GFM("1.     indented code\n\n   paragraph\n\n       more code\n",
            "<ol><li><pre><code>indented code\n</code></pre><p>paragraph</p><pre><code>more code\n</code></pre></li></ol>");
    }

    @Test
    public void example252() throws Exception {
        GFM("1.      indented code\n\n   paragraph\n\n       more code\n",
            "<ol><li><pre><code> indented code\n</code></pre><p>paragraph</p><pre><code>more code\n</code></pre></li></ol>");
    }

    @Test
    public void example253() throws Exception {
        GFM("   foo\n\nbar\n", "<p>foo</p><p>bar</p>");
    }

    @Test
    public void example254() throws Exception {
        GFM("-    foo\n\n  bar\n", "<ul><li>foo</li></ul><p>bar</p>");
    }

    @Test
    public void example255() throws Exception {
        GFM("-  foo\n\n   bar\n", "<ul><li><p>foo</p><p>bar</p></li></ul>");
    }

    @Test
    public void example256() throws Exception {
        GFM("-\n  foo\n-\n  ```\n  bar\n  ```\n-\n      baz\n",
            "<ul><li>foo</li><li><pre><code>bar\n</code></pre></li><li><pre><code>baz\n</code></pre></li></ul>");
    }

    @Test
    public void example257() throws Exception {
        GFM("-   \n  foo\n", "<ul><li>foo</li></ul>");
    }

    @Test
    public void example258() throws Exception {
        GFM("-\n\n  foo\n", "<ul><li></li></ul><p>foo</p>");
    }

    @Test
    public void example259() throws Exception {
        GFM("- foo\n-\n- bar\n", "<ul><li>foo</li><li></li><li>bar</li></ul>");
    }

    @Test
    public void example260() throws Exception {
        GFM("- foo\n-   \n- bar\n",
            "<ul><li>foo</li><li></li><li>bar</li></ul>");
    }

    @Test
    public void example261() throws Exception {
        GFM("1. foo\n2.\n3. bar\n",
            "<ol><li>foo</li><li></li><li>bar</li></ol>");
    }

    @Test
    public void example262() throws Exception {
        GFM("*\n", "<ul><li></li></ul>");
    }

    @Test
    public void example263() throws Exception {
        GFM("foo\n*\n\nfoo\n1.\n", "<p>foo\n*</p><p>foo\n1.</p>");
    }

    @Test
    public void example264() throws Exception {
        GFM(" 1.  A paragraph\n     with two lines.\n\n         indented code\n\n     > A block quote.\n",
            "<ol><li><p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote></li></ol>");
    }

    @Test
    public void example265() throws Exception {
        GFM("  1.  A paragraph\n      with two lines.\n\n          indented code\n\n      > A block quote.\n",
            "<ol><li><p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote></li></ol>");
    }

    @Test
    public void example266() throws Exception {
        GFM("   1.  A paragraph\n       with two lines.\n\n           indented code\n\n       > A block quote.\n",
            "<ol><li><p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote></li></ol>");
    }

    @Test
    public void example267() throws Exception {
        GFM("    1.  A paragraph\n        with two lines.\n\n            indented code\n\n        > A block quote.\n",
            "<pre><code>1.  A paragraph\n    with two lines.\n\n        indented code\n\n    &gt; A block quote.\n</code></pre>");
    }

    @Test
    public void example268() throws Exception {
        GFM("  1.  A paragraph\nwith two lines.\n\n          indented code\n\n      > A block quote.\n",
            "<ol><li><p>A paragraph\nwith two lines.</p><pre><code>indented code\n</code></pre><blockquote><p>A block quote.</p></blockquote></li></ol>");
    }

    @Test
    public void example269() throws Exception {
        GFM("  1.  A paragraph\n    with two lines.\n",
            "<ol><li>A paragraph\nwith two lines.</li></ol>");
    }

    @Test
    public void example270() throws Exception {
        GFM("> 1. > Blockquote\ncontinued here.\n",
            "<blockquote><ol><li><blockquote><p>Blockquote\ncontinued here.</p></blockquote></li></ol></blockquote>");
    }

    @Test
    public void example271() throws Exception {
        GFM("> 1. > Blockquote\n> continued here.\n",
            "<blockquote><ol><li><blockquote><p>Blockquote\ncontinued here.</p></blockquote></li></ol></blockquote>");
    }

    @Test
    public void example272() throws Exception {
        GFM("- foo\n  - bar\n    - baz\n      - boo\n",
            "<ul><li>foo<ul><li>bar<ul><li>baz<ul><li>boo</li></ul></li></ul></li></ul></li></ul>");
    }

    @Test
    public void example273() throws Exception {
        GFM("- foo\n - bar\n  - baz\n   - boo\n",
            "<ul><li>foo</li><li>bar</li><li>baz</li><li>boo</li></ul>");
    }

    @Test
    public void example274() throws Exception {
        GFM("10) foo\n    - bar\n",
            "<ol start=\"10\"><li>foo\n<ul><li>bar</li></ul></li></ol>");
    }

    @Test
    public void example275() throws Exception {
        GFM("10) foo\n   - bar\n",
            "<ol start=\"10\"><li>foo</li></ol><ul><li>bar</li></ul>");
    }

    @Test
    public void example276() throws Exception {
        GFM("- - foo\n", "<ul><li><ul><li>foo</li></ul></li></ul>");
    }

    @Test
    public void example277() throws Exception {
        GFM("1. - 2. foo\n",
            "<ol><li><ul><li><ol start=\"2\"><li>foo</li></ol></li></ul></li></ol>");
    }

    @Test
    public void example278() throws Exception {
        GFM("- # Foo\n- Bar\n  ---\n  baz\n",
            "<ul><li><h1>Foo</h1></li><li><h2>Bar</h2>baz</li></ul>");
    }

    @Test
    public void example279() throws Exception {
        GFM("- [ ] foo\n- [x] bar\n",
            "<ul><li><input disabled=\"\" type=\"checkbox\"> foo</li><li><input checked=\"\" disabled=\"\" type=\"checkbox\"> bar</li></ul>");
    }

    @Test
    public void example280() throws Exception {
        GFM("- [x] foo\n  - [ ] bar\n  - [x] baz\n- [ ] bim\n",
            "<ul><li><input checked=\"\" disabled=\"\" type=\"checkbox\"> foo\n<ul><li><input disabled=\"\" type=\"checkbox\"> bar</li><li><input checked=\"\" disabled=\"\" type=\"checkbox\"> baz</li></ul></li><li><input disabled=\"\" type=\"checkbox\"> bim</li></ul>");
    }

    @Test
    public void example281() throws Exception {
        GFM("- foo\n- bar\n+ baz\n",
            "<ul><li>foo</li><li>bar</li></ul><ul><li>baz</li></ul>");
    }

    @Test
    public void example282() throws Exception {
        GFM("1. foo\n2. bar\n3) baz\n",
            "<ol><li>foo</li><li>bar</li></ol><ol start=\"3\"><li>baz</li></ol>");
    }

    @Test
    public void example283() throws Exception {
        GFM("Foo\n- bar\n- baz\n",
            "<p>Foo</p><ul><li>bar</li><li>baz</li></ul>");
    }

    @Test
    public void example284() throws Exception {
        GFM("The number of windows in my house is\n14.  The number of doors is 6.\n",
            "<p>The number of windows in my house is\n14.  The number of doors is 6.</p>");
    }

    @Test
    public void example285() throws Exception {
        GFM("The number of windows in my house is\n1.  The number of doors is 6.\n",
            "<p>The number of windows in my house is</p><ol><li>The number of doors is 6.</li></ol>");
    }

    @Test
    public void example286() throws Exception {
        GFM("- foo\n\n- bar\n\n\n- baz\n",
            "<ul><li><p>foo</p></li><li><p>bar</p></li><li><p>baz</p></li></ul>");
    }

    @Test
    public void example287() throws Exception {
        GFM("- foo\n  - bar\n    - baz\n\n\n      bim\n",
            "<ul><li>foo\n<ul><li>bar\n<ul><li><p>baz</p><p>bim</p></li></ul></li></ul></li></ul>");
    }

    @Test
    public void example288() throws Exception {
        GFM("- foo\n- bar\n\n<!-- -->\n- baz\n- bim\n",
            "<ul><li>foo</li><li>bar</li></ul><!-- --><ul><li>baz</li><li>bim</li></ul>");
    }

    @Test
    public void example289() throws Exception {
        GFM("-   foo\n\n    notcode\n\n-   foo\n\n<!-- -->\n    code\n",
            "<ul><li><p>foo</p><p>notcode</p></li><li><p>foo</p></li></ul><!-- --><pre><code>code\n</code></pre>");
    }

    @Test
    public void example290() throws Exception {
        GFM("- a\n - b\n  - c\n   - d\n  - e\n - f\n- g\n",
            "<ul><li>a</li><li>b</li><li>c</li><li>d</li><li>e</li><li>f</li><li>g</li></ul>");
    }

    @Test
    public void example291() throws Exception {
        GFM("1. a\n\n  2. b\n\n   3. c\n",
            "<ol><li><p>a</p></li><li><p>b</p></li><li><p>c</p></li></ol>");
    }

    @Test
    public void example292() throws Exception {
        GFM("- a\n - b\n  - c\n   - d\n    - e\n",
            "<ul><li>a</li><li>b</li><li>c</li><li>d\n- e</li></ul>");
    }

    @Test
    public void example293() throws Exception {
        GFM("1. a\n\n  2. b\n\n    3. c\n",
            "<ol><li><p>a</p></li><li><p>b</p></li></ol><pre><code>3. c\n</code></pre>");
    }

    @Test
    public void example294() throws Exception {
        GFM("- a\n- b\n\n- c\n",
            "<ul><li><p>a</p></li><li><p>b</p></li><li><p>c</p></li></ul>");
    }

    @Test
    public void example295() throws Exception {
        GFM("* a\n*\n\n* c\n",
            "<ul><li><p>a</p></li><li></li><li><p>c</p></li></ul>");
    }

    @Test
    public void example296() throws Exception {
        GFM("- a\n- b\n\n  c\n- d\n",
            "<ul><li><p>a</p></li><li><p>b</p><p>c</p></li><li><p>d</p></li></ul>");
    }

    @Test
    public void example297() throws Exception {
        GFM("- a\n- b\n\n  [ref]: /url\n- d\n",
            "<ul><li><p>a</p></li><li><p>b</p></li><li><p>d</p></li></ul>");
    }

    @Test
    public void example298() throws Exception {
        GFM("- a\n- ```\n  b\n\n\n  ```\n- c\n",
            "<ul><li>a</li><li><pre><code>b\n\n\n</code></pre></li><li>c</li></ul>");
    }

    @Test
    public void example299() throws Exception {
        GFM("- a\n  - b\n\n    c\n- d\n",
            "<ul><li>a\n<ul><li><p>b</p><p>c</p></li></ul></li><li>d</li></ul>");
    }

    @Test
    public void example300() throws Exception {
        GFM("* a\n  > b\n  >* c\n",
            "<ul><li>a\n<blockquote><p>b</p></blockquote></li><li>c</li></ul>");
    }

    @Test
    public void example301() throws Exception {
        GFM("- a\n  > b\n  ```\n  c\n  ```\n- d\n",
            "<ul><li>a\n<blockquote><p>b</p></blockquote><pre><code>c\n</code></pre></li><li>d</li></ul>");
    }

    @Test
    public void example302() throws Exception {
        GFM("- a\n", "<ul><li>a</li></ul>");
    }

    @Test
    public void example303() throws Exception {
        GFM("- a\n  - b\n", "<ul><li>a\n<ul><li>b</li></ul></li></ul>");
    }

    @Test
    public void example304() throws Exception {
        GFM("1. ```\n   foo\n   ```\n\n   bar\n",
            "<ol><li><pre><code>foo\n</code></pre><p>bar</p></li></ol>");
    }

    @Test
    public void example305() throws Exception {
        GFM("* foo\n  * bar\n\n  baz\n",
            "<ul><li><p>foo</p><ul><li>bar</li></ul><p>baz</p></li></ul>");
    }

    @Test
    public void example306() throws Exception {
        GFM("- a\n  - b\n  - c\n\n- d\n  - e\n  - f\n",
            "<ul><li><p>a</p><ul><li>b</li><li>c</li></ul></li><li><p>d</p><ul><li>e</li><li>f</li></ul></li></ul>");
    }

    @Test
    public void example307() throws Exception {
        GFM("`hi`lo`\n", "<p><code>hi</code>lo`</p>");
    }

    @Test
    public void example308() throws Exception {
        GFM("\\!\\\"\\#\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/\\:\\;\\<\\=\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~\n",
            "<p>!&quot;#$%&amp;'()*+,-./:;&lt;=&gt;?@[\\]^_`{|}~</p>");
    }

    @Test
    public void example309() throws Exception {
        GFM("\\\t\\A\\a\\ \\3\\φ\\«\n", "<p>\\\t\\A\\a\\ \\3\\φ\\«</p>");
    }

    @Test
    public void example310() throws Exception {
        GFM("\\*not emphasized*\n\\<br/> not a tag\n\\[not a link](/foo)\n\\`not code`\n1\\. not a list\n\\* not a list\n\\# not a heading\n\\[foo]: /url \"not a reference\"\n\\&ouml; not a character entity\n",
            "<p>*not emphasized*\n&lt;br/&gt; not a tag\n[not a link](/foo)\n`not code`\n1. not a list\n* not a list\n# not a heading\n[foo]: /url &quot;not a reference&quot;\n&amp;ouml; not a character entity</p>");
    }

    @Test
    public void example311() throws Exception {
        GFM("\\\\*emphasis*\n", "<p>\\<em>emphasis</em></p>");
    }

    @Test
    public void example312() throws Exception {
        GFM("foo\\\nbar\n", "<p>foo<br />bar</p>");
    }

    @Test
    public void example313() throws Exception {
        GFM("`` \\[\\` ``\n", "<p><code>\\[\\`</code></p>");
    }

    @Test
    public void example314() throws Exception {
        GFM("    \\[\\]\n", "<pre><code>\\[\\]\n</code></pre>");
    }

    @Test
    public void example315() throws Exception {
        GFM("~~~\n\\[\\]\n~~~\n", "<pre><code>\\[\\]\n</code></pre>");
    }

    @Test
    public void example316() throws Exception {
        GFM("<http://example.com?find=\\*>",
            "<p><a href=\"http://example.com?find=%5C*\">http://example.com?find=\\*</a></p>");
    }

    @Test
    public void example317() throws Exception {
        GFM("<a href=\"/bar\\/)\">", "<a href=\"/bar\\/)\">");
    }

    @Test
    public void example318() throws Exception {
        GFM("[foo](/bar\\* \"ti\\*tle\")\n",
            "<p><a href=\"/bar*\" title=\"ti*tle\">foo</a></p>");
    }

    @Test
    public void example319() throws Exception {
        GFM("[foo]\n\n[foo]: /bar\\* \"ti\\*tle\"\n",
            "<p><a href=\"/bar*\" title=\"ti*tle\">foo</a></p>");
    }

    @Test
    public void example320() throws Exception {
        GFM("``` foo\\+bar\nfoo\n```\n",
            "<pre><code class=\"language-foo+bar\">foo\n</code></pre>");
    }

    @Test
    public void example321() throws Exception {
        GFM("&nbsp; &amp; &copy; &AElig; &Dcaron;\n&frac34; &HilbertSpace; &DifferentialD;\n&ClockwiseContourIntegral; &ngE;\n",
            "<p>\u00A0 &amp; © Æ Ď\n¾ ℋ ⅆ\n∲ ≧̸</p>");
    }

    @Test
    public void example322() throws Exception {
        GFM("&#35; &#1234; &#992; &#0;\n", "<p># Ӓ Ϡ �</p>");
    }

    @Test
    public void example323() throws Exception {
        GFM("&#X22; &#XD06; &#xcab;\n", "<p>&quot; ആ ಫ</p>");
    }

    @Test
    public void example324() throws Exception {
        GFM("&nbsp &x; &#; &#x;\n&#87654321;\n&#abcdef0;\n&ThisIsNotDefined; &hi?;\n",
            "<p>&amp;nbsp &amp;x; &amp;#; &amp;#x;\n&amp;#87654321;\n&amp;#abcdef0;\n&amp;ThisIsNotDefined; &amp;hi?;</p>");
    }

    @Test
    public void example325() throws Exception {
        GFM("&copy\n", "<p>&amp;copy</p>");
    }

    @Test
    public void example326() throws Exception {
        GFM("&MadeUpEntity;\n", "<p>&amp;MadeUpEntity;</p>");
    }

    @Test
    public void example327() throws Exception {
        GFM("<a href=\"&ouml;&ouml;.html\">", "<a href=\"&ouml;&ouml;.html\">");
    }

    @Test
    public void example328() throws Exception {
        GFM("[foo](/f&ouml;&ouml; \"f&ouml;&ouml;\")\n",
            "<p><a href=\"/f%C3%B6%C3%B6\" title=\"föö\">foo</a></p>");
    }

    @Test
    public void example329() throws Exception {
        GFM("[foo]\n\n[foo]: /f&ouml;&ouml; \"f&ouml;&ouml;\"\n",
            "<p><a href=\"/f%C3%B6%C3%B6\" title=\"föö\">foo</a></p>");
    }

    @Test
    public void example330() throws Exception {
        GFM("``` f&ouml;&ouml;\nfoo\n```\n",
            "<pre><code class=\"language-föö\">foo\n</code></pre>");
    }

    @Test
    public void example331() throws Exception {
        GFM("`f&ouml;&ouml;`\n", "<p><code>f&amp;ouml;&amp;ouml;</code></p>");
    }

    @Test
    public void example332() throws Exception {
        GFM("    f&ouml;f&ouml;\n",
            "<pre><code>f&amp;ouml;f&amp;ouml;\n</code></pre>");
    }

    @Test
    public void example333() throws Exception {
        GFM("&#42;foo&#42;\n*foo*\n", "<p>*foo*\n<em>foo</em></p>");
    }

    @Test
    public void example334() throws Exception {
        GFM("&#42; foo\n\n* foo\n", "<p>* foo</p><ul><li>foo</li></ul>");
    }

    @Test
    public void example335() throws Exception {
        GFM("foo&#10;&#10;bar\n", "<p>foo\n\nbar</p>");
    }

    @Test
    public void example336() throws Exception {
        GFM("&#9;foo\n", "<p>\tfoo</p>");
    }

    @Test
    public void example337() throws Exception {
        GFM("[a](url &quot;tit&quot;)\n", "<p>[a](url &quot;tit&quot;)</p>");
    }

    @Test
    public void example338() throws Exception {
        GFM("`foo`\n", "<p><code>foo</code></p>");
    }

    @Test
    public void example339() throws Exception {
        GFM("`` foo ` bar ``\n", "<p><code>foo ` bar</code></p>");
    }

    @Test
    public void example340() throws Exception {
        GFM("` `` `\n", "<p><code>``</code></p>");
    }

    @Test
    public void example341() throws Exception {
        GFM("`  ``  `\n", "<p><code> `` </code></p>");
    }

    @Test
    public void example342() throws Exception {
        GFM("` a`\n", "<p><code> a</code></p>");
    }

    @Test
    public void example343() throws Exception {
        GFM("` b `\n", "<p><code> b </code></p>");
    }

    @Test
    public void example344() throws Exception {
        GFM("` `\n`  `\n", "<p><code> </code><code>  </code></p>");
    }

    @Test
    public void example345() throws Exception {
        GFM("``\nfoo\nbar  \nbaz\n``\n", "<p><code>foo bar   baz</code></p>");
    }

    @Test
    public void example346() throws Exception {
        GFM("``\nfoo \n``\n", "<p><code>foo </code></p>");
    }

    @Test
    public void example347() throws Exception {
        GFM("`foo   bar \nbaz`\n", "<p><code>foo   bar  baz</code></p>");
    }

    @Test
    public void example348() throws Exception {
        GFM("`foo\\`bar`\n", "<p><code>foo\\</code>bar`</p>");
    }

    @Test
    public void example349() throws Exception {
        GFM("``foo`bar``\n", "<p><code>foo`bar</code></p>");
    }

    @Test
    public void example350() throws Exception {
        GFM("` foo `` bar `\n", "<p><code>foo `` bar</code></p>");
    }

    @Test
    public void example351() throws Exception {
        GFM("*foo`*`\n", "<p>*foo<code>*</code></p>");
    }

    @Test
    public void example352() throws Exception {
        GFM("[not a `link](/foo`)\n", "<p>[not a <code>link](/foo</code>)</p>");
    }

    @Test
    public void example353() throws Exception {
        GFM("`<a href=\"`\">`\n",
            "<p><code>&lt;a href=&quot;</code>&quot;&gt;`</p>");
    }

    @Test
    public void example354() throws Exception {
        GFM("<a href=\"`\">`\n", "<p><a href=\"`\">`</p>");
    }

    @Test
    public void example355() throws Exception {
        GFM("`<http://foo.bar.`baz>`\n",
            "<p><code>&lt;http://foo.bar.</code>baz&gt;`</p>");
    }

    @Test
    public void example356() throws Exception {
        GFM("<http://foo.bar.`baz>`\n",
            "<p><a href=\"http://foo.bar.%60baz\">http://foo.bar.`baz</a>`</p>");
    }

    @Test
    public void example357() throws Exception {
        GFM("```foo``\n", "<p>```foo``</p>");
    }

    @Test
    public void example358() throws Exception {
        GFM("`foo\n", "<p>`foo</p>");
    }

    @Test
    public void example359() throws Exception {
        GFM("`foo``bar``\n", "<p>`foo<code>bar</code></p>");
    }

    @Test
    public void example360() throws Exception {
        GFM("*foo bar*\n", "<p><em>foo bar</em></p>");
    }

    @Test
    public void example361() throws Exception {
        GFM("a * foo bar*\n", "<p>a * foo bar*</p>");
    }

    @Test
    public void example362() throws Exception {
        GFM("a*\"foo\"*\n", "<p>a*&quot;foo&quot;*</p>");
    }

    @Test
    public void example363() throws Exception {
        GFM("* a *\n", "<p>* a *</p>");
    }

    @Test
    public void example364() throws Exception {
        GFM("foo*bar*\n", "<p>foo<em>bar</em></p>");
    }

    @Test
    public void example365() throws Exception {
        GFM("5*6*78\n", "<p>5<em>6</em>78</p>");
    }

    @Test
    public void example366() throws Exception {
        GFM("_foo bar_\n", "<p><em>foo bar</em></p>");
    }

    @Test
    public void example367() throws Exception {
        GFM("_ foo bar_\n", "<p>_ foo bar_</p>");
    }

    @Test
    public void example368() throws Exception {
        GFM("a_\"foo\"_\n", "<p>a_&quot;foo&quot;_</p>");
    }

    @Test
    public void example369() throws Exception {
        GFM("foo_bar_\n", "<p>foo_bar_</p>");
    }

    @Test
    public void example370() throws Exception {
        GFM("5_6_78\n", "<p>5_6_78</p>");
    }

    @Test
    public void example371() throws Exception {
        GFM("пристаням_стремятся_\n", "<p>пристаням_стремятся_</p>");
    }

    @Test
    public void example372() throws Exception {
        GFM("aa_\"bb\"_cc\n", "<p>aa_&quot;bb&quot;_cc</p>");
    }

    @Test
    public void example373() throws Exception {
        GFM("foo-_(bar)_\n", "<p>foo-<em>(bar)</em></p>");
    }

    @Test
    public void example374() throws Exception {
        GFM("_foo*\n", "<p>_foo*</p>");
    }

    @Test
    public void example375() throws Exception {
        GFM("*foo bar *\n", "<p>*foo bar *</p>");
    }

    @Test
    public void example376() throws Exception {
        GFM("*foo bar\n*\n", "<p>*foo bar\n*</p>");
    }

    @Test
    public void example377() throws Exception {
        GFM("*(*foo)\n", "<p>*(*foo)</p>");
    }

    @Test
    public void example378() throws Exception {
        GFM("*(*foo*)*\n", "<p><em>(<em>foo</em>)</em></p>");
    }

    @Test
    public void example379() throws Exception {
        GFM("*foo*bar\n", "<p><em>foo</em>bar</p>");
    }

    @Test
    public void example380() throws Exception {
        GFM("_foo bar _\n", "<p>_foo bar _</p>");
    }

    @Test
    public void example381() throws Exception {
        GFM("_(_foo)\n", "<p>_(_foo)</p>");
    }

    @Test
    public void example382() throws Exception {
        GFM("_(_foo_)_\n", "<p><em>(<em>foo</em>)</em></p>");
    }

    @Test
    public void example383() throws Exception {
        GFM("_foo_bar\n", "<p>_foo_bar</p>");
    }

    @Test
    public void example384() throws Exception {
        GFM("_пристаням_стремятся\n", "<p>_пристаням_стремятся</p>");
    }

    @Test
    public void example385() throws Exception {
        GFM("_foo_bar_baz_\n", "<p><em>foo_bar_baz</em></p>");
    }

    @Test
    public void example386() throws Exception {
        GFM("_(bar)_.\n", "<p><em>(bar)</em>.</p>");
    }

    @Test
    public void example387() throws Exception {
        GFM("**foo bar**\n", "<p><strong>foo bar</strong></p>");
    }

    @Test
    public void example388() throws Exception {
        GFM("** foo bar**\n", "<p>** foo bar**</p>");
    }

    @Test
    public void example389() throws Exception {
        GFM("a**\"foo\"**\n", "<p>a**&quot;foo&quot;**</p>");
    }

    @Test
    public void example390() throws Exception {
        GFM("foo**bar**\n", "<p>foo<strong>bar</strong></p>");
    }

    @Test
    public void example391() throws Exception {
        GFM("__foo bar__\n", "<p><strong>foo bar</strong></p>");
    }

    @Test
    public void example392() throws Exception {
        GFM("__ foo bar__\n", "<p>__ foo bar__</p>");
    }

    @Test
    public void example393() throws Exception {
        GFM("__\nfoo bar__\n", "<p>__\nfoo bar__</p>");
    }

    @Test
    public void example394() throws Exception {
        GFM("a__\"foo\"__\n", "<p>a__&quot;foo&quot;__</p>");
    }

    @Test
    public void example395() throws Exception {
        GFM("foo__bar__\n", "<p>foo__bar__</p>");
    }

    @Test
    public void example396() throws Exception {
        GFM("5__6__78\n", "<p>5__6__78</p>");
    }

    @Test
    public void example397() throws Exception {
        GFM("пристаням__стремятся__\n", "<p>пристаням__стремятся__</p>");
    }

    @Test
    public void example398() throws Exception {
        GFM("__foo, __bar__, baz__\n",
            "<p><strong>foo, <strong>bar</strong>, baz</strong></p>");
    }

    @Test
    public void example399() throws Exception {
        GFM("foo-__(bar)__\n", "<p>foo-<strong>(bar)</strong></p>");
    }

    @Test
    public void example400() throws Exception {
        GFM("**foo bar **\n", "<p>**foo bar **</p>");
    }

    @Test
    public void example401() throws Exception {
        GFM("**(**foo)\n", "<p>**(**foo)</p>");
    }

    @Test
    public void example402() throws Exception {
        GFM("*(**foo**)*\n", "<p><em>(<strong>foo</strong>)</em></p>");
    }

    @Test
    public void example403() throws Exception {
        GFM("**Gomphocarpus (*Gomphocarpus physocarpus*, syn.\n*Asclepias physocarpa*)**\n",
            "<p><strong>Gomphocarpus (<em>Gomphocarpus physocarpus</em>, syn.\n<em>Asclepias physocarpa</em>)</strong></p>");
    }

    @Test
    public void example404() throws Exception {
        GFM("**foo \"*bar*\" foo**\n",
            "<p><strong>foo &quot;<em>bar</em>&quot; foo</strong></p>");
    }

    @Test
    public void example405() throws Exception {
        GFM("**foo**bar\n", "<p><strong>foo</strong>bar</p>");
    }

    @Test
    public void example406() throws Exception {
        GFM("__foo bar __\n", "<p>__foo bar __</p>");
    }

    @Test
    public void example407() throws Exception {
        GFM("__(__foo)\n", "<p>__(__foo)</p>");
    }

    @Test
    public void example408() throws Exception {
        GFM("_(__foo__)_\n", "<p><em>(<strong>foo</strong>)</em></p>");
    }

    @Test
    public void example409() throws Exception {
        GFM("__foo__bar\n", "<p>__foo__bar</p>");
    }

    @Test
    public void example410() throws Exception {
        GFM("__пристаням__стремятся\n", "<p>__пристаням__стремятся</p>");
    }

    @Test
    public void example411() throws Exception {
        GFM("__foo__bar__baz__\n", "<p><strong>foo__bar__baz</strong></p>");
    }

    @Test
    public void example412() throws Exception {
        GFM("__(bar)__.\n", "<p><strong>(bar)</strong>.</p>");
    }

    @Test
    public void example413() throws Exception {
        GFM("*foo [bar](/url)*\n",
            "<p><em>foo <a href=\"/url\">bar</a></em></p>");
    }

    @Test
    public void example414() throws Exception {
        GFM("*foo\nbar*\n", "<p><em>foo\nbar</em></p>");
    }

    @Test
    public void example415() throws Exception {
        GFM("_foo __bar__ baz_\n",
            "<p><em>foo <strong>bar</strong> baz</em></p>");
    }

    @Test
    public void example416() throws Exception {
        GFM("_foo _bar_ baz_\n", "<p><em>foo <em>bar</em> baz</em></p>");
    }

    @Test
    public void example417() throws Exception {
        GFM("__foo_ bar_\n", "<p><em><em>foo</em> bar</em></p>");
    }

    @Test
    public void example418() throws Exception {
        GFM("*foo *bar**\n", "<p><em>foo <em>bar</em></em></p>");
    }

    @Test
    public void example419() throws Exception {
        GFM("*foo **bar** baz*\n",
            "<p><em>foo <strong>bar</strong> baz</em></p>");
    }

    @Test
    public void example420() throws Exception {
        GFM("*foo**bar**baz*\n", "<p><em>foo<strong>bar</strong>baz</em></p>");
    }

    @Test
    public void example421() throws Exception {
        GFM("*foo**bar*\n", "<p><em>foo**bar</em></p>");
    }

    @Test
    public void example422() throws Exception {
        GFM("***foo** bar*\n", "<p><em><strong>foo</strong> bar</em></p>");
    }

    @Test
    public void example423() throws Exception {
        GFM("*foo **bar***\n", "<p><em>foo <strong>bar</strong></em></p>");
    }

    @Test
    public void example424() throws Exception {
        GFM("*foo**bar***\n", "<p><em>foo<strong>bar</strong></em></p>");
    }

    @Test
    public void example425() throws Exception {
        GFM("foo***bar***baz\n", "<p>foo<em><strong>bar</strong></em>baz</p>");
    }

    @Test
    public void example426() throws Exception {
        GFM("foo******bar*********baz\n",
            "<p>foo<strong><strong><strong>bar</strong></strong></strong>***baz</p>");
    }

    @Test
    public void example427() throws Exception {
        GFM("*foo **bar *baz* bim** bop*\n",
            "<p><em>foo <strong>bar <em>baz</em> bim</strong> bop</em></p>");
    }

    @Test
    public void example428() throws Exception {
        GFM("*foo [*bar*](/url)*\n",
            "<p><em>foo <a href=\"/url\"><em>bar</em></a></em></p>");
    }

    @Test
    public void example429() throws Exception {
        GFM("** is not an empty emphasis\n",
            "<p>** is not an empty emphasis</p>");
    }

    @Test
    public void example430() throws Exception {
        GFM("**** is not an empty strong emphasis\n",
            "<p>**** is not an empty strong emphasis</p>");
    }

    @Test
    public void example431() throws Exception {
        GFM("**foo [bar](/url)**\n",
            "<p><strong>foo <a href=\"/url\">bar</a></strong></p>");
    }

    @Test
    public void example432() throws Exception {
        GFM("**foo\nbar**\n", "<p><strong>foo\nbar</strong></p>");
    }

    @Test
    public void example433() throws Exception {
        GFM("__foo _bar_ baz__\n",
            "<p><strong>foo <em>bar</em> baz</strong></p>");
    }

    @Test
    public void example434() throws Exception {
        GFM("__foo __bar__ baz__\n",
            "<p><strong>foo <strong>bar</strong> baz</strong></p>");
    }

    @Test
    public void example435() throws Exception {
        GFM("____foo__ bar__\n",
            "<p><strong><strong>foo</strong> bar</strong></p>");
    }

    @Test
    public void example436() throws Exception {
        GFM("**foo **bar****\n",
            "<p><strong>foo <strong>bar</strong></strong></p>");
    }

    @Test
    public void example437() throws Exception {
        GFM("**foo *bar* baz**\n",
            "<p><strong>foo <em>bar</em> baz</strong></p>");
    }

    @Test
    public void example438() throws Exception {
        GFM("**foo*bar*baz**\n", "<p><strong>foo<em>bar</em>baz</strong></p>");
    }

    @Test
    public void example439() throws Exception {
        GFM("***foo* bar**\n", "<p><strong><em>foo</em> bar</strong></p>");
    }

    @Test
    public void example440() throws Exception {
        GFM("**foo *bar***\n", "<p><strong>foo <em>bar</em></strong></p>");
    }

    @Test
    public void example441() throws Exception {
        GFM("**foo *bar **baz**\nbim* bop**\n",
            "<p><strong>foo <em>bar <strong>baz</strong>bim</em> bop</strong></p>");
    }

    @Test
    public void example442() throws Exception {
        GFM("**foo [*bar*](/url)**\n",
            "<p><strong>foo <a href=\"/url\"><em>bar</em></a></strong></p>");
    }

    @Test
    public void example443() throws Exception {
        GFM("__ is not an empty emphasis\n",
            "<p>__ is not an empty emphasis</p>");
    }

    @Test
    public void example444() throws Exception {
        GFM("____ is not an empty strong emphasis\n",
            "<p>____ is not an empty strong emphasis</p>");
    }

    @Test
    public void example445() throws Exception {
        GFM("foo ***\n", "<p>foo ***</p>");
    }

    @Test
    public void example446() throws Exception {
        GFM("foo *\\**\n", "<p>foo <em>*</em></p>");
    }

    @Test
    public void example447() throws Exception {
        GFM("foo *_*\n", "<p>foo <em>_</em></p>");
    }

    @Test
    public void example448() throws Exception {
        GFM("foo *****\n", "<p>foo *****</p>");
    }

    @Test
    public void example449() throws Exception {
        GFM("foo **\\***\n", "<p>foo <strong>*</strong></p>");
    }

    @Test
    public void example450() throws Exception {
        GFM("foo **_**\n", "<p>foo <strong>_</strong></p>");
    }

    @Test
    public void example451() throws Exception {
        GFM("**foo*\n", "<p>*<em>foo</em></p>");
    }

    @Test
    public void example452() throws Exception {
        GFM("*foo**\n", "<p><em>foo</em>*</p>");
    }

    @Test
    public void example453() throws Exception {
        GFM("***foo**\n", "<p>*<strong>foo</strong></p>");
    }

    @Test
    public void example454() throws Exception {
        GFM("****foo*\n", "<p>***<em>foo</em></p>");
    }

    @Test
    public void example455() throws Exception {
        GFM("**foo***\n", "<p><strong>foo</strong>*</p>");
    }

    @Test
    public void example456() throws Exception {
        GFM("*foo****\n", "<p><em>foo</em>***</p>");
    }

    @Test
    public void example457() throws Exception {
        GFM("foo ___\n", "<p>foo ___</p>");
    }

    @Test
    public void example458() throws Exception {
        GFM("foo _\\__\n", "<p>foo <em>_</em></p>");
    }

    @Test
    public void example459() throws Exception {
        GFM("foo _*_\n", "<p>foo <em>*</em></p>");
    }

    @Test
    public void example460() throws Exception {
        GFM("foo _____\n", "<p>foo _____</p>");
    }

    @Test
    public void example461() throws Exception {
        GFM("foo __\\___\n", "<p>foo <strong>_</strong></p>");
    }

    @Test
    public void example462() throws Exception {
        GFM("foo __*__\n", "<p>foo <strong>*</strong></p>");
    }

    @Test
    public void example463() throws Exception {
        GFM("__foo_\n", "<p>_<em>foo</em></p>");
    }

    @Test
    public void example464() throws Exception {
        GFM("_foo__\n", "<p><em>foo</em>_</p>");
    }

    @Test
    public void example465() throws Exception {
        GFM("___foo__\n", "<p>_<strong>foo</strong></p>");
    }

    @Test
    public void example466() throws Exception {
        GFM("____foo_\n", "<p>___<em>foo</em></p>");
    }

    @Test
    public void example467() throws Exception {
        GFM("__foo___\n", "<p><strong>foo</strong>_</p>");
    }

    @Test
    public void example468() throws Exception {
        GFM("_foo____\n", "<p><em>foo</em>___</p>");
    }

    @Test
    public void example469() throws Exception {
        GFM("**foo**\n", "<p><strong>foo</strong></p>");
    }

    @Test
    public void example470() throws Exception {
        GFM("*_foo_*\n", "<p><em><em>foo</em></em></p>");
    }

    @Test
    public void example471() throws Exception {
        GFM("__foo__\n", "<p><strong>foo</strong></p>");
    }

    @Test
    public void example472() throws Exception {
        GFM("_*foo*_\n", "<p><em><em>foo</em></em></p>");
    }

    @Test
    public void example473() throws Exception {
        GFM("****foo****\n", "<p><strong><strong>foo</strong></strong></p>");
    }

    @Test
    public void example474() throws Exception {
        GFM("____foo____\n", "<p><strong><strong>foo</strong></strong></p>");
    }

    @Test
    public void example475() throws Exception {
        GFM("******foo******\n",
            "<p><strong><strong><strong>foo</strong></strong></strong></p>");
    }

    @Test
    public void example476() throws Exception {
        GFM("***foo***\n", "<p><em><strong>foo</strong></em></p>");
    }

    @Test
    public void example477() throws Exception {
        GFM("_____foo_____\n",
            "<p><em><strong><strong>foo</strong></strong></em></p>");
    }

    @Test
    public void example478() throws Exception {
        GFM("*foo _bar* baz_\n", "<p><em>foo _bar</em> baz_</p>");
    }

    @Test
    public void example479() throws Exception {
        GFM("*foo __bar *baz bim__ bam*\n",
            "<p><em>foo <strong>bar *baz bim</strong> bam</em></p>");
    }

    @Test
    public void example480() throws Exception {
        GFM("**foo **bar baz**\n", "<p>**foo <strong>bar baz</strong></p>");
    }

    @Test
    public void example481() throws Exception {
        GFM("*foo *bar baz*\n", "<p>*foo <em>bar baz</em></p>");
    }

    @Test
    public void example482() throws Exception {
        GFM("*[bar*](/url)\n", "<p>*<a href=\"/url\">bar*</a></p>");
    }

    @Test
    public void example483() throws Exception {
        GFM("_foo [bar_](/url)\n", "<p>_foo <a href=\"/url\">bar_</a></p>");
    }

    @Test
    public void example484() throws Exception {
        GFM("*<img src=\"foo\" title=\"*\"/>",
            "<p>*<img src=\"foo\" title=\"*\"/></p>");
    }

    @Test
    public void example485() throws Exception {
        GFM("**<a href=\"**\">", "<p>**<a href=\"**\"></p>");
    }

    @Test
    public void example486() throws Exception {
        GFM("__<a href=\"__\">", "<p>__<a href=\"__\"></p>");
    }

    @Test
    public void example487() throws Exception {
        GFM("*a `*`*\n", "<p><em>a <code>*</code></em></p>");
    }

    @Test
    public void example488() throws Exception {
        GFM("_a `_`_\n", "<p><em>a <code>_</code></em></p>");
    }

    @Test
    public void example489() throws Exception {
        GFM("**a<http://foo.bar/?q=**>",
            "<p>**a<a href=\"http://foo.bar/?q=**\">http://foo.bar/?q=**</a></p>");
    }

    @Test
    public void example490() throws Exception {
        GFM("__a<http://foo.bar/?q=__>",
            "<p>__a<a href=\"http://foo.bar/?q=__\">http://foo.bar/?q=__</a></p>");
    }

    @Test
    public void example491() throws Exception {
        GFM("~~Hi~~ Hello, world!\n", "<p><del>Hi</del> Hello, world!</p>");
    }

    @Test
    public void example492() throws Exception {
        GFM("This ~~has a\n\nnew paragraph~~.\n",
            "<p>This ~~has a</p><p>new paragraph~~.</p>");
    }

    @Test
    public void example493() throws Exception {
        GFM("[link](/uri \"title\")\n",
            "<p><a href=\"/uri\" title=\"title\">link</a></p>");
    }

    @Test
    public void example494() throws Exception {
        GFM("[link](/uri)\n", "<p><a href=\"/uri\">link</a></p>");
    }

    @Test
    public void example495() throws Exception {
        GFM("[link]()\n", "<p><a href=\"\">link</a></p>");
    }

    @Test
    public void example496() throws Exception {
        GFM("[link](<>)\n", "<p><a href=\"\">link</a></p>");
    }

    @Test
    public void example497() throws Exception {
        GFM("[link](/my uri)\n", "<p>[link](/my uri)</p>");
    }

    @Test
    public void example498() throws Exception {
        GFM("[link](</my uri>)\n", "<p><a href=\"/my%20uri\">link</a></p>");
    }

    @Test
    public void example499() throws Exception {
        GFM("[link](foo\nbar)\n", "<p>[link](foo\nbar)</p>");
    }

    @Test
    public void example500() throws Exception {
        GFM("[link](<foo\nbar>)\n", "<p>[link](<foo\nbar>)</p>");
    }

    @Test
    public void example501() throws Exception {
        GFM("[a](<b)c>)\n", "<p><a href=\"b)c\">a</a></p>");
    }

    @Test
    public void example502() throws Exception {
        GFM("[link](<foo\\>)\n", "<p>[link](&lt;foo&gt;)</p>");
    }

    @Test
    public void example503() throws Exception {
        GFM("[a](<b)c\n[a](<b)c>[a](<b>c)\n",
            "<p>[a](&lt;b)c\n[a](&lt;b)c&gt;\n[a](<b>c)</p>");
    }

    @Test
    public void example504() throws Exception {
        GFM("[link](\\(foo\\))\n", "<p><a href=\"(foo)\">link</a></p>");
    }

    @Test
    public void example505() throws Exception {
        GFM("[link](foo(and(bar)))\n",
            "<p><a href=\"foo(and(bar))\">link</a></p>");
    }

    @Test
    public void example506() throws Exception {
        GFM("[link](foo\\(and\\(bar\\))\n",
            "<p><a href=\"foo(and(bar)\">link</a></p>");
    }

    @Test
    public void example507() throws Exception {
        GFM("[link](<foo(and(bar)>)\n",
            "<p><a href=\"foo(and(bar)\">link</a></p>");
    }

    @Test
    public void example508() throws Exception {
        GFM("[link](foo\\)\\:)\n", "<p><a href=\"foo):\">link</a></p>");
    }

    @Test
    public void example509() throws Exception {
        GFM("[link](#fragment)\n\n[link](http://example.com#fragment)\n\n[link](http://example.com?foo=3#frag)\n",
            "<p><a href=\"#fragment\">link</a></p><p><a href=\"http://example.com#fragment\">link</a></p><p><a href=\"http://example.com?foo=3#frag\">link</a></p>");
    }

    @Test
    public void example510() throws Exception {
        GFM("[link](foo\\bar)\n", "<p><a href=\"foo%5Cbar\">link</a></p>");
    }

    @Test
    public void example511() throws Exception {
        GFM("[link](foo%20b&auml;)\n",
            "<p><a href=\"foo%20b%C3%A4\">link</a></p>");
    }

    @Test
    public void example512() throws Exception {
        GFM("[link](\"title\")\n", "<p><a href=\"%22title%22\">link</a></p>");
    }

    @Test
    public void example513() throws Exception {
        GFM("[link](/url \"title\")\n[link](/url 'title')\n[link](/url (title))\n",
            "<p><a href=\"/url\" title=\"title\">link</a>\n<a href=\"/url\" title=\"title\">link</a>\n<a href=\"/url\" title=\"title\">link</a></p>");
    }

    @Test
    public void example514() throws Exception {
        GFM("[link](/url \"title \\\"&quot;\")\n",
            "<p><a href=\"/url\" title=\"title &quot;&quot;\">link</a></p>");
    }

    @Test
    public void example515() throws Exception {
        GFM("[link](/url \"title\")\n",
            "<p><a href=\"/url%C2%A0%22title%22\">link</a></p>");
    }

    @Test
    public void example516() throws Exception {
        GFM("[link](/url \"title \"and\" title\")\n",
            "<p>[link](/url &quot;title &quot;and&quot; title&quot;)</p>");
    }

    @Test
    public void example517() throws Exception {
        GFM("[link](/url 'title \"and\" title')\n",
            "<p><a href=\"/url\" title=\"title &quot;and&quot; title\">link</a></p>");
    }

    @Test
    public void example518() throws Exception {
        GFM("[link](   /uri\n  \"title\"  )\n",
            "<p><a href=\"/uri\" title=\"title\">link</a></p>");
    }

    @Test
    public void example519() throws Exception {
        GFM("[link] (/uri)\n", "<p>[link] (/uri)</p>");
    }

    @Test
    public void example520() throws Exception {
        GFM("[link [foo [bar]]](/uri)\n",
            "<p><a href=\"/uri\">link [foo [bar]]</a></p>");
    }

    @Test
    public void example521() throws Exception {
        GFM("[link] bar](/uri)\n", "<p>[link] bar](/uri)</p>");
    }

    @Test
    public void example522() throws Exception {
        GFM("[link [bar](/uri)\n", "<p>[link <a href=\"/uri\">bar</a></p>");
    }

    @Test
    public void example523() throws Exception {
        GFM("[link \\[bar](/uri)\n", "<p><a href=\"/uri\">link [bar</a></p>");
    }

    @Test
    public void example524() throws Exception {
        GFM("[link *foo **bar** `#`*](/uri)\n",
            "<p><a href=\"/uri\">link <em>foo <strong>bar</strong> <code>#</code></em></a></p>");
    }

    @Test
    public void example525() throws Exception {
        GFM("[![moon](moon.jpg)](/uri)\n",
            "<p><a href=\"/uri\"><img src=\"moon.jpg\" alt=\"moon\" /></a></p>");
    }

    @Test
    public void example526() throws Exception {
        GFM("[foo [bar](/uri)](/uri)\n",
            "<p>[foo <a href=\"/uri\">bar</a>](/uri)</p>");
    }

    @Test
    public void example527() throws Exception {
        GFM("[foo *[bar [baz](/uri)](/uri)*](/uri)\n",
            "<p>[foo <em>[bar <a href=\"/uri\">baz</a>](/uri)</em>](/uri)</p>");
    }

    @Test
    public void example528() throws Exception {
        GFM("![[[foo](uri1)](uri2)](uri3)\n",
            "<p><img src=\"uri3\" alt=\"[foo](uri2)\" /></p>");
    }

    @Test
    public void example529() throws Exception {
        GFM("*[foo*](/uri)\n", "<p>*<a href=\"/uri\">foo*</a></p>");
    }

    @Test
    public void example530() throws Exception {
        GFM("[foo *bar](baz*)\n", "<p><a href=\"baz*\">foo *bar</a></p>");
    }

    @Test
    public void example531() throws Exception {
        GFM("*foo [bar* baz]\n", "<p><em>foo [bar</em> baz]</p>");
    }

    @Test
    public void example532() throws Exception {
        GFM("[foo <bar attr=\"](baz)\">", "<p>[foo <bar attr=\"](baz)\"></p>");
    }

    @Test
    public void example533() throws Exception {
        GFM("[foo`](/uri)`\n", "<p>[foo<code>](/uri)</code></p>");
    }

    @Test
    public void example534() throws Exception {
        GFM("[foo<http://example.com/?search=](uri)>",
            "<p>[foo<a href=\"http://example.com/?search=%5D(uri)\">http://example.com/?search=](uri)</a></p>");
    }

    @Test
    public void example535() throws Exception {
        GFM("[foo][bar]\n\n[bar]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example536() throws Exception {
        GFM("[link [foo [bar]]][ref]\n\n[ref]: /uri\n",
            "<p><a href=\"/uri\">link [foo [bar]]</a></p>");
    }

    @Test
    public void example537() throws Exception {
        GFM("[link \\[bar][ref]\n\n[ref]: /uri\n",
            "<p><a href=\"/uri\">link [bar</a></p>");
    }

    @Test
    public void example538() throws Exception {
        GFM("[link *foo **bar** `#`*][ref]\n\n[ref]: /uri\n",
            "<p><a href=\"/uri\">link <em>foo <strong>bar</strong> <code>#</code></em></a></p>");
    }

    @Test
    public void example539() throws Exception {
        GFM("[![moon](moon.jpg)][ref]\n\n[ref]: /uri\n",
            "<p><a href=\"/uri\"><img src=\"moon.jpg\" alt=\"moon\" /></a></p>");
    }

    @Test
    public void example540() throws Exception {
        GFM("[foo [bar](/uri)][ref]\n\n[ref]: /uri\n",
            "<p>[foo <a href=\"/uri\">bar</a>]<a href=\"/uri\">ref</a></p>");
    }

    @Test
    public void example541() throws Exception {
        GFM("[foo *bar [baz][ref]*][ref]\n\n[ref]: /uri\n",
            "<p>[foo <em>bar <a href=\"/uri\">baz</a></em>]<a href=\"/uri\">ref</a></p>");
    }

    @Test
    public void example542() throws Exception {
        GFM("*[foo*][ref]\n\n[ref]: /uri\n",
            "<p>*<a href=\"/uri\">foo*</a></p>");
    }

    @Test
    public void example543() throws Exception {
        GFM("[foo *bar][ref]*\n\n[ref]: /uri\n",
            "<p><a href=\"/uri\">foo *bar</a>*</p>");
    }

    @Test
    public void example544() throws Exception {
        GFM("[foo <bar attr=\"][ref]\">\n[ref]: /uri\n",
            "<p>[foo <bar attr=\"][ref]\"></p>");
    }

    @Test
    public void example545() throws Exception {
        GFM("[foo`][ref]`\n\n[ref]: /uri\n", "<p>[foo<code>][ref]</code></p>");
    }

    @Test
    public void example546() throws Exception {
        GFM("[foo<http://example.com/?search=][ref]>\n[ref]: /uri\n",
            "<p>[foo<a href=\"http://example.com/?search=%5D%5Bref%5D\">http://example.com/?search=][ref]</a></p>");
    }

    @Test
    public void example547() throws Exception {
        GFM("[foo][BaR]\n\n[bar]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example548() throws Exception {
        GFM("[ẞ]\n\n[SS]: /url\n", "<p><a href=\"/url\">ẞ</a></p>");
    }

    @Test
    public void example549() throws Exception {
        GFM("[Foo\n  bar]: /url\n\n[Baz][Foo bar]\n",
            "<p><a href=\"/url\">Baz</a></p>");
    }

    @Test
    public void example550() throws Exception {
        GFM("[foo] [bar]\n\n[bar]: /url \"title\"\n",
            "<p>[foo] <a href=\"/url\" title=\"title\">bar</a></p>");
    }

    @Test
    public void example551() throws Exception {
        GFM("[foo]\n[bar]\n\n[bar]: /url \"title\"\n",
            "<p>[foo]\n<a href=\"/url\" title=\"title\">bar</a></p>");
    }

    @Test
    public void example552() throws Exception {
        GFM("[foo]: /url1\n\n[foo]: /url2\n\n[bar][foo]\n",
            "<p><a href=\"/url1\">bar</a></p>");
    }

    @Test
    public void example553() throws Exception {
        GFM("[bar][foo\\!]\n\n[foo!]: /url\n", "<p>[bar][foo!]</p>");
    }

    @Test
    public void example554() throws Exception {
        GFM("[foo][ref[]\n\n[ref[]: /uri\n",
            "<p>[foo][ref[]</p><p>[ref[]: /uri</p>");
    }

    @Test
    public void example555() throws Exception {
        GFM("[foo][ref[bar]]\n\n[ref[bar]]: /uri\n",
            "<p>[foo][ref[bar]]</p><p>[ref[bar]]: /uri</p>");
    }

    @Test
    public void example556() throws Exception {
        GFM("[[[foo]]]\n\n[[[foo]]]: /url\n",
            "<p>[[[foo]]]</p><p>[[[foo]]]: /url</p>");
    }

    @Test
    public void example557() throws Exception {
        GFM("[foo][ref\\[]\n\n[ref\\[]: /uri\n",
            "<p><a href=\"/uri\">foo</a></p>");
    }

    @Test
    public void example558() throws Exception {
        GFM("[bar\\\\]: /uri\n\n[bar\\\\]\n",
            "<p><a href=\"/uri\">bar\\</a></p>");
    }

    @Test
    public void example559() throws Exception {
        GFM("[]\n\n[]: /uri\n", "<p>[]</p><p>[]: /uri</p>");
    }

    @Test
    public void example560() throws Exception {
        GFM("[\n ]\n\n[\n ]: /uri\n", "<p>[\n]</p><p>[\n]: /uri</p>");
    }

    @Test
    public void example561() throws Exception {
        GFM("[foo][]\n\n[foo]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example562() throws Exception {
        GFM("[*foo* bar][]\n\n[*foo* bar]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\"><em>foo</em> bar</a></p>");
    }

    @Test
    public void example563() throws Exception {
        GFM("[Foo][]\n\n[foo]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">Foo</a></p>");
    }

    @Test
    public void example564() throws Exception {
        GFM("[foo] \n[]\n\n[foo]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">foo</a>[]</p>");
    }

    @Test
    public void example565() throws Exception {
        GFM("[foo]\n\n[foo]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example566() throws Exception {
        GFM("[*foo* bar]\n\n[*foo* bar]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\"><em>foo</em> bar</a></p>");
    }

    @Test
    public void example567() throws Exception {
        GFM("[[*foo* bar]]\n\n[*foo* bar]: /url \"title\"\n",
            "<p>[<a href=\"/url\" title=\"title\"><em>foo</em> bar</a>]</p>");
    }

    @Test
    public void example568() throws Exception {
        GFM("[[bar [foo]\n\n[foo]: /url\n",
            "<p>[[bar <a href=\"/url\">foo</a></p>");
    }

    @Test
    public void example569() throws Exception {
        GFM("[Foo]\n\n[foo]: /url \"title\"\n",
            "<p><a href=\"/url\" title=\"title\">Foo</a></p>");
    }

    @Test
    public void example570() throws Exception {
        GFM("[foo] bar\n\n[foo]: /url\n", "<p><a href=\"/url\">foo</a> bar</p>");
    }

    @Test
    public void example571() throws Exception {
        GFM("\\[foo]\n\n[foo]: /url \"title\"\n", "<p>[foo]</p>");
    }

    @Test
    public void example572() throws Exception {
        GFM("[foo*]: /url\n\n*[foo*]\n", "<p>*<a href=\"/url\">foo*</a></p>");
    }

    @Test
    public void example573() throws Exception {
        GFM("[foo][bar]\n\n[foo]: /url1\n[bar]: /url2\n",
            "<p><a href=\"/url2\">foo</a></p>");
    }

    @Test
    public void example574() throws Exception {
        GFM("[foo][]\n\n[foo]: /url1\n", "<p><a href=\"/url1\">foo</a></p>");
    }

    @Test
    public void example575() throws Exception {
        GFM("[foo]()\n\n[foo]: /url1\n", "<p><a href=\"\">foo</a></p>");
    }

    @Test
    public void example576() throws Exception {
        GFM("[foo](not a link)\n\n[foo]: /url1\n",
            "<p><a href=\"/url1\">foo</a>(not a link)</p>");
    }

    @Test
    public void example577() throws Exception {
        GFM("[foo][bar][baz]\n\n[baz]: /url\n",
            "<p>[foo]<a href=\"/url\">bar</a></p>");
    }

    @Test
    public void example578() throws Exception {
        GFM("[foo][bar][baz]\n\n[baz]: /url1\n[bar]: /url2\n",
            "<p><a href=\"/url2\">foo</a><a href=\"/url1\">baz</a></p>");
    }

    @Test
    public void example579() throws Exception {
        GFM("[foo][bar][baz]\n\n[baz]: /url1\n[foo]: /url2\n",
            "<p>[foo]<a href=\"/url1\">bar</a></p>");
    }

    @Test
    public void example580() throws Exception {
        GFM("![foo](/url \"title\")\n",
            "<p><img src=\"/url\" alt=\"foo\" title=\"title\" /></p>");
    }

    @Test
    public void example581() throws Exception {
        GFM("![foo *bar*]\n\n[foo *bar*]: train.jpg \"train & tracks\"\n",
            "<p><img src=\"train.jpg\" alt=\"foo bar\" title=\"train &amp; tracks\" /></p>");
    }

    @Test
    public void example582() throws Exception {
        GFM("![foo ![bar](/url)](/url2)\n",
            "<p><img src=\"/url2\" alt=\"foo bar\" /></p>");
    }

    @Test
    public void example583() throws Exception {
        GFM("![foo [bar](/url)](/url2)\n",
            "<p><img src=\"/url2\" alt=\"foo bar\" /></p>");
    }

    @Test
    public void example584() throws Exception {
        GFM("![foo *bar*][]\n\n[foo *bar*]: train.jpg \"train & tracks\"\n",
            "<p><img src=\"train.jpg\" alt=\"foo bar\" title=\"train &amp; tracks\" /></p>");
    }

    @Test
    public void example585() throws Exception {
        GFM("![foo *bar*][foobar]\n\n[FOOBAR]: train.jpg \"train & tracks\"\n",
            "<p><img src=\"train.jpg\" alt=\"foo bar\" title=\"train &amp; tracks\" /></p>");
    }

    @Test
    public void example586() throws Exception {
        GFM("![foo](train.jpg)\n",
            "<p><img src=\"train.jpg\" alt=\"foo\" /></p>");
    }

    @Test
    public void example587() throws Exception {
        GFM("My ![foo bar](/path/to/train.jpg  \"title\"   )\n",
            "<p>My <img src=\"/path/to/train.jpg\" alt=\"foo bar\" title=\"title\" /></p>");
    }

    @Test
    public void example588() throws Exception {
        GFM("![foo](<url>)\n", "<p><img src=\"url\" alt=\"foo\" /></p>");
    }

    @Test
    public void example589() throws Exception {
        GFM("![](/url)\n", "<p><img src=\"/url\" alt=\"\" /></p>");
    }

    @Test
    public void example590() throws Exception {
        GFM("![foo][bar]\n\n[bar]: /url\n",
            "<p><img src=\"/url\" alt=\"foo\" /></p>");
    }

    @Test
    public void example591() throws Exception {
        GFM("![foo][bar]\n\n[BAR]: /url\n",
            "<p><img src=\"/url\" alt=\"foo\" /></p>");
    }

    @Test
    public void example592() throws Exception {
        GFM("![foo][]\n\n[foo]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"foo\" title=\"title\" /></p>");
    }

    @Test
    public void example593() throws Exception {
        GFM("![*foo* bar][]\n\n[*foo* bar]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"foo bar\" title=\"title\" /></p>");
    }

    @Test
    public void example594() throws Exception {
        GFM("![Foo][]\n\n[foo]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"Foo\" title=\"title\" /></p>");
    }

    @Test
    public void example595() throws Exception {
        GFM("![foo] \n[]\n\n[foo]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"foo\" title=\"title\" />[]</p>");
    }

    @Test
    public void example596() throws Exception {
        GFM("![foo]\n\n[foo]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"foo\" title=\"title\" /></p>");
    }

    @Test
    public void example597() throws Exception {
        GFM("![*foo* bar]\n\n[*foo* bar]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"foo bar\" title=\"title\" /></p>");
    }

    @Test
    public void example598() throws Exception {
        GFM("![[foo]]\n\n[[foo]]: /url \"title\"\n",
            "<p>![[foo]]</p><p>[[foo]]: /url &quot;title&quot;</p>");
    }

    @Test
    public void example599() throws Exception {
        GFM("![Foo]\n\n[foo]: /url \"title\"\n",
            "<p><img src=\"/url\" alt=\"Foo\" title=\"title\" /></p>");
    }

    @Test
    public void example600() throws Exception {
        GFM("!\\[foo]\n\n[foo]: /url \"title\"\n", "<p>![foo]</p>");
    }

    @Test
    public void example601() throws Exception {
        GFM("\\![foo]\n\n[foo]: /url \"title\"\n",
            "<p>!<a href=\"/url\" title=\"title\">foo</a></p>");
    }

    @Test
    public void example602() throws Exception {
        GFM("<http://foo.bar.baz>",
            "<p><a href=\"http://foo.bar.baz\">http://foo.bar.baz</a></p>");
    }

    @Test
    public void example603() throws Exception {
        GFM("<http://foo.bar.baz/test?q=hello&id=22&boolean>",
            "<p><a href=\"http://foo.bar.baz/test?q=hello&amp;id=22&amp;boolean\">http://foo.bar.baz/test?q=hello&amp;id=22&amp;boolean</a></p>");
    }

    @Test
    public void example604() throws Exception {
        GFM("<irc://foo.bar:2233/baz>",
            "<p><a href=\"irc://foo.bar:2233/baz\">irc://foo.bar:2233/baz</a></p>");
    }

    @Test
    public void example605() throws Exception {
        GFM("<MAILTO:FOO@BAR.BAZ>",
            "<p><a href=\"MAILTO:FOO@BAR.BAZ\">MAILTO:FOO@BAR.BAZ</a></p>");
    }

    @Test
    public void example606() throws Exception {
        GFM("<a+b+c:d>", "<p><a href=\"a+b+c:d\">a+b+c:d</a></p>");
    }

    @Test
    public void example607() throws Exception {
        GFM("<made-up-scheme://foo,bar>",
            "<p><a href=\"made-up-scheme://foo,bar\">made-up-scheme://foo,bar</a></p>");
    }

    @Test
    public void example608() throws Exception {
        GFM("<http://../>", "<p><a href=\"http://../\">http://../</a></p>");
    }

    @Test
    public void example609() throws Exception {
        GFM("<localhost:5001/foo>",
            "<p><a href=\"localhost:5001/foo\">localhost:5001/foo</a></p>");
    }

    @Test
    public void example610() throws Exception {
        GFM("<http://foo.bar/baz bim>", "<p>&lt;http://foo.bar/baz bim&gt;</p>");
    }

    @Test
    public void example611() throws Exception {
        GFM("<http://example.com/\\[\\>",
            "<p><a href=\"http://example.com/%5C%5B%5C\">http://example.com/\\[\\</a></p>");
    }

    @Test
    public void example612() throws Exception {
        GFM("<foo@bar.example.com>",
            "<p><a href=\"mailto:foo@bar.example.com\">foo@bar.example.com</a></p>");
    }

    @Test
    public void example613() throws Exception {
        GFM("<foo+special@Bar.baz-bar0.com>",
            "<p><a href=\"mailto:foo+special@Bar.baz-bar0.com\">foo+special@Bar.baz-bar0.com</a></p>");
    }

    @Test
    public void example614() throws Exception {
        GFM("<foo\\+@bar.example.com>", "<p>&lt;foo+@bar.example.com&gt;</p>");
    }

    @Test
    public void example615() throws Exception {
        GFM("<>", "<p>&lt;&gt;</p>");
    }

    @Test
    public void example616() throws Exception {
        GFM("< http://foo.bar >", "<p>&lt; http://foo.bar &gt;</p>");
    }

    @Test
    public void example617() throws Exception {
        GFM("<m:abc>", "<p>&lt;m:abc&gt;</p>");
    }

    @Test
    public void example618() throws Exception {
        GFM("<foo.bar.baz>", "<p>&lt;foo.bar.baz&gt;</p>");
    }

    @Test
    public void example619() throws Exception {
        GFM("http://example.com\n", "<p>http://example.com</p>");
    }

    @Test
    public void example620() throws Exception {
        GFM("foo@bar.example.com\n", "<p>foo@bar.example.com</p>");
    }

    @Test
    public void example621() throws Exception {
        GFM("www.commonmark.org\n",
            "<p><a href=\"http://www.commonmark.org\">www.commonmark.org</a></p>");
    }

    @Test
    public void example622() throws Exception {
        GFM("Visit www.commonmark.org/help for more information.\n",
            "<p>Visit <a href=\"http://www.commonmark.org/help\">www.commonmark.org/help</a> for more information.</p>");
    }

    @Test
    public void example623() throws Exception {
        GFM("Visit www.commonmark.org.\n\nVisit www.commonmark.org/a.b.\n",
            "<p>Visit <a href=\"http://www.commonmark.org\">www.commonmark.org</a>.</p><p>Visit <a href=\"http://www.commonmark.org/a.b\">www.commonmark.org/a.b</a>.</p>");
    }

    @Test
    public void example624() throws Exception {
        GFM("www.google.com/search?q=Markup+(business)\n\nwww.google.com/search?q=Markup+(business)))\n\n(www.google.com/search?q=Markup+(business))\n\n(www.google.com/search?q=Markup+(business)\n",
            "<p><a href=\"http://www.google.com/search?q=Markup+(business)\">www.google.com/search?q=Markup+(business)</a></p><p><a href=\"http://www.google.com/search?q=Markup+(business)\">www.google.com/search?q=Markup+(business)</a>))</p><p>(<a href=\"http://www.google.com/search?q=Markup+(business)\">www.google.com/search?q=Markup+(business)</a>)</p><p>(<a href=\"http://www.google.com/search?q=Markup+(business)\">www.google.com/search?q=Markup+(business)</a></p>");
    }

    @Test
    public void example625() throws Exception {
        GFM("www.google.com/search?q=(business))+ok\n",
            "<p><a href=\"http://www.google.com/search?q=(business))+ok\">www.google.com/search?q=(business))+ok</a></p>");
    }

    @Test
    public void example626() throws Exception {
        GFM("www.google.com/search?q=commonmark&hl=en\n\nwww.google.com/search?q=commonmark&hl;\n",
            "<p><a href=\"http://www.google.com/search?q=commonmark&amp;hl=en\">www.google.com/search?q=commonmark&amp;hl=en</a></p><p><a href=\"http://www.google.com/search?q=commonmark\">www.google.com/search?q=commonmark</a>&amp;hl;</p>");
    }

    @Test
    public void example627() throws Exception {
        GFM("www.commonmark.org/he<lp\n",
            "<p><a href=\"http://www.commonmark.org/he\">www.commonmark.org/he</a>&lt;lp</p>");
    }

    @Test
    public void example628() throws Exception {
        GFM("http://commonmark.org\n\n(Visit https://encrypted.google.com/search?q=Markup+(business))\n",
            "<p><a href=\"http://commonmark.org\">http://commonmark.org</a></p><p>(Visit <a href=\"https://encrypted.google.com/search?q=Markup+(business)\">https://encrypted.google.com/search?q=Markup+(business)</a>)</p>");
    }

    @Test
    public void example629() throws Exception {
        GFM("foo@bar.baz\n",
            "<p><a href=\"mailto:foo@bar.baz\">foo@bar.baz</a></p>");
    }

    @Test
    public void example630() throws Exception {
        GFM("hello@mail+xyz.example isn't valid, but hello+xyz@mail.example is.\n",
            "<p>hello@mail+xyz.example isn't valid, but <a href=\"mailto:hello+xyz@mail.example\">hello+xyz@mail.example</a> is.</p>");
    }

    @Test
    public void example631() throws Exception {
        GFM("a.b-c_d@a.b\n\na.b-c_d@a.b.\n\na.b-c_d@a.b-\n\na.b-c_d@a.b_\n",
            "<p><a href=\"mailto:a.b-c_d@a.b\">a.b-c_d@a.b</a></p><p><a href=\"mailto:a.b-c_d@a.b\">a.b-c_d@a.b</a>.</p><p>a.b-c_d@a.b-</p><p>a.b-c_d@a.b_</p>");
    }

    @Test
    public void example632() throws Exception {
        GFM("<a><bab><c2c>", "<p><a><bab><c2c></p>");
    }

    @Test
    public void example633() throws Exception {
        GFM("<a/><b2/>", "<p><a/><b2/></p>");
    }

    @Test
    public void example634() throws Exception {
        GFM("<a  /><b2\ndata=\"foo\" >", "<p><a  /><b2\ndata=\"foo\" ></p>");
    }

    @Test
    public void example635() throws Exception {
        GFM("<a foo=\"bar\" bam = 'baz <em>\"</em>'\n_boolean zoop:33=zoop:33 />",
            "<p><a foo=\"bar\" bam = 'baz <em>\"</em>'\n_boolean zoop:33=zoop:33 /></p>");
    }

    @Test
    public void example636() throws Exception {
        GFM("Foo <responsive-image src=\"foo.jpg\" />",
            "<p>Foo <responsive-image src=\"foo.jpg\" /></p>");
    }

    @Test
    public void example637() throws Exception {
        GFM("<33> <__>", "<p>&lt;33&gt; &lt;__&gt;</p>");
    }

    @Test
    public void example638() throws Exception {
        GFM("<a h*#ref=\"hi\">", "<p>&lt;a h*#ref=&quot;hi&quot;&gt;</p>");
    }

    @Test
    public void example639() throws Exception {
        GFM("<a href=\"hi'> <a href=hi'>",
            "<p>&lt;a href=&quot;hi'&gt; &lt;a href=hi'&gt;</p>");
    }

    @Test
    public void example640() throws Exception {
        GFM("< a><\nfoo><bar/ >\n<foo bar=baz\nbim!bop />",
            "<p>&lt; a&gt;&lt;\nfoo&gt;&lt;bar/ &gt;\n&lt;foo bar=baz\nbim!bop /&gt;</p>");
    }

    @Test
    public void example641() throws Exception {
        GFM("<a href='bar'title=title>",
            "<p>&lt;a href='bar'title=title&gt;</p>");
    }

    @Test
    public void example642() throws Exception {
        GFM("</a></foo >", "<p></a></foo ></p>");
    }

    @Test
    public void example643() throws Exception {
        GFM("</a href=\"foo\">", "<p>&lt;/a href=&quot;foo&quot;&gt;</p>");
    }

    @Test
    public void example644() throws Exception {
        GFM("foo <!-- this is a\ncomment - with hyphen -->",
            "<p>foo <!-- this is a\ncomment - with hyphen --></p>");
    }

    @Test
    public void example645() throws Exception {
        GFM("foo <!-- not a comment -- two hyphens -->",
            "<p>foo &lt;!-- not a comment -- two hyphens --&gt;</p>");
    }

    @Test
    public void example646() throws Exception {
        GFM("foo <!--> foo -->\nfoo <!-- foo--->",
            "<p>foo &lt;!--&gt; foo --&gt;</p><p>foo &lt;!-- foo---&gt;</p>");
    }

    @Test
    public void example647() throws Exception {
        GFM("foo <?php echo $a; ?>", "<p>foo <?php echo $a; ?></p>");
    }

    @Test
    public void example648() throws Exception {
        GFM("foo <!ELEMENT br EMPTY>", "<p>foo <!ELEMENT br EMPTY></p>");
    }

    @Test
    public void example649() throws Exception {
        GFM("foo <![CDATA[>&<]]>", "<p>foo <![CDATA[>&<]]></p>");
    }

    @Test
    public void example650() throws Exception {
        GFM("foo <a href=\"&ouml;\">", "<p>foo <a href=\"&ouml;\"></p>");
    }

    @Test
    public void example651() throws Exception {
        GFM("foo <a href=\"\\*\">", "<p>foo <a href=\"\\*\"></p>");
    }

    @Test
    public void example652() throws Exception {
        GFM("<a href=\"\\\"\">", "<p>&lt;a href=&quot;&quot;&quot;&gt;</p>");
    }

    @Test
    public void example653() throws Exception {
        GFM("<strong> <title> <style> <em>\n<blockquote>  <xmp> is disallowed.  <XMP> is also disallowed.\n</blockquote>",
            "<p><strong> &lt;title> &lt;style> <em></p><blockquote>  &lt;xmp> is disallowed.  &lt;XMP> is also disallowed.\n</blockquote>");
    }

    @Test
    public void example654() throws Exception {
        GFM("foo  \nbaz\n", "<p>foo<br />\nbaz</p>");
    }

    @Test
    public void example655() throws Exception {
        GFM("foo\\\nbaz\n", "<p>foo<br />\nbaz</p>");
    }

    @Test
    public void example656() throws Exception {
        GFM("foo       \nbaz\n", "<p>foo<br />\nbaz</p>");
    }

    @Test
    public void example657() throws Exception {
        GFM("foo  \n     bar\n", "<p>foo<br />\nbar</p>");
    }

    @Test
    public void example658() throws Exception {
        GFM("foo\\\n     bar\n", "<p>foo<br />\nbar</p>");
    }

    @Test
    public void example659() throws Exception {
        GFM("*foo  \nbar*\n", "<p><em>foo<br />\nbar</em></p>");
    }

    @Test
    public void example660() throws Exception {
        GFM("*foo\\\nbar*\n", "<p><em>foo<br />\bar</em></p>");
    }

    @Test
    public void example661() throws Exception {
        GFM("`code  \nspan`\n", "<p><code>code   span</code></p>");
    }

    @Test
    public void example662() throws Exception {
        GFM("`code\\\nspan`\n", "<p><code>code\\ span</code></p>");
    }

    @Test
    public void example663() throws Exception {
        GFM("<a href=\"foo  \nbar\">", "<p><a href=\"foo  \nbar\"></p>");
    }

    @Test
    public void example664() throws Exception {
        GFM("<a href=\"foo\\\nbar\">", "<p><a href=\"foo\\\nbar\"></p>");
    }

    @Test
    public void example665() throws Exception {
        GFM("foo\\\n", "<p>foo\\</p>");
    }

    @Test
    public void example666() throws Exception {
        GFM("foo  \n", "<p>foo</p>");
    }

    @Test
    public void example667() throws Exception {
        GFM("### foo\\\n", "<h3>foo\\</h3>");
    }

    @Test
    public void example668() throws Exception {
        GFM("### foo  \n", "<h3>foo</h3>");
    }

    @Test
    public void example669() throws Exception {
        GFM("foo\nbaz\n", "<p>foo\nbaz</p>");
    }

    @Test
    public void example670() throws Exception {
        GFM("foo \n baz\n", "<p>foo\nbaz</p>");
    }

    @Test
    public void example671() throws Exception {
        GFM("hello $.;'there\n", "<p>hello $.;'there</p>");
    }

    @Test
    public void example672() throws Exception {
        GFM("Foo χρῆν\n", "<p>Foo χρῆν</p>");
    }

    @Test
    public void example673() throws Exception {
        GFM("Multiple     spaces\n", "<p>Multiple     spaces</p>");
    }

    private void GFM(String markdown, String expect) throws Exception {
        assertEquals(expect, MD.parse(markdown));
    }
}
