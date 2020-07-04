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

import static com.shorindo.docs.markdown.MarkdownParser.MarkdownRules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import com.shorindo.docs.action.ActionLogger;
import com.shorindo.util.PEGCombinator;
import com.shorindo.util.PEGCombinator.PEGContext;
import com.shorindo.util.PEGCombinator.PEGException;
import com.shorindo.util.PEGCombinator.PEGNode;
import com.shorindo.util.PEGCombinator.Rule;
import com.shorindo.util.PEGCombinator.RuleTypes;
import com.shorindo.util.PEGCombinator.UnmatchException;

/**
 * 
 */
public class MarkdownParser {
    private static final String PUNCTUATION = "!\"#$%&'\\(\\)\\*\\+\\,\\-./:;<=>?@\\[\\\\\\]^_`{|}~";
    private static final ActionLogger LOG = ActionLogger.getLogger(MarkdownParser.class);
    private static final PEGCombinator PEG = new PEGCombinator();
    //private static final Map<String,String> varMap = new HashMap<>();

    static {
        PEG.define(MARKDOWN,
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_HR),
                    PEG.rule(MD_LIST),
                    PEG.rule(MD_OLIST),
                    PEG.rule(MD_HEAD),
                    PEG.rule(MD_QUOTE),
                    PEG.rule(MD_STX_H1),
                    PEG.rule(MD_STX_H2),
                    PEG.rule(MD_PRE),
                    PEG.rule(MD_CODE_BLOCK),
                    PEG.rule(MD_HTML_BLOCK),
                    PEG.rule(MD_TABLE),
                    PEG.rule(MD_LINK_DEF),
                    PEG.rule(MD_PARA),
                    PEG.rule(MD_EMPTY),
                    PEG.rule(MD_EOL)
                        .action($$ -> {
                            $$.setValue("");
                            return $$;
                        }))),
            PEG.rule(MD_EOF))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    PEGNode $i = $0.get(i).get(0);
                    if (!"\n".equals($i.getValue())) { // FIXME
                        sb.append($i.getValue());
                    }
                }
                $$.setValue(sb.toString());
                return $$;
            });
        
        PEG.define(MD_BLOCK,
            PEG.rule$Choice(
                PEG.rule(MD_HR),
                PEG.rule(MD_LIST),
                PEG.rule(MD_OLIST),
                PEG.rule(MD_HEAD),
                PEG.rule(MD_QUOTE),
                PEG.rule(MD_STX_H1),
                PEG.rule(MD_STX_H2),
                PEG.rule(MD_PRE),
                PEG.rule(MD_CODE_BLOCK)
//                PEG.rule(MD_HTML_BLOCK),
//                PEG.rule(MD_LINK_DEF),
//                PEG.rule(MD_PARA)
                ));

        PEG.define(MD_QUOTE_BLOCK,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_HR),
                    PEG.rule(MD_LIST),
                    PEG.rule(MD_OLIST),
                    PEG.rule(MD_HEAD),
                    PEG.rule(MD_QUOTE),
//                PEG.rule(MD_STX_H1),
//                PEG.rule(MD_STX_H2),
                    PEG.rule(MD_PRE),
                    PEG.rule(MD_CODE_BLOCK),
                    PEG.rule(MD_HTML_BLOCK),
                    PEG.rule(MD_LINK_DEF),
                    PEG.rule(MD_PARA),
                    PEG.rule(MD_EMPTY),
                    PEG.rule(MD_EOL)
                    )))
            .action($$ -> {
                return $$.pack();
            });

        // 見出し１
        // ========
        PEG.define(MD_STX_H1,
            PEG.rule(MD_PRESPACES),
            PEG.rule(MD_INLINE),
            PEG.rule(MD_EOL),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule(MD_STX_H1_UNDER)),
                PEG.rule$OneOrMore(PEG.rule(MD_INLINE)),
                PEG.rule(MD_EOL)),
            PEG.rule(MD_STX_H1_UNDER))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().trim());
                if ($$.get(3).length() > 0) {
                    sb.append("\n");
                    sb.append($$.get(3).pack().getValue());
                }
                $$.setValue(H1(sb.toString().trim()));
                return $$;
            });
        PEG.define(MD_STX_H1_UNDER,
            PEG.rule(MD_PRESPACES),
            PEG.rule$OneOrMore(PEG.rule$Literal("=")),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class(" \t")),
            PEG.rule(MD_EOL_OR_EOF));
        
        // 見出し２
        // --------
        PEG.define(MD_STX_H2,
            PEG.rule(MD_PRESPACES),
            PEG.rule(MD_INLINE),
            PEG.rule(MD_EOL),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule(MD_STX_H2_UNDER)),
                PEG.rule$OneOrMore(
                    PEG.rule(MD_INLINE)),
                PEG.rule(MD_EOL)),
            PEG.rule(MD_STX_H2_UNDER))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().trim());
                if ($$.get(3).length() > 0) {
                    sb.append("\n");
                    sb.append($$.get(3).pack().getValue());
                }
                $$.setValue(H2(sb.toString().trim()));
                return $$;
            });
        PEG.define(MD_STX_H2_UNDER,
            PEG.rule(MD_PRESPACES),
            PEG.rule$OneOrMore(PEG.rule$Literal("-")),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class(" \t")),
            PEG.rule(MD_EOL_OR_EOF));

        // #+ 見出し
        PEG.define(MD_HEAD,
            PEG.rule(MD_PRESPACES),
            PEG.rule$Choice(
                PEG.rule$Literal("######"),
                PEG.rule$Literal("#####"),
                PEG.rule$Literal("####"),
                PEG.rule$Literal("###"),
                PEG.rule$Literal("##"),
                PEG.rule$Literal("#")),
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$OneOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^\n")),
                    PEG.rule(MD_EOL_OR_EOF))
                    .action($$ -> {
                        return $$.get(1);
                    }),
                PEG.rule(MD_EOL)))
            .action($$ -> {
                int depth = $$.get(1).getValue().length();
                PEGNode $2 = $$.get(2);
                if (depth <= 6) {
                    String title = $2.pack().getValue()
                        .trim()
                        .replaceAll(" +((?!\\\\)#)+$", "")
                        .replaceAll("\\\\#", "#")
                        .replaceAll("^#+$", "");
                    if (!"".equals(title)) {
                        PEGContext ctx = $$.getContext().createContext(title);
                        try {
                            title = PEG.rule(MD_INLINE).accept(ctx).getValue();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    switch(depth) {
                    case 1: $$.setValue(H1(title)); break;
                    case 2: $$.setValue(H2(title)); break;
                    case 3: $$.setValue(H3(title)); break;
                    case 4: $$.setValue(H4(title)); break;
                    case 5: $$.setValue(H5(title)); break;
                    case 6: $$.setValue(H6(title)); break;
                    default: $$.setValue($$.getSource());
                    }
                } else {
                    $$.setValue($$.getSource());
                }
                return $$;
            });

        // 整形済
        PEG.define(MD_PRE,
            PEG.rule$Choice(
                PEG.rule$Literal("    "),
                PEG.rule$Literal("\t"),
                PEG.rule$Literal(" \t"),
                PEG.rule$Literal("  \t"),
                PEG.rule$Literal("   \t")),
            PEG.rule$RegExp("[^\n]*"),
            PEG.rule(MD_EOL_OR_EOF),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$RegExp(" *"),
                        PEG.rule(MD_EOL))
                        .action($$ -> {
                            $$.setType(MD_EOL);
                            $$.setValue($$.get(0).pack().getValue().replaceAll("^ {1,4}", "") + "\n");
                            return $$;
                        }),
                    PEG.rule$Sequence(
                        PEG.rule$Choice(
                            PEG.rule$Literal("    "),
                            PEG.rule$Literal("\t"),
                            PEG.rule$Literal(" \t"),
                            PEG.rule$Literal("  \t"),
                            PEG.rule$Literal("   \t")),
                        PEG.rule$RegExp("[^\n]*"),
                        PEG.rule(MD_EOL_OR_EOF)))))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                sb.append($$.get(1).getValue());
                sb.append("\n");
                PEGNode zeroOrMore = $$.get(3);
                for (int i = 0; i < zeroOrMore.length(); i++) {
                    PEGNode choice = zeroOrMore.get(i).get(0);
                    if (choice.getType() == MD_EOL) {
                        sb.append(choice.getValue());
                    } else {
                        sb.append(choice.get(1).getValue());
                        sb.append("\n");
                    }
                }
                String code = escapeHTML(sb.toString()
                    .replaceAll("^\n+", "")
                    .replaceAll("\n+$", "\n"));
                $$.clear();
                $$.setValue("<pre><code>" + code + "</code></pre>");
                return $$;
            });

        // コード
        PEG.define(MD_CODE_BLOCK,
            PEG.rule$Choice( // ださい...
                createCodeBlock("``````"),
                createCodeBlock("`````"),
                createCodeBlock("````"),
                createCodeBlock("```"),
                createCodeBlock("~~~~~~"),
                createCodeBlock("~~~~~"),
                createCodeBlock("~~~~"),
                createCodeBlock("~~~")))
            .action($$ -> {
                return $$.pack();
            });

        // 引用
        PEG.define(MD_QUOTE,
            PEG.rule$Sequence(
                PEG.rule(MD_PRESPACES),
                PEG.rule$RegExp(">+([ \t])")
                    .action($$ -> {
                        if ("\t".equals($$.get(1).getValue())) {
                            $$.setValue("   ");
                        } else {
                            $$.setValue("");
                        }
                        return $$;
                    }),
                PEG.rule$ZeroOrMore(
                    PEG.rule(MD_CHAR)),
                PEG.rule(MD_EOL_OR_EOF))
                .action($$ -> {
                    String line = $$.get(1).getValue() + 
                        $$.get(2).pack().getValue().replaceAll("(?=\\s)\t", "   ") +
                        $$.get(3).pack().getValue();
                    $$.setValue(line);
                    return $$;
                }),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule$Choice(
                        PEG.rule(MD_HR))),
                PEG.rule(MD_PRESPACES),
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal("> "),
                        PEG.rule$ZeroOrMore(
                            PEG.rule(MD_CHAR)))
                        .action($$ -> {
                            return $$.get(1).pack();
                        }),
                    PEG.rule$OneOrMore(
                        PEG.rule(MD_CHAR))),
                PEG.rule(MD_EOL_OR_EOF))
                .action($$ -> {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < $$.length(); i++) {
                        PEGNode $i = $$.get(i);
                        sb.append($i.get(2).pack().getValue());
                        sb.append($i.get(3).pack().getValue());
                    }
                    return $$;
                }))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue());
                sb.append($$.get(1).pack().getValue());
                PEGContext ctx = $$.getContext().createContext(sb.toString());
                try {
                    StringBuffer result = new StringBuffer();
                    result.append("<blockquote>");
                    result.append(PEG.rule(MD_QUOTE_BLOCK).accept(ctx).getValue());
                    result.append("</blockquote>");
                    $$.setValue(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return $$;
            });

        PEG.define(MD_LIST,
            PEG.rule$OneOrMore(
                PEG.rule$Not(PEG.rule(MD_HR)),
                PEG.rule$Choice(
                    PEG.rule(MD_LIST_AST),
                    PEG.rule(MD_LIST_PLUS),
                    PEG.rule(MD_LIST_BAR))))
            .action($$ -> {
                // 各アイテムが空行を含むかどうか
                boolean loose = isLoose($$);
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                sb.append("<ul>");
                for (int i = 0; i < $0.length(); i++) {
                    String item = $0.get(i).get(1).getValue();
                    if (!loose) {
                        // FIXME
                        item = item.replaceAll("^<li><p>(.*?)</p>", "<li>$1");
                    }
                    sb.append(item);
                }
                sb.append("</ul>");
                $$.setValue(sb.toString());
                return $$;
            });

        PEG.define(MD_LIST_AST,
            PEG.rule$Choice(
                createListItem("   *    "),
                createListItem("   *   "),
                createListItem("   *  "),
                createListItem("   * "),
                createListItem("   *\t"),
                createListItem("  *    "),
                createListItem("  *   "),
                createListItem("  *  "),
                createListItem("  * "),
                createListItem("  *\t"),
                createListItem(" *    "),
                createListItem(" *   "),
                createListItem(" *  "),
                createListItem(" * "),
                createListItem(" *\t"),
                createListItem("*    "),
                createListItem("*   "),
                createListItem("*  "),
                createListItem("* "),
                createListItem("*\t")
            ))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                sb.append("<li>" + $$.get(0).get(1).getValue().trim() + "</li>");
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(MD_LIST_PLUS,
            PEG.rule$Choice(
                createListItem("   +    "),
                createListItem("   +   "),
                createListItem("   +  "),
                createListItem("   + "),
                createListItem("   +\t"),
                createListItem("  +    "),
                createListItem("  +   "),
                createListItem("  +  "),
                createListItem("  + "),
                createListItem("  +\t"),
                createListItem(" +    "),
                createListItem(" +   "),
                createListItem(" +  "),
                createListItem(" + "),
                createListItem(" +\t"),
                createListItem("+    "),
                createListItem("+   "),
                createListItem("+  "),
                createListItem("+ "),
                createListItem("+\t")
            ))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                sb.append("<li>" + $$.get(0).getValue().trim() + "</li>");
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(MD_LIST_BAR,
            PEG.rule$Choice(
                createListItem("   -    "),
                createListItem("   -   "),
                createListItem("   -  "),
                createListItem("   - "),
                createListItem("   -\t"),
                createListItem("  -    "),
                createListItem("  -   "),
                createListItem("  -  "),
                createListItem("  - "),
                createListItem("  -\t"),
                createListItem(" -    "),
                createListItem(" -   "),
                createListItem(" -  "),
                createListItem(" - "),
                createListItem(" -\t"),
                createListItem("-    "),
                createListItem("-   "),
                createListItem("-  "),
                createListItem("- "),
                createListItem("-\t")
            ))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                sb.append("<li>" + $$.get(0).getValue().trim() + "</li>");
                $$.setValue(sb.toString());
                return $$;
            });

        PEG.define(MD_OLIST,
            PEG.rule$OneOrMore(
                PEG.rule(MD_OLIST_ITEM)))
            .action($$ -> {
                StringBuffer sb = new StringBuffer("<ol");
                if ($$.get(0).get(0).get(0).length() > 3) {
                    PEGNode $start = $$.get(0).get(0).get(0).get(3);
                    int start = Integer.parseInt($start.getValue());
                    if (start == 0 || start > 1) {
                        sb.append(" start=\"" + start + "\"");
                    }
                }
                sb.append(">");
                for (int i = 0; i < $$.get(0).length(); i++) {
                    PEGNode $i = $$.get(0).get(i);
                    sb.append($i.pack().getValue());
                }
                sb.append("</ol>");
                $$.setValue(sb.toString());
                return $$;
            });

        PEG.define(MD_OLIST_ITEM,
            PEG.rule$RegExp("( {0,3})(\\d{1,9})([\\.\\)])[ \n]( {0,3})"),
            PEG.rule$RegExp("[^\n]+"),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                int depth = $$.get(0).get(1).getValue().length();
                String start = $$.get(0).get(2).getValue();
                String marker = $$.get(0).get(2).getValue() +
                    $$.get(0).get(3).getValue();
                String offset = spaces($$.get(0).getValue().length());
                String first = $$.get(1).pack().getValue();
                depth = depth > 1 ? depth - 1 : 0;
                Rule nextRule = 
                    PEG.rule$Sequence(
                        // 同一レベル以下のリストは含まない
                        PEG.rule$Not(
                            PEG.rule$RegExp(" {0," + (depth + 1) + "}" +
                                "(\\*|\\-|\\+|\\d+\\.|\\d\\))" +
                                " {1,3}" +
                                "[^\n]*")),
                        // 空行を含まない継続行
                        PEG.rule$ZeroOrMore(
                            PEG.rule$RegExp("[^\n]+").action($a -> {
                                $a.setValue(
                                    $a.pack().getValue().replaceAll("^ +", ""));
                                return $a;
                            }),
                            PEG.rule(MD_EOL_OR_EOF)),
                        // 空行を含む継続行
                        PEG.rule$ZeroOrMore(
                            PEG.rule$Choice(
                                PEG.rule$Sequence(
                                    PEG.rule$Literal(offset),
                                    PEG.rule$RegExp("[^\n]+"),
                                    PEG.rule(MD_EOL_OR_EOF))
                                    .action($i -> {
                                        $i.setValue(
                                            $i.get(1).pack().getValue() +
                                            $i.get(2).pack().getValue());
                                        return $i;
                                    }),
                                PEG.rule(MD_EOL))));
                try {
                    // 子要素を入れ替える
                    $$.clear();
                    PEGNode $indent = new PEGNode($$.getContext(), MD_OLIST_ITEM);
                    $indent.setValue(spaces(depth));
                    $$.add($indent);
                    PEGNode $marker = new PEGNode($$.getContext(), MD_OLIST_ITEM);
                    $marker.setValue(marker);
                    $$.add($marker);
                    PEGNode $offset = new PEGNode($$.getContext(), MD_OLIST_ITEM);
                    $offset.setValue(offset);
                    $$.add($offset);
                    PEGNode $start = new PEGNode($$.getContext(), MD_OLIST_ITEM);
                    $start.setValue(start);
                    $$.add($start);

                    // 継続行があるときは<p></p>
                    String next = nextRule.accept($$.getContext()).pack().getValue();
                    PEGNode node = null;
                    if ("".equals(next)) {
                        PEGContext ctx = $$.getContext().createContext(first);
                        node = PEG.rule(MD_INLINE).accept(ctx);
                    } else {
                        PEGContext ctx = $$.getContext().createContext(first + "\n" + next);
                        node = PEG.rule(MARKDOWN).accept(ctx);
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("<li>");
                    sb.append(node.pack().getValue());
                    sb.append("</li>");
                    $$.setValue(sb.toString());
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    //LOG.info(code, args);
                }
                return $$.pack();
            });

        // 水平線
        PEG.define(MD_HR,
            PEG.rule(MD_PRESPACES),
            PEG.rule$Choice(
                PEG.rule$RegExp("\\*[ \t]*\\*[ \t]*\\*[ \t\\*]*"),
                PEG.rule$RegExp("\\-[ \t]*\\-[ \t]*\\-[ \t\\-]*"),
                PEG.rule$RegExp("\\_[ \t]*\\_[ \t]*\\_[ \t\\_]*")),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                $$.setValue("<hr>");
                return $$;
            });
        
        // テーブル
        PEG.define(MD_TABLE,
            PEG.rule$RegExp("\\|((\\\\\\||[^\\|])+)"),
            PEG.rule$ZeroOrMore(
                PEG.rule$RegExp("\\|((\\\\\\||[^\\|\n])+)")),
            PEG.rule$Literal("|"),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                int columnCount = 1 + $$.get(1).length();
                StringBuffer sb = new StringBuffer();
                sb.append("<table><thead><tr>");
                List<String> heads = new ArrayList<>();
                heads.add($$.get(0).get(1).getValue().trim());
                for (int i = 0; i < $$.get(1).length(); i++) {
                    PEGNode $i = $$.get(1).get(i);
                    heads.add($i.get(0).get(1).getValue().trim());
                }
                StringBuffer delimiterRow = new StringBuffer();
                delimiterRow.append("\\|?((?:\\\\\\||[^\\|])+)");
                for (int i = 1; i < columnCount; i++) {
                    delimiterRow.append("\\|((?:\\\\\\||[^\\|])+)");
                }
                delimiterRow.append("\\|?\n");
                Rule delimiterRule = PEG.rule$RegExp(delimiterRow.toString());
                List<String> aligns = new ArrayList<>();
                try {
                    PEGNode $dels = delimiterRule.accept($$.getContext()).pack();
                    for (int i = 0; i < $dels.length() - 1; i++) {
                        String head = unescape(heads.get(i));
                        String align = $dels.get(i + 1).getValue().trim();
                        if (align.matches("^:\\-+$")) {
                            aligns.add(" align=\"left\"");
                            sb.append("<th align=\"left\">" + head + "</th>");
                        } else if (align.matches("^\\-+:$")) {
                            aligns.add(" align=\"right\"");
                            sb.append("<th align=\"right\">" + head + "</th>");
                        } else if (align.matches("^:\\-+:$")) {
                            aligns.add(" align=\"center\"");
                            sb.append("<th align=\"center\">" + head + "</th>");
                        } else {
                            aligns.add("");
                            sb.append("<th>" + head + "</th>");
                        }
                    }
                    sb.append("</tr></thead>");
                } catch (PEGException e) {
                    $$.setValue($$.getSource());
                    return $$;
                }

                Rule rowRule = 
                    PEG.rule$ZeroOrMore(
                        PEG.rule$RegExp(delimiterRow.toString()))
                        .action($z -> {
                            StringBuffer line = new StringBuffer("<tr>");
                            for (int row = 0; row < $z.length(); row++) {
                                PEGNode $row = $z.get(0).get(0);
                                for (int col = 1; col < $row.length(); col++) {
                                    PEGNode $cell = $row.get(col);
                                    line.append("<td" + aligns.get(col - 1) + ">" + unescape($cell.pack().getValue().trim()) + "</td>");
                                }
                                line.append("</tr>");
                            }
                            $z.setValue(line.toString());
                            return $z;
                        });
                try {
                    PEGNode $rows = rowRule.accept($$.getContext()).pack();
                    if ($rows.length() > 0) {
                        sb.append("<tbody>");
                        sb.append($rows.getValue());
                        sb.append("</tbody>");
                    }
                    sb.append("</table>");
                    $$.setValue(sb.toString());
                } catch (Exception e) {
                    $$.setValue($$.getSource());
                }
                return $$;
            });
        
        // パラグラフ
        PEG.define(MD_PARA,
            PEG.rule(MD_LINE),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule$Choice(
                        PEG.rule(MD_CODE_BLOCK),
                        PEG.rule(MD_HR),
                        PEG.rule(MD_HEAD))),
                PEG.rule$Sequence(
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class(" \t")),
                    PEG.rule(MD_LINE))
                    .action($$ -> {
                        return $$.get(1).pack();
                    })))
            .action($$ -> {
                $$.setValue(P($$.pack().getValue().trim()
                    .replaceAll("<br />$", "")));
                return $$;
            });

        // 行
        PEG.define(MD_LINE,
            PEG.rule$Not(
                PEG.rule$Choice(
//                    PEG.rule(MD_HR),
                    PEG.rule(MD_LIST),
//                    PEG.rule(MD_HEAD),
//                    PEG.rule(MD_STX_H1),
//                    PEG.rule(MD_STX_H2),
//                    PEG.rule(MD_PRE),
//                    PEG.rule(MD_CODE_BLOCK),
                    PEG.rule(MD_QUOTE))),
            PEG.rule(MD_PRESPACES),
            PEG.rule(MD_INLINE),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                String line = $$.get(2).pack()
                    .getValue()
                    .replaceAll("^\\s+", "")
                    .replaceAll("  +$", "<br />")
                    .replaceAll("\\\\$", "<br />")
                    .replaceAll(" $", "");
                if ($$.get(3).length() > 0) {
                    line += $$.get(3).get(0).getValue();
                }
                $$.setValue(line);
                return $$;
            });

        // インライン
        PEG.define(MD_INLINE,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_LINK),
                    PEG.rule(MD_CODE_SPAN),
                    PEG.rule(MD_AUTO_LINK),
                    PEG.rule(MD_PLAIN_LINK),
                    PEG.rule(MD_LINK_REFREF),
                    PEG.rule(MD_LINK_REF),
                    PEG.rule(MD_STRONG),
                    PEG.rule(MD_EMPHASIS),
                    PEG.rule(MD_HTML),
                    PEG.rule(MD_IMAGE),
                    PEG.rule(MD_NUMERIC_REF),
                    PEG.rule(MD_ENTITY_REF),
                    PEG.rule(MD_ESCAPED),
                    PEG.rule(MD_SPECIAL),
                    PEG.rule(MD_CHAR))))
            .action($$ -> {
                $$.setValue($$.pack().getValue());
                return $$;
            });

        PEG.define(MD_EMPHASIS,
            PEG.rule$Choice(
                createNotModification("_"),
                createModification("*"),
                createModification("_")));
        
        PEG.define(MD_STRONG,
            PEG.rule$Choice(
                createNotModification("__"),
                createModification("**"),
                createModification("__")));

//            PEG.rule$Choice(
//                PEG.rule$Sequence(
//                    PEG.rule$Choice(
//                        PEG.rule$Not(
//                            PEG.rule(MD_NOT_PUNCTUATION)),
//                        PEG.rule$Literal("*"),
//                        PEG.rule(MD_PUNCTUATION)
//                        ),
//                    PEG.rule$Choice(
//                        PEG.rule(MD_STRONG),
//                        PEG.rule(MD_EMPH),
//                        PEG.rule$ZeroOrMore(
//                            PEG.rule$Class("^*"))),
//                    PEG.rule$Literal("*")
//                    ),
//                PEG.rule$Sequence(
//                    PEG.rule$Literal("*"),
//                    PEG.rule$Choice(
//                        PEG.rule(MD_STRONG),
//                        PEG.rule(MD_EMPH),
//                        PEG.rule$ZeroOrMore(
//                            PEG.rule$Class("^*"))),
//                    PEG.rule$Literal("*"))));

        PEG.define(MD_ITALIC,
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$Not(-1, PEG.rule(MD_PUNCTUATION)),
                    PEG.rule$Literal("_"),
                    PEG.rule$Not(
                        PEG.rule$Choice(
                            PEG.rule$Literal(" "),
                            PEG.rule(MD_PUNCTUATION))),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(PEG.rule$Literal("_")),
                        PEG.rule$Choice(
                            PEG.rule(MD_BOLD),
                            PEG.rule$Sequence(
                                PEG.rule$Not(PEG.rule$Literal("_")),
                                PEG.rule$Any(), //PEG.rule$Class("^\n"),
                                PEG.rule$Not(PEG.rule$Literal(" _")))
                                .action($$ -> {
                                    $$.setValue($$.get(1).getValue());
                                    return $$;
                                }))),
                    PEG.rule$Literal("_"))
                    .action($$ -> {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < $$.get(3).length(); i++) {
                            sb.append($$.get(3).get(i).get(1).getValue());
                        }
                        $$.setValue("<em>" + sb.toString() + "</em>");
                        return $$;
                    }),
               PEG.rule$Sequence(
                   PEG.rule$Not(-1, PEG.rule(MD_PUNCTUATION)),
                   PEG.rule$Literal("*"),
                   PEG.rule$Not(
                       PEG.rule$Choice(
                           PEG.rule$Literal(" "),
                           PEG.rule(MD_PUNCTUATION))),
                   PEG.rule$OneOrMore(
                       PEG.rule$Not(PEG.rule$Literal("*")),
                       PEG.rule$Choice(
                           PEG.rule(MD_BOLD),
                           PEG.rule$Sequence(
                               PEG.rule$Not(PEG.rule$Literal("*")),
                               PEG.rule$Any(), //PEG.rule$Class("^\n"),
                               PEG.rule$Not(PEG.rule$Literal(" *")))
                               .action($$ -> {
                                   $$.setValue($$.get(1).getValue());
                                   return $$;
                               }))),
                   PEG.rule$Literal("*"))
                   .action($$ -> {
                       StringBuffer sb = new StringBuffer();
                       for (int i = 0; i < $$.get(3).length(); i++) {
                           sb.append($$.get(3).get(i).get(1).getValue());
                       }
                       $$.setValue("<em>" + sb.toString() + "</em>");
                       return $$;
                   })))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

        PEG.define(MD_BOLD,
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    //PEG.rule$Not(-1, PEG.rule(MD_PUNCTUATION)),
                    PEG.rule$Literal("__"),
                    PEG.rule$Not(
                        PEG.rule$Choice(
                            PEG.rule$Literal(" "),
                            PEG.rule(MD_PUNCTUATION))),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Not(PEG.rule$Literal("__")),
                        PEG.rule$Choice(
                            PEG.rule(MD_ITALIC),
                            PEG.rule$Sequence(
                                PEG.rule$Not(PEG.rule$Literal("__")),
                                PEG.rule$Any(), //PEG.rule$Class("^\n"),
                                PEG.rule$Not(PEG.rule$Literal(" __")))
                                .action($$ -> {
                                    $$.setValue($$.get(1).getValue());
                                    return $$;
                                }))),
                    PEG.rule$Literal("__"))
                    .action($$ -> {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < $$.get(3).length(); i++) {
                            sb.append($$.get(3).get(i).get(1).getValue());
                        }
                        $$.setValue("<strong>" + sb.toString() + "</strong>");
                        return $$;
                    }),
                    PEG.rule$Sequence(
                        PEG.rule$Not(PEG.rule(MD_PUNCTUATION)),
                        PEG.rule$Literal("**"),
                        PEG.rule$Optional(
                            PEG.rule$Not(
                                PEG.rule$Choice(
                                    PEG.rule$Literal(" "),
                                    PEG.rule(MD_PUNCTUATION))),
                                PEG.rule$OneOrMore(
                                    PEG.rule$Not(PEG.rule$Literal("**")),
                                    PEG.rule$Choice(
                                        PEG.rule(MD_ITALIC),
                                        PEG.rule$Sequence(
                                            PEG.rule$Not(PEG.rule$Literal("**")),
                                            PEG.rule$Any(), //PEG.rule$Class("^\n"),
                                            PEG.rule$Not(PEG.rule$Literal(" **")))
                                            .action($$ -> {
                                                $$.setValue($$.get(1).getValue());
                                                return $$;
                                            })))),
                        PEG.rule$Literal("**"))
                        .action($$ -> {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < $$.get(3).length(); i++) {
                                sb.append($$.get(3).get(i).get(1).getValue());
                            }
                            $$.setValue("<strong>" + sb.toString() + "</strong>");
                            return $$;
                        })))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

//        PEG.define(MD_CODE_SPAN,
//            PEG.rule$RegExp("(`+)([\\s\\S]+?)\\1"))
//            .action($$ -> {
//                $$.setValue(
//                    "<code>" +
//                    escapeHTML($$.get(0).get(2).getValue()
//                        .replaceAll("^\\s+", "")
//                        .replaceAll("\\s+$", "")) +
//                    "</code>");
//                return $$;
//            });

        PEG.define(MD_CODE_SPAN,
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$Literal("```"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(
                            PEG.rule$Choice(
                                // FIXME MD_CODE_BLOCKは避ける
                                PEG.rule$Literal("```"),
                                PEG.rule(MD_PRE).action($$ -> {
                                    return $$;
                                }))),
                        PEG.rule$Any()),
                    PEG.rule$RegExp("```+"))
                    .action($$ -> {
                        $$.setValue($$.get(1).pack().getValue());
                        return $$;
                    }),
                PEG.rule$Sequence(
                    PEG.rule$RegExp("``"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(
                            PEG.rule$Choice(
                                PEG.rule$Literal("``"),
                                PEG.rule(MD_BLOCK))),
                        PEG.rule$Any()),
                    PEG.rule$RegExp("``+"))
                    .action($$ -> {
                        $$.setValue($$.get(1).pack().getValue());
                        return $$;
                    }),
                PEG.rule$Sequence(
                    PEG.rule$Literal("`"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(
                            PEG.rule$Choice(
                                PEG.rule$Literal("`"),
                                PEG.rule(MD_BLOCK))),
                        PEG.rule$Any()),
                    PEG.rule$RegExp("`+"))
                    .action($$ -> {
                        $$.setValue($$.get(1).pack().getValue());
                        return $$;
                    })))
            .action($$ -> {
                String result = $$.pack().getValue();
                if (!" ".equals(result)) {
                    result = result.trim(); // FIXME
                }
                $$.setValue("<code>" + result + "</code>");
                return $$;
            });

        // URL
        PEG.define(MD_URL,
                PEG.rule$Sequence(
                    //scheme      = ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
                    PEG.rule$Class("a-zA-Z"),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("a-zA-Z0-9+-.")),
                    PEG.rule$Literal("://"),
                    //authority   = [ userinfo "@" ] host [ ":" port ]
                    PEG.rule$Optional(
                        PEG.rule$OneOrMore(
                            PEG.rule$Class("a-zA-Z0-9-")),
                        PEG.rule$Literal("@")),
                    PEG.rule$OneOrMore(
                        PEG.rule$Class("a-zA-Z0-9-")),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Literal("."),
                        PEG.rule$OneOrMore(
                            PEG.rule$Class("a-zA-Z0-9-"))),
                    // port
                    PEG.rule$Optional(
                        PEG.rule$Literal(":"),
                        PEG.rule$OneOrMore(
                            PEG.rule$Class("0-9"))),
                    // path
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Choice(
                            PEG.rule$Literal("/"),
                            PEG.rule(MD_URL_PCHARS))),
                    // query
                    PEG.rule$Optional(
                        PEG.rule$Literal("?"),
                        PEG.rule(MD_URL_PCHARS)),
                    // fragment
                    PEG.rule$Optional(
                        PEG.rule$Literal("#"),
                        PEG.rule(MD_URL_PCHARS))
                ))
            .action($$ -> {
                $$.setValue($$.getSource());
                return $$;
            });

        PEG.define(MD_URL_PCHARS,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule$Class("a-zA-Z0-9-._~!$&'*+,;=:@"),
                    PEG.rule$Sequence(
                        PEG.rule$Literal("%"),
                        PEG.rule$Class("0-9a-fA-F"),
                        PEG.rule$Class("0-9a-fA-F")))));

        PEG.define(MD_LINK_TEXT,
            PEG.rule$Literal("["),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_LINK_TEXT)
                        .action($$ -> {
                            $$.setValue("[" + $$.pack().getValue() + "]");
                            return $$;
                        }),
                    PEG.rule$Sequence(
                        PEG.rule$Literal("\\"),
                        PEG.rule$Class("\\[\\]"))
                        .action($$ -> {
                            return $$.get(1).pack();
                        }),
                    PEG.rule$Class("^\\[\\]")
                        .action($$ -> {
                            return $$;
                        }))),
            PEG.rule$Literal("]"))
            .action($$ -> {
                return $$.get(1).pack();
            });

        PEG.define(MD_LINK,
            PEG.rule$Choice(
                // [foo](<bar>)
                PEG.rule$Sequence(
                    PEG.rule(MD_LINK_TEXT),
                    PEG.rule$Literal("(<"),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^<>\n")),
                    PEG.rule$Literal(">)"))
                    .action($$ -> {
                        $$.setValue(A($$.get(0).pack().getValue(),
                            $$.get(2).pack().getValue().trim(),
                            null));
                        return $$;
                    }),
                // [foo](/bar) / [foo](/bar "title") / [foo](/bar 'title') / [foo](/bar (title))
                PEG.rule$Sequence(
                    PEG.rule(MD_LINK_TEXT),
                    PEG.rule$Choice(
                        PEG.rule(MD_LINK_PAREN),
                        PEG.rule$Sequence(
                            PEG.rule$Literal("("),
                            PEG.rule$ZeroOrMore(PEG.rule$Class("\\s")),
                            PEG.rule$ZeroOrMore(PEG.rule$Class("^\\s\\)")),
                            PEG.rule$Optional(
                                PEG.rule$OneOrMore(
                                    PEG.rule$Class("\\s")),
                                PEG.rule$Choice(
                                    PEG.rule$Sequence(
                                        PEG.rule$Literal("\""),
                                        PEG.rule$ZeroOrMore(PEG.rule$Class("^\"")),
                                        PEG.rule$Literal("\""))
                                        .action($$ -> {
                                            return $$.get(1).pack();
                                        }),
                                    PEG.rule$Sequence(
                                        PEG.rule$Literal("'"),
                                        PEG.rule$ZeroOrMore(PEG.rule$Class("^'")),
                                        PEG.rule$Literal("'"))
                                        .action($$ -> {
                                            return $$.get(1).pack();
                                        }))),
                                PEG.rule$ZeroOrMore(PEG.rule$Class("\\s")),
                                PEG.rule$Literal(")"))))
                    .action($$ -> {
                        String target = $$.get(0).pack().getValue();
                        if ($$.get(1).getType() == MD_LINK_PAREN) {
                            String url = urlEncode(filterReferences($$.get(1).pack().getValue().trim().replaceAll("\n", "")));
                            $$.setValue(A(target, url.replaceAll("^\\((.*?)\\)$", "$1"), null));
                        } else if ($$.get(1).get(3).length() > 0) {
                            String url = urlEncode(filterReferences($$.get(1).get(2).pack().getValue().trim().replaceAll("\n", "")));
                            String title = filterReferences($$.get(1).get(3).get(1).pack().getValue());
                            $$.setValue(A(target, url, title));
                        } else {
                            String url = urlEncode(filterReferences($$.get(1).get(2).pack().getValue().trim().replaceAll("\n", "")));
                            $$.setValue(A(target, url, null));
                        }
                        return $$;
                    })))
            .action($$ -> {
                return $$.pack();
            });
        
        PEG.define(MD_LINK_PAREN,
            PEG.rule$Literal("("),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal("\\"),
                        PEG.rule$Class("\\(\\)"))
                        .action($$ -> {
                            $$.setValue($$.get(1).pack().getValue());
                            return $$;
                        }),
                    PEG.rule(MD_LINK_PAREN),
                    PEG.rule$Class("^ \\)\"\'\n"))),
            PEG.rule$Literal(")"))
            .action($$ -> {
                return $$.pack();
            });

        PEG.define(MD_AUTO_LINK,
            PEG.rule$Literal("<"),
            PEG.rule$Optional(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("a-zA-Z\\+\\.\\-")),
                PEG.rule$Literal(":"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("^ <>"))),
            PEG.rule$Literal(">"))
            .action($$ -> {
                PEGNode $1 = $$.get(1);
                String url = $1.pack()
                    .getValue()
                    .replaceAll("&", "&amp;");
                $$.setValue(A(url, url, null));
                return $$;
            });
        
        PEG.define(MD_PLAIN_LINK,
            PEG.rule$Choice(
                PEG.rule$RegExp("https?://[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)+(/((?!&[a-zA-Z0-9]{1,8};)[^ \t\r\n<])*)?")
                    .action($$ -> {
                        $$.pack();
                        String url = escapeHTML($$.getValue());
                        $$.setValue(
                            "<a href=\"" + url + "\">" + url + "</a>");
                        return $$;
                    }),
                PEG.rule$RegExp("www(\\.[a-zA-Z0-9-_]+)+(/((?!&[a-zA-Z0-9]{1,8};)[^ \t\r\n<])*)?")
                    .action($$ -> {
                        $$.pack();
                        String url = escapeHTML($$.getValue());
                        $$.setValue(
                            "<a href=\"http://" + url + "\">" + url + "</a>");
                        return $$;
                    })));

        PEG.define(MD_IMAGE,
            PEG.rule$Not(PEG.rule$Literal("\\")),
            PEG.rule$Literal("!"),
            PEG.rule(MD_LINK_TEXT),
            PEG.rule$Literal("("),
            PEG.rule$ZeroOrMore(PEG.rule$Class("\\s")),
            PEG.rule$ZeroOrMore(PEG.rule$Class("^\\s\\)")),
            PEG.rule$Optional(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("\\s")).action($$ -> {
                        $$.setValue("");
                        return $$;
                    }),
                    PEG.rule$Choice(
                        PEG.rule$Sequence(
                            PEG.rule$Literal("\""),
                            PEG.rule$ZeroOrMore(PEG.rule$Class("^\"")),
                            PEG.rule$Literal("\""))
                            .action($$ -> {
                                return $$.get(1).pack();
                            }),
                        PEG.rule$Sequence(
                            PEG.rule$Literal("'"),
                            PEG.rule$ZeroOrMore(PEG.rule$Class("^'")),
                            PEG.rule$Literal("'"))
                            .action($$ -> {
                                return $$.get(1).pack();
                            }),
                        PEG.rule$Sequence(
                            PEG.rule$Literal("("),
                            PEG.rule$ZeroOrMore(PEG.rule$Class("^)")),
                            PEG.rule$Literal(")"))
                            .action($$ -> {
                                return $$.get(1).pack();
                            })
                        )),
                    PEG.rule$ZeroOrMore(PEG.rule$Class("\\s")),
                    PEG.rule$Literal(")"))
            .action($$ -> {
                String alt = $$.get(2).pack().getValue();
                String url = $$.get(5).pack().getValue();
                String title = escapeHTML($$.get(6).pack().getValue());
                if (!"".equals(title)) {
                    title = " title=\"" + title + "\"";
                }
                $$.setValue("<img src=\"" + url + "\" alt=\"" + alt + "\"" + title + " />");
                return $$;
            });

        PEG.define(MD_ESCAPED,
                PEG.rule$Literal("\\"),
                PEG.rule(MD_PUNCTUATION))
            .action($$ -> {
                PEGNode $1 = $$.get(1);
                switch ($1.getValue()) {
                case "\"": $$.setValue("&quot;"); break;
                case "<": $$.setValue("&lt;"); break;
                case ">": $$.setValue("&gt;"); break;
                case "&": $$.setValue("&amp;"); break;
                default: $$.setValue($1.getValue()); break;
                }
                return $$;
            });

        PEG.define(MD_SPECIAL,
                PEG.rule$Choice(
                    PEG.rule$Literal("\"")
                        .action($$ -> { $$.setValue("&quot;"); return $$; }),
                    PEG.rule$Literal("<")
                        .action($$ -> { $$.setValue("&lt;"); return $$; }),
                    PEG.rule$Literal(">")
                        .action($$ -> { $$.setValue("&gt;"); return $$; }),
                    PEG.rule$Literal("&")
                        .action($$ -> { $$.setValue("&amp;"); return $$; })))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

        PEG.define(MD_NOT_PUNCTUATION,
            PEG.rule$Class("^!\"#$%&'\\(\\)\\*\\+\\,\\-./:;<=>?@\\[\\\\\\]^_`{|}~"))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

        PEG.define(MD_PUNCTUATION,
            PEG.rule$Class(PUNCTUATION))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

        PEG.define(MD_NUMERIC_REF,
            PEG.rule$Literal("&#"),
            PEG.rule$Choice(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("0-9"))
                    .action($$ -> {
                        int c = Integer.parseInt($$.pack().getValue(), 10);
                        if (c == 0) {
                            $$.setValue(String.valueOf((char)0xFFFD));
                        } else if (c < 0xFFFF) {
                            $$.setValue(String.valueOf((char)c));
                        } else {
                            $$.setValue("&#" + $$.pack().getValue() + ";");
                        }
                        return $$;
                    }),
                PEG.rule$Sequence(
                    PEG.rule$Class("xX"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Class("0-9a-fA-F")))
                    .action($$ -> {
                        int c = Integer.parseInt($$.get(1).pack().getValue(), 16);
                        if (c == 0) {
                            $$.setValue(String.valueOf((char)0xFFFD));
                        } else if (c < 0xFFFF) {
                            $$.setValue(String.valueOf((char)c));
                        } else {
                            $$.setValue("&#" + $$.pack().getValue() + ";");
                        }
                        return $$;
                    })),
            PEG.rule$Literal(";"))
            .action($$ -> {
                $$.setValue(escapeHTML($$.get(1).getValue()));
                return $$;
            });

        // 文字実体参照
        PEG.define(MD_ENTITY_REF,
            PEG.rule$Literal("&"),
            PEG.rule$OneOrMore(
                PEG.rule$Class("a-zA-Z0-9")),
            PEG.rule$Literal(";"))
            .action($$ -> {
                String name = $$.get(1).pack().getValue();
                if ("amp".equals(name)) {
                    $$.setValue("&amp;");
                } else {
                    String c = EntityReference.get(name);
                    if (c != null) {
                        $$.setValue(c);
                    } else {
                        $$.setValue("&amp;" + name + $$.get(2).getValue());
                    }
                }
                return $$;
            });
        
        PEG.define(MD_CHAR,
            PEG.rule$Class("^\r\n"))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

        PEG.define(MD_EOL_OR_EOF,
            PEG.rule$Choice(
                PEG.rule(MD_EOL),
                PEG.rule(MD_EOF)
                    .action($$ -> {
                        $$.setValue("");
                        return $$;
                    })))
            .action($$ -> {
                //$$.setValue($$.getSource());
                return $$.pack();
            });

        PEG.define(MD_EOL,
            PEG.rule$Optional(PEG.rule$Literal("\r")),
            PEG.rule$Literal("\n"))
            .action($$ -> {
                $$.setValue("\n");
                return $$;
            });
        PEG.define(MD_EOF,
            PEG.rule$Not(PEG.rule$Any()));

        PEG.define(MD_PRESPACES,
            PEG.rule$Optional(
                PEG.rule$Choice(
                    PEG.rule$Literal("   "),
                    PEG.rule$Literal("  "),
                    PEG.rule$Literal(" "))),
            PEG.rule$Not(
                PEG.rule$Class(" \t")));

        PEG.define(MD_WS,
            PEG.rule$Class(" \t\t\n"));

        PEG.define(MD_EMPTY,
                PEG.rule$OneOrMore(
                    PEG.rule$Class(" \t")),
                PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                $$.setValue("");
                return $$;
            });

        PEG.define(MD_HTML,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(HTML_OPEN_TAG),
                    PEG.rule(HTML_CLOSING_TAG),
                    PEG.rule(HTML_COMMENT),
                    PEG.rule(HTML_INSTRUCTION),
                    PEG.rule(HTML_DECLARATION),
                    PEG.rule(HTML_CDATA))))
            .action($$ -> {
                $$.setValue($$.getSource());
                return $$;
            });

        PEG.define(HTML_OPEN_TAG,
            PEG.rule$Literal("<"),
            PEG.rule(HTML_TAG_NAME),
            PEG.rule$ZeroOrMore(
                PEG.rule(HTML_ATTR)),
            PEG.rule$ZeroOrMore(
                PEG.rule(MD_WS)),
            PEG.rule$Optional(
                PEG.rule$Literal("/")),
            PEG.rule$Literal(">"));

        PEG.define(HTML_CLOSING_TAG,
            PEG.rule$Literal("</"),
            PEG.rule(HTML_TAG_NAME),
            PEG.rule$ZeroOrMore(
                PEG.rule(MD_WS)),
            PEG.rule$Literal(">"));

        PEG.define(HTML_TAG_NAME,
            PEG.rule$Class("a-zA-Z"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z0-9\\-")));

        PEG.define(HTML_ATTR,
            PEG.rule$OneOrMore(PEG.rule(MD_WS)),
            PEG.rule(HTML_ATTR_NAME),
            PEG.rule$ZeroOrMore(
                PEG.rule$ZeroOrMore(PEG.rule(MD_WS)),
                PEG.rule$Literal("="),
                PEG.rule$ZeroOrMore(PEG.rule(MD_WS)),
                PEG.rule(HTML_ATTR_VALUE)));

        PEG.define(HTML_ATTR_NAME,
            PEG.rule$Class("a-zA-Z_:"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z0-9_\\.:\\-")));

        PEG.define(HTML_ATTR_VALUE,
            PEG.rule$Choice(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("^ \"'=<>`\n")),
                PEG.rule$Sequence(
                    PEG.rule$Literal("\""),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Not(PEG.rule(MD_BLOCK)),
                        PEG.rule$Class("^\"")),
                    PEG.rule$Literal("\"")),
                PEG.rule$Sequence(
                    PEG.rule$Literal("'"),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Not(PEG.rule(MD_BLOCK)),
                        PEG.rule$Class("^'")),
                    PEG.rule$Literal("'"))));

        PEG.define(HTML_COMMENT,
            PEG.rule$Literal("<!--"),
            PEG.rule$Not(PEG.rule$Literal(">")),
            PEG.rule$Not(PEG.rule$Literal("->")),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule$Literal("--")),
                PEG.rule$Any()),
            //TODO PEG.rule$Not(PEG.rule$Literal("-")),
            PEG.rule$Literal("-->"));

        PEG.define(HTML_INSTRUCTION,
            PEG.rule$Literal("<?"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule$Literal("?>")),
                PEG.rule$Any()),
            PEG.rule$Literal("?>"));

        PEG.define(HTML_DECLARATION,
            PEG.rule$Literal("<!"),
            PEG.rule$OneOrMore(
                PEG.rule$Class("A-Z")),
            PEG.rule$OneOrMore(
                PEG.rule(MD_WS)),
            PEG.rule$OneOrMore(
                PEG.rule$Not(PEG.rule$Literal(">")),
                PEG.rule$Any()),
            PEG.rule$Literal(">"));

        PEG.define(HTML_CDATA,
            PEG.rule$Literal("<![CDATA"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule$Literal("]]>")),
                PEG.rule$Any()),
            PEG.rule$Literal("]]>"));
        
        PEG.define(MD_AST,
            PEG.rule$Literal("*"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule$Literal("*")),
                PEG.rule$Any()),
            PEG.rule$Literal("*"));
        
        PEG.define(MD_HTML_BLOCK,
            PEG.rule(MD_PRESPACES),
            PEG.rule$Choice(
                PEG.rule$Choice(
                    createHtmlBlock("script"),
                    createHtmlBlock("pre"),
                    createHtmlBlock("style")),
                PEG.rule$Sequence(
                    PEG.rule$Choice(
                        PEG.rule(HTML_COMMENT),
                        PEG.rule(HTML_INSTRUCTION),
                        PEG.rule(HTML_DECLARATION),
                        PEG.rule(HTML_CDATA)),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^\n")),
                    PEG.rule(MD_EOL_OR_EOF))
                    .action($$ -> {
                        String comment = $$.get(0).pack().getValue();
                        String after = $$.get(1).pack().getValue();
                        if(comment.contains("\n")) {
                            PEGContext ctx = $$.getContext().createContext(after);
                            try {
                                $$.setValue(comment +
                                    PEG.rule(MARKDOWN).accept(ctx).pack().getValue());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return $$;
                    }),
                PEG.rule$Sequence(
                    PEG.rule$RegExp("(?i)</?(address|article|aside|base|basefont|blockquote|body|" +
                        "caption|center|col|colgroup|dd|details|dialog|dir|div|dl|dt|fieldset|" +
                        "figcaption|figure|footer|form|frame|frameset|h1|h2|h3|h4|h5|h6|head|" +
                        "header|hr|html|iframe|legend|li|link|main|menu|menuitem|nav|noframes|ol|" +
                        "optgroup|option|p|param|section|source|summary|table|tbody|td|tfoot|" +
                        "th|thead|title|tr|track|ul)( |\n|/?>)"),
                    PEG.rule$RegExp("[^\n]*"),
                    PEG.rule(MD_EOL_OR_EOF),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$RegExp(".+"),
                        PEG.rule(MD_EOL_OR_EOF))),
                PEG.rule$Sequence(
                    //PEG.rule$RegExp("<\\/?([^ >]+)( [^>]+)?>([\\s\\S]*?</\\1>)?"), // ここ分ける？
                    PEG.rule$RegExp("<\\/?([a-zA-Z0-9\\-\\_]+)( [^>]+)?>"),
                    PEG.rule(MD_EOL_OR_EOF),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$RegExp("[^\n]+"),
                        PEG.rule(MD_EOL_OR_EOF)),
                    PEG.rule(MD_EOL_OR_EOF)).action($$ -> {
                        StringBuffer sb = new StringBuffer($$.get(0).getValue());
                        sb.append($$.get(1).getValue());
                        for (int i = 0; i < $$.get(2).length(); i++) {
                            PEGNode $i = $$.get(2).get(i);
                            sb.append($i.pack().getValue());
                        }
                        $$.setValue(sb.toString());
                        return $$;
                    }),
                PEG.rule$Sequence(
                    PEG.rule$RegExp("</?([[a-zA-Z0-9\\-\\_]+]+)( [^>]+)?>[ \t]*"),
                    PEG.rule(MD_EOL_OR_EOF))
                ))
            .action($$ -> {
                return $$.pack();
            });

        // 参照の参照
        PEG.define(MD_LINK_REFREF,
            PEG.rule$RegExp("\\[([^\\]]+)\\]"),
            PEG.rule$RegExp("\\[([^\\]]*)\\]"))
            .action($$ -> {
                String ref = $$.get(1).get(1).getValue();
                String linkText = $$.get(0).get(1).getValue();
                if ("".equals(ref)) {
                    ref = linkText;
                }
                String link = $$.getContext().getAttr(ref.toLowerCase());
                // TODO 参照先がまだ定義されてない場合
                if (link != null) {
                    link = link.replaceAll(">([^>]*)<", ">" + inline($$.getContext(), linkText) + "<");
                    $$.setValue(link);
                } else {
                    $$.setValue("${{" + ref + "}}");
                }
                return $$;
            });

        // 参照リンク
        PEG.define(MD_LINK_REF,
            PEG.rule$Literal("["),
            PEG.rule$RegExp("(\\\\]|[^\\]])*").action($$ -> {
                $$.setValue($$.getValue().replaceAll("\\\\]", "]"));
                return $$;
            }),
            PEG.rule$Literal("]"),
            PEG.rule$Not(
                PEG.rule$Class("\\[\\(:"))
            )
            .action($$ -> {
                // FIXME 何か他に良い手はないか？
                $$.setValue("${{" + $$.get(1).pack().getValue() + "}}");
                return $$;
            });

        // 参照リンク定義
        PEG.define(MD_LINK_DEF,
            PEG.rule(MD_PRESPACES),
            PEG.rule$Literal("["),
            PEG.rule$RegExp("(\\\\]|[^\\]])+").action($$ -> {
                $$.setValue($$.getValue().replaceAll("\\\\]", "]"));
                return $$;
            }),
            PEG.rule$Literal("]:"),
            PEG.rule$RegExp("( *\n *| *)"),
            PEG.rule$Choice(
                PEG.rule$RegExp("<([^>\n]*)>").action($$ -> {
                    $$.setValue($$.get(1).getValue());
                    return $$;
                }),
                PEG.rule$RegExp("[^ \n]+")),
            PEG.rule$Optional(
                PEG.rule$RegExp("( *\n *| *)"),
                PEG.rule$Choice(
                    PEG.rule$RegExp("\"(((?!\n\n)[^\"])*)\"").action($$ -> {
                        $$.setValue($$.get(1).getValue());
                        return $$;
                    }),
                    PEG.rule$RegExp("'(((?!\n\n)[^'])*)'")).action($$ -> {
                        $$.setValue($$.get(1).getValue());
                        return $$;
                    }),
                PEG.rule$RegExp(" *")),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                String title = null;
                if ($$.get(6).length() > 0) {
                    title = $$.get(6).get(1).getValue();
                }
                String result = A(
                    $$.get(2).pack().getValue(),
                    $$.get(5).pack().getValue(),
                    title);
                String linkText = $$.get(2).pack().getValue()
                    .replaceAll("\n", "")
                    .replaceAll(" +", " ")
                    .toLowerCase();
                if ($$.getContext().getAttr(linkText) == null) {
                    $$.getContext().setAttr(linkText, result);
                }
                $$.setValue("");
                return $$; 
            });
    }
    
    
    private static String H1(String text) {
        return "<h1>" + text + "</h1>";
    }

    private static String H2(String text) {
        return "<h2>" + text + "</h2>";
    }

    private static String H3(String text) {
        return "<h3>" + text + "</h3>";
    }

    private static String H4(String text) {
        return "<h4>" + text + "</h4>";
    }

    private static String H5(String text) {
        return "<h5>" + text + "</h5>";
    }

    private static String H6(String text) {
        return "<h6>" + text + "</h6>";
    }

    private static String P(String text) {
        return "<p>" + text + "</p>";
    }

    private static String A(String linkText, String url, String title) {
        if (title != null) {
            title = " title=\"" + escapeHTML(title) + "\"";
        } else {
            title = "";
        }
        return "<a href=\"" + urlEncode(url) + "\"" + title + ">" + linkText + "</a>";
    }
    
    private static String IMG(String alt, String url, String title) {
        return null;
    }
    
    private static String EM(String text) {
        return "<em>" + text + "</em>";
    }

    private static String STRONG(String text) {
        return "<strong>" + text + "</strong>";
    }

    private static String unescape(String text) {
        return text.replaceAll("\\\\(.)", "$1");
    }

    private static String escapeHTML(String text) {
        return text
            .replaceAll("&", "&amp;")
            .replaceAll("\"", "&quot;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }
    
    private static Pattern urlChar = Pattern.compile("[^:/\\?#\\[\\]@a-zA-Z0-9\\-\\._~%\\(\\)=]");
    private static String urlEncode(String url) {
        StringBuffer sb = new StringBuffer();
        Matcher m = urlChar.matcher(url);
        int start = 0;
        while (m.find(start)) {
            if (start < m.start()) {
                sb.append(url.substring(start, m.start()));
            }
            for (int i = 0; i < m.group().length(); i++) {
                int c = m.group().charAt(i);
                sb.append("%" + String.format("%02X", c));
            }
            start = m.end();
        }
        if (start < url.length()) {
            sb.append(url.substring(start));
        }
        return sb.toString();
    }

    private static Rule createCodeBlock(String fence) {
        String fc = fence.substring(0, 1);
        return PEG.rule$Sequence(
            PEG.rule(MD_PRESPACES),
            PEG.rule$Literal(fence),
            PEG.rule$ZeroOrMore(
                PEG.rule$Literal(fc)),
            PEG.rule$Optional(
                PEG.rule$RegExp("[ \t]*"),
                PEG.rule$RegExp("[^`~ \t\n]+"),
                PEG.rule$RegExp("(~|[^" + fc + "\n])*")),
            PEG.rule(MD_EOL_OR_EOF),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule$Sequence(
                        PEG.rule$RegExp(" {0,3}"),
                        PEG.rule$Literal(fence))),
                PEG.rule$RegExp("[^\n]*"),
                PEG.rule(MD_EOL)),
            PEG.rule$Optional(
                PEG.rule$RegExp(" {0,3}"),
                PEG.rule$Literal(fence)),
            PEG.rule$ZeroOrMore(
                PEG.rule$Literal(fc)))
            .action($$ -> {
                int indent = $$.get(0).pack().getValue().length();
                String lang = $$.get(3).length() > 1
                    ? $$.get(3).get(1).pack().getValue()
                    : "";
                if (!"".equals(lang)) {
                    lang = " class=\"language-" + unescape(lang) + "\"";
                }
                StringBuffer code = new StringBuffer();
                for (int i = 0; i < $$.get(5).length(); i++) {
                    String line = $$.get(5).get(i).pack()
                        .getValue();
                    if ("\n".equals(line)) {
                        continue;
                    }
                    line = line.replaceAll("^ {0," + indent + "}", "");
                    code.append(line);
                }
                $$.setValue("<pre><code" + lang + ">"+ escapeHTML(code.toString()) + "</code></pre>");
                return $$;
            });
    }

    /**
     * リストアイテムの規則を生成する
     */
    private static Rule createListItem(String prefix) {
        final StringBuffer indent = new StringBuffer();
        final StringBuffer marker = new StringBuffer();
        for (int i = 0; i < prefix.length(); i++) {
            indent.append(" ");
            if (prefix.charAt(i) != ' ') {
                marker.append(prefix.charAt(i));
            }
        }
        // インデントTABは空白４文字と等価とする
        Rule indentRule = PEG.rule$Choice(
            PEG.rule$Literal(indent.toString()))
            .action($$ -> {
                $$.setValue($$.getValue().replaceAll("\t", "    "));
                return $$;
            });
        if (indent.length() <= 4) {
            indentRule.add(
                PEG.rule$OneOrMore(
                    PEG.rule$Literal("\t"))
                    .action($$ -> {
                        $$.pack();
                        $$.setValue($$.getValue().replaceAll("\t", "    "));
                        return $$;
                    }));
        } else if (indent.length() > 4) {
            for (int i = 0; i < indent.length() - 4; i++) {
                String pre = indent.substring(0, i);
                String post = indent.substring(i, indent.length() - 4);
                indentRule.add(
                    PEG.rule$Literal(pre + "\t" + post));
            }
        }
        if (indent.length() == 8) {
            indentRule.add(
                PEG.rule$Literal("\t\t"));
        }
        
        return PEG.rule$Sequence(
            PEG.rule$Literal(prefix),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("^\n"))
                    .action($$ -> {
                        return $$.pack();
                    }),
            PEG.rule(MD_EOL_OR_EOF),
            // 空行を挟まない継続行
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule$Choice(
                        PEG.rule(MD_HR),
                        PEG.rule$Sequence(
                            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                            PEG.rule$Literal(marker.toString())))),
                PEG.rule$OneOrMore(
                    PEG.rule$Class("^\n"))
                    .action($$ -> {
                        $$.setValue(
                            $$.pack().getValue()
                                .replaceAll("(?![^ \t])\t", "    ")
                                .replaceAll("^" + indent.toString(), ""));
                        return $$;
                    }),
                PEG.rule(MD_EOL_OR_EOF))
                .action($$ -> {
                    return $$.pack();
                }),
            // 空行を挟むかもしれない継続行
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        indentRule,
                        PEG.rule$OneOrMore(
                            PEG.rule$Class("^\n")),
                        PEG.rule(MD_EOL_OR_EOF))
                        .action($$ -> {
                            String sp = $$.get(0).getValue()
                                .replaceAll("(?![^ \t])\t", "    ")
                                .substring(indent.length());
                            $$.setValue(
                                //$$.get(0).getValue().substring(indent.length()) +
                                sp +
                                $$.get(1).pack().getValue() +
                                $$.get(2).pack().getValue());
                            return $$;
                        }),
                    PEG.rule(MD_EOL) // 空行
                        .action($$ -> {
                            $$.setType(MD_BLANK_LINE);
                            return $$;
                        })))
                .action($$ -> {
                    return $$.pack();
                }))
            .action($$ -> {
                String first = $$.get(0).pack().getValue() +
                    $$.get(1).pack().getValue();
                first = first.substring(first.indexOf(marker.toString()) + 1)
                    .replaceAll("(?=\\s)\t", "   ");
                StringBuffer sb = new StringBuffer()
                    .append(first)
                    .append($$.get(2).getValue())
                    .append($$.get(3).getValue())
                    .append($$.get(4).getValue());
                String s = sb.toString()
                    .replaceAll("[ \t\n]*$", "");
                PEGContext ctx = $$.getContext().createContext(s);
                try {
                    if ("".equals(s)) {
                        $$.setValue("");
                    } else {
                        PEGNode result = PEG.rule(MARKDOWN).accept(ctx);
                        $$.setValue(result.pack().getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return $$;
            });
    }
    
    private static boolean isLoose(PEGNode node) {
        if (node.getType() == MD_BLANK_LINE) {
            return true;
        } else {
            for (int i = 0; i < node.length(); i++) {
                if (isLoose(node.get(i))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * em / strong
     */
    private static Rule createModification(String marker) {
        Rule start = PEG.rule$Choice(
            PEG.rule$Sequence(
                PEG.rule$Not(PEG.rule$Class("^" + PUNCTUATION)),
                PEG.rule$Literal(marker),
                PEG.rule$And(PEG.rule$Class(PUNCTUATION))),
            PEG.rule$Sequence(
                PEG.rule$Not(-1, PEG.rule$Literal("\\")),
                PEG.rule$Literal(marker),
                PEG.rule$Not(PEG.rule$Literal(" ")))
                .action($$ -> {
                    $$.setValue("");
                    return $$;
                }));
        
        Rule end = PEG.rule$Choice(
            PEG.rule$Sequence(
                PEG.rule$Class(PUNCTUATION),
                PEG.rule$Literal(marker),
                PEG.rule$Not(PEG.rule$Class("^" + PUNCTUATION)))
                .action($$ -> {
                    $$.setValue(escapeHTML($$.get(0).getValue()));
                    return $$;
                }),
            PEG.rule$Sequence(
                PEG.rule$Not(PEG.rule$Literal(" ")),
                PEG.rule$Literal(marker))
                .action($$ -> {
                    $$.setValue("");
                    return $$;
                }));

        return PEG.rule$Sequence(
            start,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_STRONG),
                    PEG.rule(MD_EMPHASIS),
                    PEG.rule$Sequence(
                        PEG.rule$OneOrMore(
                            PEG.rule$Not(PEG.rule$Literal(marker)),
                            PEG.rule$Class("^ ")),
                        PEG.rule$ZeroOrMore(
                            PEG.rule$OneOrMore(
                                PEG.rule$Class(" ")),
                            PEG.rule$OneOrMore(
                                PEG.rule$Not(PEG.rule$Literal(marker)),
                                PEG.rule$Class("^ ")))))),
            end)
            .action($$ -> {
                if (marker.length() == 1) {
                    $$.setValue(EM($$.get(1).pack().getValue()));
                } else {
                    $$.setValue(STRONG($$.get(1).pack().getValue()));
                }
                return $$;
            });
    }

    private static Rule createNotModification(String marker) {
        return PEG.rule$Choice(
            PEG.rule$Sequence(
                PEG.rule$Class("^" + PUNCTUATION),
                createModification(marker),
                PEG.rule$Class("^" + PUNCTUATION)),
            PEG.rule$Sequence(
                PEG.rule$Class("^" + PUNCTUATION),
                createModification(marker)),
            PEG.rule$Sequence(
                createModification(marker),
                PEG.rule$Class("^" + PUNCTUATION)))
            .action($$ -> {
                $$.setValue(escapeHTML($$.getSource()));
                return $$;
            });
    }

    private static Rule createHtmlBlock(String tagName) {
        return PEG.rule$Choice(
            PEG.rule$Sequence(
                PEG.rule$Literal("<" + tagName, true),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Not(PEG.rule$Literal("</" + tagName + ">", true)),
                    PEG.rule$Class("^\n")),
                PEG.rule$Choice(
                    PEG.rule$Literal("</" + tagName + ">", true),
                    PEG.rule(MD_EOF))),
            PEG.rule$Sequence(
                PEG.rule$Literal("<" + tagName, true),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("^\n")),
                PEG.rule(MD_EOL),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Not(PEG.rule$Literal("</" + tagName + ">", true)),
                    PEG.rule$Any()),
                PEG.rule$Choice(
                    PEG.rule$Literal("</" + tagName + ">", true),
                    PEG.rule(MD_EOF))));
    }

    private static String spaces(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public String parse(String text) throws MarkdownException {
        try {
            PEGContext ctx = PEG.createContext(text);
            //LOG.trace(PEG.rule(MARKDOWN).toString());
            PEGNode node = PEG.rule(MARKDOWN).accept(ctx);
            //LOG.debug(node.toString());
            //LOG.debug(node.getValue());
            if (ctx.available() > 0) {
                throw new MarkdownException(ctx.subString(ctx.position()));
            }

            if (LOG.isTraceEnabled()) {
                AtomicInteger total = new AtomicInteger();
                AtomicInteger success = new AtomicInteger();
                ctx.getStatistics()
                    .entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        int total1 = e1.getValue().getSuccess() + e1.getValue().getFailure();
                        Float r1 = 100.0f * e1.getValue().getSuccess() / total1;
                        int total2 = e2.getValue().getSuccess() + e2.getValue().getFailure();
                        Float r2 = 100.0f * e2.getValue().getSuccess() / total2;
                        return r2.compareTo(r1);
                    })
                    .forEach(e -> {
                        total.addAndGet(e.getValue().getSuccess());
                        total.addAndGet(e.getValue().getFailure());
                        success.addAndGet(e.getValue().getSuccess());
                        LOG.debug(e.getKey() + ": {0} / {1}",
                            e.getValue().getSuccess(),
                            (e.getValue().getSuccess() + e.getValue().getFailure()));
                    });
                LOG.debug("Total: {0} / {1}", success, total);
            }
            
            return applyLinks(ctx, node.pack().getValue());
        } catch (PEGException e) {
            throw new MarkdownException(e);
        }
    }

    private static void findLinks(Map<String,String> map, PEGNode node) {
        String type = node.getType().name();
        if ("MD_LINK_DEF".equals(type)) {
            String linkText = node.getValue()
                .replaceAll("<a [^>]*>([^<]*)</a>", "$1");
            map.put(linkText.toLowerCase(), node.getValue());
            node.setValue("");
        }
        for (int i = 0; i < node.length(); i++) {
            PEGNode child = node.get(i);
            findLinks(map, child);
        }
    }

    private static String applyLinks(PEGContext ctx, String source) {
        Pattern p = Pattern.compile("\\$\\{\\{(.*?)}}");
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer();
        int start = 0;
        while (m.find(start)) {
            if (start < m.start()) {
                sb.append(source.substring(start, m.start()));
            }
            String linkText = m.group(1);
            String val = ctx.getAttr(linkText.toLowerCase());
            if (val != null) {
                val = val.replaceAll(">(.*?)<", ">" + inline(ctx, linkText) + "<");
                sb.append(val);
            } else {
                sb.append("[" + linkText + "]");
            }
            start = m.end();
        }
        if (start < source.length()) {
            sb.append(source.substring(start));
        }
        return sb.toString();
    }
    
    private static String inline(PEGContext ctx, String text) {
        try {
            PEGContext ctxx = ctx.createContext(text);
            PEGNode node = PEG.rule(MD_INLINE).accept(ctxx);
            return node.pack().getValue();
        } catch (PEGException e) {
            e.printStackTrace();
        }
        return text;
    }

    private static String filterReferences(String text) {
        StringBuffer decoded = new StringBuffer();
        Pattern pattern = Pattern.compile(
            "(" +
            "(&#(x|X)([a-fA-F0-9]+);)|" +
            "(&#([0-9]+);)|" +
            "(&([a-zA-F0-9]+);)" +
            ")");
        Matcher matcher = pattern.matcher(text);
        int start = 0;
        while (matcher.find(start)) {
            if (start < matcher.start()) {
                decoded.append(text.substring(start, matcher.start()));
            }
            if (matcher.group(2) != null) {
                int c = Integer.valueOf(matcher.group(4), 16);
                decoded.append((char)c);
            } else if (matcher.group(5) != null) {
                int c = Integer.valueOf(matcher.group(6), 10);
                decoded.append((char)c);
            } else if (matcher.group(7) != null) {
                String name = matcher.group(8);
                if ("amp".equals(name)) {
                    decoded.append("&amp;");
                } else {
                    String c = EntityReference.get(name);
                    if (c != null) {
                        decoded.append(c);
                    } else {
                        decoded.append(matcher.group());
                    }
                }
            }
            start = matcher.end();
        }
        
        if (start < text.length()) {
            decoded.append(text.substring(start));
        }

        return decoded.toString();
    }

    public enum MarkdownRules implements RuleTypes {
        MARKDOWN, MD_STX_H1, MD_STX_H2, MD_HEAD, MD_H1, MD_H2, MD_H3, MD_H4, MD_H5, MD_H6,
        MD_PRE, MD_CODE_BLOCK, MD_CODE_SPAN, MD_QUOTE, MD_LIST, MD_PARA, MD_LINE, MD_INLINE, MD_ITALIC, MD_BOLD,
        MD_HR, MD_LINK, MD_AUTO_LINK, MD_IMAGE, MD_URL, MD_URL_PCHARS, MD_ESCAPED, MD_SPECIAL,
        MD_PUNCTUATION, MD_NOT_PUNCTUATION, MD_CHAR, MD_WS, MD_EMPTY, MD_EOL, MD_EOF, MD_EOL_OR_EOF, MD_PRESPACES,
        MD_HTML, HTML_TAG_NAME, HTML_ATTR, HTML_ATTR_NAME, HTML_ATTR_VALUE,
        HTML_OPEN_TAG, HTML_CLOSING_TAG, HTML_COMMENT, HTML_INSTRUCTION, HTML_DECLARATION,
        HTML_CDATA,
        MD_AST, MD_ASTAST, MD_BAR, MD_BARBAR, MD_AUTO_LINK_INNER, MD_LINK_TEXT, MD_LINK_PAREN,
        MD_LIST_AST, MD_LIST_PLUS, MD_LIST_BAR, MD_TABLE, MD_BLANK_LINE,
        MD_NUMERIC_REF, MD_ENTITY_REF, MD_STX_H1_UNDER, MD_STX_H2_UNDER,
        MD_EMPHASIS, MD_STRONG, MD_EMPH_AST, MD_EMPHASIS_HYPHHEN, MD_STRONG_AST, MD_STRONG_HYPHHEN,
        MD_HTML_BLOCK, MD_LINK_REF, MD_LINK_REFREF, MD_LINK_DEF,
        MD_OLIST, MD_OLIST_ITEM, MD_PLAIN_LINK, MD_BLOCK, MD_QUOTE_BLOCK
        ;
    }
    
    public static class MarkdownException extends Exception {
        private static final long serialVersionUID = -4509817915640541204L;

        public MarkdownException(String message) {
            super(message);
        }
        public MarkdownException(Exception e) {
            super(e);
        }
    }
    
}
