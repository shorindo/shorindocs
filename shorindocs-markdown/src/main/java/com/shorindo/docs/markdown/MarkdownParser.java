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

import java.util.HashMap;
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

/**
 * 
 */
public class MarkdownParser {
    private static ActionLogger LOG = ActionLogger.getLogger(MarkdownParser.class);
    private static PEGCombinator PEG = new PEGCombinator();
    private static Map<String,Character> REFERENCE_MAP = new HashMap<>();
    static {

        PEG.define(MARKDOWN,
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_HR),
                    PEG.rule(MD_LIST),
                    PEG.rule(MD_HEAD),
                    PEG.rule(MD_STX_H1),
                    PEG.rule(MD_STX_H2),
                    PEG.rule(MD_PRE),
                    PEG.rule(MD_CODE_BLOCK),
                    PEG.rule(MD_QUOTE),
                    PEG.rule(MD_HTML),
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
        // 見出し１
        // ========
        PEG.define(MD_STX_H1,
            PEG.rule(MD_PRESPACES),
            PEG.rule(MD_INLINE),
            PEG.rule(MD_EOL),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(
                    PEG.rule(MD_STX_H1_UNDER)),
                PEG.rule$OneOrMore(
                    PEG.rule(MD_INLINE)),
                PEG.rule(MD_EOL)),
            PEG.rule(MD_STX_H1_UNDER))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().trim());
                if ($$.get(3).length() > 0) {
                    sb.append("\n");
                    sb.append($$.get(3).pack().getValue());
                }
                $$.setValue("<h1>" + sb.toString().trim() + "</h1>");
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
                $$.setValue("<h2>" + sb.toString().trim() + "</h2>");
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
                    PEG.rule$OneOrMore(PEG.rule$Class("\\s")),
                    PEG.rule(MD_INLINE),
                    PEG.rule(MD_EOL_OR_EOF))
                    .action($$ -> {
                        return $$.get(1);
                    }),
                PEG.rule(MD_EOL)))
            .action($$ -> {
                int depth = $$.get(1).getValue().length();
                PEGNode $2 = $$.get(2);
                if (depth <= 6) {
                    String title = $2.getValue()
                        .trim()
                        .replaceAll(" +((?!\\\\)#)+$", "")
                        .replaceAll("\\\\#", "#");
                    $$.setValue(
                        "<h" + depth + ">" +
                        title +
                        "</h" + depth + ">");
                } else {
                    $$.setValue($$.getSource());
                }
                return $$;
            });
        // 整形済
        PEG.define(MD_PRE,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule$Literal("    "),
                    PEG.rule$Literal("\t"),
                    PEG.rule$Literal(" \t"),
                    PEG.rule$Literal("  \t"),
                    PEG.rule$Literal("   \t")),
                PEG.rule(MD_INLINE),
                PEG.rule(MD_EOL_OR_EOF)))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    PEGNode $001 = $0.get(i).get(1);
                    sb.append($001.getValue());
                    sb.append("\n");
                }
                $$.clear();
                $$.setValue("<pre><code>" + sb.toString() + "</code></pre>");
                return $$;
            });
        // コード
        PEG.define(MD_CODE_BLOCK,
            PEG.rule$Choice(
                createCodeBlock("```"),
                createCodeBlock("~~~")))
            .action($$ -> {
                return $$.pack();
            });
        // コンテナ
//        PEG.define(MD_CONTAINER,
//            PEG.rule$ZeroOrMore(
//                PEG.rule$Choice(
//                    PEG.rule(MD_HR),
//                    PEG.rule(MD_LIST),
//                    PEG.rule(MD_HEAD),
//                    PEG.rule(MD_STX_H1),
//                    PEG.rule(MD_STX_H2),
//                    PEG.rule(MD_PRE),
//                    PEG.rule(MD_CODE_BLOCK),
//                    PEG.rule(MD_QUOTE),
//                    PEG.rule(MD_HTML),
//                    PEG.rule(MD_PARA),
//                    PEG.rule(MD_EMPTY),
//                    PEG.rule(MD_EOL)
//                        .action($$ -> {
//                            $$.setValue("");
//                            return $$;
//                        }))),
//            PEG.rule(MD_EOF));
        // 引用
        PEG.define(MD_QUOTE,
            PEG.rule$Sequence(
                PEG.rule(MD_PRESPACES),
                PEG.rule$Literal(">"),
                PEG.rule$ZeroOrMore(
                    PEG.rule(MD_CHAR)),
                PEG.rule(MD_EOL_OR_EOF))
                .action($$ -> {
                    String line = $$.get(2).pack().getValue() +
                        $$.get(3).pack().getValue();
                    $$.setValue(line.replaceAll("(?=\\s)\t", "   "));
                    return $$;
                }),
            PEG.rule$ZeroOrMore(
                PEG.rule(MD_PRESPACES),
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal(">"),
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
                        sb.append($i.get(1).pack().getValue());
                        sb.append($i.get(2).pack().getValue());
                    }
                    return $$;
                }))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue());
                sb.append($$.get(1).pack().getValue());
                PEGContext ctx = new PEGContext(sb.toString());
                try {
                    StringBuffer result = new StringBuffer();
                    result.append("<blockquote>");
                    result.append(PEG.rule(MARKDOWN).accept(ctx).getValue());
                    result.append("</blockquote>");
                    $$.setValue(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return $$;
            });
//        PEG.define(MD_QUOTE,
//            PEG.rule$Sequence(
//                PEG.rule$OneOrMore(PEG.rule$Literal(">")),
//                PEG.rule(MD_INLINE),
//                PEG.rule(MD_EOL_OR_EOF)),
//            PEG.rule$ZeroOrMore(
//                PEG.rule$ZeroOrMore(PEG.rule$Literal(">")),
//                PEG.rule(MD_INLINE),
//                PEG.rule(MD_EOL_OR_EOF)))
//            .action($$ -> {
//                StringBuffer sb = new StringBuffer("<blockquote>");
//                Stack<String> closer = new Stack<>();
//                closer.push("</blockquote>");
//
//                StringBuffer prefix = new StringBuffer();
//                PEGNode $0 = $$.get(0).get(0).get(0);
//                for (int i = 0; i < $0.length(); i++) {
//                    prefix.append($0.get(i).getValue());
//                }
//                sb.append($$.get(0).get(1).getValue().trim());
//
//                PEGNode $1 = $$.get(1);
//                for (int i = 0; i < $1.length(); i++) {
//                    StringBuffer nextPrefix = new StringBuffer();
//                    PEGNode $10 = $1.get(i).get(0);
//                    for (int j = 0; j < $10.length(); j++) {
//                        nextPrefix.append($10.get(j).get(0).getValue());
//                    }
//                    if (nextPrefix.length() > prefix.length()) {
//                        sb.append("<blockquote>");
//                        closer.push("</blockquote>");
//                    } else if (nextPrefix.length() < prefix.length() && nextPrefix.length() != 0) {
//                        sb.append(closer.pop());
//                    }
//                    sb.append($1.get(i).get(1).getValue().trim());
//                    prefix = nextPrefix;
//                }
//                sb.append(closer.pop());
//                $$.setValue(sb.toString());
//                return $$;
//            });
        // リスト
//        PEG.define(MD_LIST,
//            PEG.rule$Sequence(
//                PEG.rule(MD_PRESPACES),
//                PEG.rule$Choice(
//                    PEG.rule$Class("-*")
//                        .action($$ -> {
//                            $$.setValue("ul");
//                            return $$;
//                        }),
//                    PEG.rule$Sequence(
//                        PEG.rule$OneOrMore(
//                            PEG.rule$Class("0-9")),
//                        PEG.rule$Literal("."))
//                        .action($$ -> {
//                            $$.setValue("ol");
//                            return $$;
//                        })),
//                PEG.rule$OneOrMore(
//                    PEG.rule$Class("\\s")),
//                PEG.rule(MARKDOWN),
//                PEG.rule(MD_EOL_OR_EOF)),
//            PEG.rule$ZeroOrMore(
//                PEG.rule$ZeroOrMore(PEG.rule$Class(" \t"))
//                    .action($$ -> {
//                        StringBuffer sb = new StringBuffer();
//                        for (int i = 0; i < $$.length(); i++) {
//                            String value = $$.get(i).get(0).getValue();
//                            if ("\t".equals(value)) {
//                                sb.append("    ");
//                            } else {
//                                sb.append(value);
//                            }
//                        }
//                        $$.setValue(sb.toString());
//                        return $$;
//                    }),
//                PEG.rule$Choice(
//                    PEG.rule$Class("-*")
//                        .action($$ -> {
//                            $$.setValue("ul");
//                            return $$;
//                        }),
//                    PEG.rule$Sequence(
//                        PEG.rule$OneOrMore(
//                            PEG.rule$Class("0-9")),
//                        PEG.rule$Literal("."))
//                            .action($$ -> {
//                                $$.setValue("ol");
//                                return $$;
//                            })),
//                PEG.rule$OneOrMore(
//                    PEG.rule$Class("\\s")),
//                PEG.rule(MARKDOWN),
//                PEG.rule(MD_EOL_OR_EOF)))
//            .action($$ -> {
//                int depth = 0;
//                StringBuffer sb = new StringBuffer();
//                String marker = $$.get(0).get(1).getValue();
//                PEGNode $1 = $$.get(1);
//                Stack<String> stack = new Stack<>();
//                sb.append("<" + marker + ">");
//                stack.push("</" + marker + ">");
//                sb.append("<li>" + $$.get(0).get(3).getValue().trim());
//                stack.push("</li>");
//                for (int i = 0; i < $1.length(); i++) {
//                    String childMarker = $1.get(i).get(1).getValue();
//                    int spaces = $1.get(i).get(0).getValue().length();
//                    if (spaces >= depth + 2) {
//                        sb.append("<" + childMarker + ">");
//                        stack.push("</" + childMarker + ">");
//                    } else if (spaces <= depth - 2) {
//                        sb.append(stack.pop());
//                    } else if (!marker.equals(childMarker)) {
//                        sb.append(stack.pop());
//                        sb.append("<" + childMarker + ">");
//                        stack.push("</" + childMarker + ">");
//                    } else {
//                        sb.append(stack.pop());
//                    }
//                    sb.append("<li>" + $1.get(i).get(3).getValue().trim());
//                    stack.push("</li>");
//                    depth = spaces;
//                    marker = childMarker;
//                }
//                while (stack.size() > 0) {
//                    sb.append(stack.pop());
//                }
//
//                $$.setValue(sb.toString());
//                return $$;
//            });
        PEG.define(MD_LIST,
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(MD_LIST_AST),
                    PEG.rule(MD_LIST_PLUS),
                    PEG.rule(MD_LIST_BAR)
                    )))
            .action($$ -> {
                // 各アイテムが空行を含むかどうか
                boolean loose = isLoose($$);
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                sb.append("<ul>");
                for (int i = 0; i < $0.length(); i++) {
                    String item = $0.get(i).get(0).getValue();
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
        // 水平線
        PEG.define(MD_HR,
            PEG.rule(MD_PRESPACES),
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$Literal("*"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("*"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("*"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$ZeroOrMore(PEG.rule$Class("* \t"))),
                PEG.rule$Sequence(
                    PEG.rule$Literal("-"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("-"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("-"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$ZeroOrMore(PEG.rule$Class("- \t"))),
                PEG.rule$Sequence(
                    PEG.rule$Literal("_"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("_"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Literal("_"),
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$ZeroOrMore(PEG.rule$Class("_ \t")))),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                $$.setValue("<hr>");
                return $$;
            });
        
        // テーブル
//        PEG.define(MD_TABLE,
//            PEG.rule$Literal("|"),
//            );
        
        // パラグラフ
        PEG.define(MD_PARA,
            PEG.rule(MD_LINE),
            PEG.rule$ZeroOrMore(
                PEG.rule$Sequence(
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class(" \t")),
                    PEG.rule(MD_LINE))
                    .action($$ -> {
                        return $$.get(1).pack();
                    })))
            .action($$ -> {
                $$.setValue("<p>" + $$.pack().getValue().trim() + "</p>");
                return $$;
            });

        // 行
        PEG.define(MD_LINE,
            PEG.rule$Not(
                PEG.rule$Choice(
//                    PEG.rule(MD_HR),
                    PEG.rule(MD_LIST),
                    PEG.rule(MD_HEAD),
//                    PEG.rule(MD_STX_H1),
//                    PEG.rule(MD_STX_H2),
//                    PEG.rule(MD_PRE),
                    PEG.rule(MD_CODE_BLOCK),
                    PEG.rule(MD_QUOTE))),
            PEG.rule(MD_PRESPACES),
            PEG.rule(MD_INLINE),
            PEG.rule(MD_EOL_OR_EOF))
            .action($$ -> {
                String line = $$.get(2).pack()
                    .getValue()
                    .replaceAll("^\\s+", "")
                    .replaceAll("  +$", "<br />");
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
                    PEG.rule(MD_HTML),
                    PEG.rule(MD_IMAGE),
                    PEG.rule(MD_LINK),
                    PEG.rule(MD_CODE_SPAN),
                    PEG.rule(MD_AUTO_LINK),
                    PEG.rule(MD_BOLD),
                    PEG.rule(MD_ITALIC),
                    PEG.rule(MD_NUMERIC_REF),
                    PEG.rule(MD_ENTITY_REF),
                    PEG.rule(MD_ESCAPED),
                    PEG.rule(MD_SPECIAL),
                    PEG.rule(MD_CHAR))))
            .action($$ -> {
                return $$.pack();
            });

        /*
         * 開始 -
         *       (1) not followed by Unicode whitespace -> ('*' !' ') 
         *       either (2a) not followed by a punctuation character, -> ('*' !PUNCTUATION)
         *           or (2b) followed by a punctuation character and preceded by Unicode whitespace or a punctuation character.
         *                                                            -> &(' ' / PUNCTUATION) '*' PUNCTUATION
         */
        /*
         *  A delimiter run is either a sequence of one or more * characters
         *  that is not preceded or followed by a non-backslash-escaped * character,
         *  or a sequence of one or more _ characters 
         *  that is not preceded or followed by a non-backslash-escaped _ character.
         */
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

        PEG.define(MD_CODE_SPAN,
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$Literal("``"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(
                            PEG.rule$Literal("``")),
                        PEG.rule$Class("^\n")),
                    PEG.rule$Literal("``"))
                    .action($$ -> {
                        return $$.get(1).pack();
                    }),
                PEG.rule$Sequence(
                    PEG.rule$Literal("`"),
                    PEG.rule$OneOrMore(
                        PEG.rule$Not(
                            PEG.rule$Literal("`")),
                            PEG.rule$Class("^\n")),
                    PEG.rule$Literal("`"))
                    .action($$ -> {
                        return $$.get(1).pack();
                    })))
            .action($$ -> {
                $$.setValue("<code>" + $$.pack().getValue().trim() + "</code>");
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
            PEG.rule$OneOrMore(
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
                        $$.setValue(a($$.get(0).pack().getValue(),
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
                                    }))),
                                PEG.rule$ZeroOrMore(PEG.rule$Class("\\s")),
                                PEG.rule$Literal(")"))))
                    .action($$ -> {
                        String target = $$.get(0).pack().getValue();
                        String url = urlEncode($$.get(1).pack().getValue().trim().replaceAll("\n", ""));
                        if ($$.get(1).getType() == MD_LINK_PAREN) {
                            $$.setValue(a(target, url.replaceAll("^\\((.*?)\\)$", "$1"), null));
                        } else if ($$.get(1).get(4).length() > 0) {
                            $$.setValue(a(target, url, $$.get(1).get(4).pack().getValue()));
                        } else {
                            $$.setValue(a(target, url, null));
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
                            return $$.get(1).pack();
                        }),
                    PEG.rule(MD_LINK_PAREN),
                    PEG.rule$Class("^ \\)\n"))),
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
                $$.setValue(a(url, url, null));
                return $$;
            });

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

        PEG.define(MD_PUNCTUATION,
            PEG.rule$Class("!\"#$%&'\\(\\)\\*\\+\\,\\-./:;<=>?@\\[\\\\\\]^_`{|}~"))
            .action($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });

//        PEG.define(MD_PCDATA,
//            PEG.rule$Choice(
//                PEG.rule(MD_NUMERIC_REF),
//                PEG.rule(MD_ENTITY_REF),
//                PEG.rule$Class("^\r\n")))
//            .action($$ -> {
//                return $$.pack();
//            });
        
        PEG.define(MD_NUMERIC_REF,
            PEG.rule$Literal("&#"),
            PEG.rule$Choice(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("0-9"))
                    .action($$ -> {
                        int c = Integer.parseInt($$.pack().getValue(), 10);
                        if (c < 0xFFFF) {
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
                        if (c < 0xFFFF) {
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
                Character c = REFERENCE_MAP.get(name);
                if (c != null) {
                    $$.setValue(String.valueOf(c));
                } else {
                    $$.setValue("&amp;" + name + $$.get(2).getValue());
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
                        PEG.rule$Class("^\"\n")),
                    PEG.rule$Literal("\"")),
                PEG.rule$Sequence(
                    PEG.rule$Literal("'"),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^'\n")),
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
        
        // 実体参照定義
        REFERENCE_MAP.put("nbsp", (char)(char)160);
        REFERENCE_MAP.put("iexcl", (char)161);
        REFERENCE_MAP.put("cent", (char)162);
        REFERENCE_MAP.put("pound", (char)163);
        REFERENCE_MAP.put("curren", (char)164);
        REFERENCE_MAP.put("yen", (char)165);
        REFERENCE_MAP.put("brvbar", (char)166);
        REFERENCE_MAP.put("sect", (char)167);
        REFERENCE_MAP.put("uml", (char)168);
        REFERENCE_MAP.put("copy", (char)169);
        REFERENCE_MAP.put("ordf", (char)170);
        REFERENCE_MAP.put("laquo", (char)171);
        REFERENCE_MAP.put("not", (char)172);
        REFERENCE_MAP.put("shy", (char)173);
        REFERENCE_MAP.put("reg", (char)174);
        REFERENCE_MAP.put("macr", (char)175);
        REFERENCE_MAP.put("deg", (char)176);
        REFERENCE_MAP.put("plusmn", (char)177);
        REFERENCE_MAP.put("sup2", (char)178);
        REFERENCE_MAP.put("sup3", (char)179);
        REFERENCE_MAP.put("acute", (char)180);
        REFERENCE_MAP.put("micro", (char)181);
        REFERENCE_MAP.put("para", (char)182);
        REFERENCE_MAP.put("middot", (char)183);
        REFERENCE_MAP.put("cedil", (char)184);
        REFERENCE_MAP.put("sup1", (char)185);
        REFERENCE_MAP.put("ordm", (char)186);
        REFERENCE_MAP.put("raquo", (char)187);
        REFERENCE_MAP.put("frac14", (char)188);
        REFERENCE_MAP.put("frac12", (char)189);
        REFERENCE_MAP.put("frac34", (char)190);
        REFERENCE_MAP.put("iquest", (char)191);
        REFERENCE_MAP.put("Agrave", (char)192);
        REFERENCE_MAP.put("Aacute", (char)193);
        REFERENCE_MAP.put("Acirc", (char)194);
        REFERENCE_MAP.put("Atilde", (char)195);
        REFERENCE_MAP.put("Auml", (char)196);
        REFERENCE_MAP.put("Aring", (char)197);
        REFERENCE_MAP.put("AElig", (char)198);
        REFERENCE_MAP.put("Ccedil", (char)199);
        REFERENCE_MAP.put("Egrave", (char)200);
        REFERENCE_MAP.put("Eacute", (char)201);
        REFERENCE_MAP.put("Ecirc", (char)202);
        REFERENCE_MAP.put("Euml", (char)203);
        REFERENCE_MAP.put("Igrave", (char)204);
        REFERENCE_MAP.put("Iacute", (char)205);
        REFERENCE_MAP.put("Icirc", (char)206);
        REFERENCE_MAP.put("Iuml", (char)207);
        REFERENCE_MAP.put("ETH", (char)208);
        REFERENCE_MAP.put("Ntilde", (char)209);
        REFERENCE_MAP.put("Ograve", (char)210);
        REFERENCE_MAP.put("Oacute", (char)211);
        REFERENCE_MAP.put("Ocirc", (char)212);
        REFERENCE_MAP.put("Otilde", (char)213);
        REFERENCE_MAP.put("Ouml", (char)214);
        REFERENCE_MAP.put("times", (char)215);
        REFERENCE_MAP.put("Oslash", (char)216);
        REFERENCE_MAP.put("Ugrave", (char)217);
        REFERENCE_MAP.put("Uacute", (char)218);
        REFERENCE_MAP.put("Ucirc", (char)219);
        REFERENCE_MAP.put("Uuml", (char)220);
        REFERENCE_MAP.put("Yacute", (char)221);
        REFERENCE_MAP.put("THORN", (char)222);
        REFERENCE_MAP.put("szlig", (char)223);
        REFERENCE_MAP.put("agrave", (char)224);
        REFERENCE_MAP.put("aacute", (char)225);
        REFERENCE_MAP.put("acirc", (char)226);
        REFERENCE_MAP.put("atilde", (char)227);
        REFERENCE_MAP.put("auml", (char)228);
        REFERENCE_MAP.put("aring", (char)229);
        REFERENCE_MAP.put("aelig", (char)230);
        REFERENCE_MAP.put("ccedil", (char)231);
        REFERENCE_MAP.put("egrave", (char)232);
        REFERENCE_MAP.put("eacute", (char)233);
        REFERENCE_MAP.put("ecirc", (char)234);
        REFERENCE_MAP.put("euml", (char)235);
        REFERENCE_MAP.put("igrave", (char)236);
        REFERENCE_MAP.put("iacute", (char)237);
        REFERENCE_MAP.put("icirc", (char)238);
        REFERENCE_MAP.put("iuml", (char)239);
        REFERENCE_MAP.put("eth", (char)240);
        REFERENCE_MAP.put("ntilde", (char)241);
        REFERENCE_MAP.put("ograve", (char)242);
        REFERENCE_MAP.put("oacute", (char)243);
        REFERENCE_MAP.put("ocirc", (char)244);
        REFERENCE_MAP.put("otilde", (char)245);
        REFERENCE_MAP.put("ouml", (char)246);
        REFERENCE_MAP.put("divide", (char)247);
        REFERENCE_MAP.put("oslash", (char)248);
        REFERENCE_MAP.put("ugrave", (char)249);
        REFERENCE_MAP.put("uacute", (char)250);
        REFERENCE_MAP.put("ucirc", (char)251);
        REFERENCE_MAP.put("uuml", (char)252);
        REFERENCE_MAP.put("yacute", (char)253);
        REFERENCE_MAP.put("thorn", (char)254);
        REFERENCE_MAP.put("yuml", (char)255);
        REFERENCE_MAP.put("fnof", (char)402);
        REFERENCE_MAP.put("Alpha", (char)913);
        REFERENCE_MAP.put("Beta", (char)914);
        REFERENCE_MAP.put("Gamma", (char)915);
        REFERENCE_MAP.put("Delta", (char)916);
        REFERENCE_MAP.put("Epsilon", (char)917);
        REFERENCE_MAP.put("Zeta", (char)918);
        REFERENCE_MAP.put("Eta", (char)919);
        REFERENCE_MAP.put("Theta", (char)920);
        REFERENCE_MAP.put("Iota", (char)921);
        REFERENCE_MAP.put("Kappa", (char)922);
        REFERENCE_MAP.put("Lambda", (char)923);
        REFERENCE_MAP.put("Mu", (char)924);
        REFERENCE_MAP.put("Nu", (char)925);
        REFERENCE_MAP.put("Xi", (char)926);
        REFERENCE_MAP.put("Omicron", (char)927);
        REFERENCE_MAP.put("Pi", (char)928);
        REFERENCE_MAP.put("Rho", (char)929);
        REFERENCE_MAP.put("Sigma", (char)931);
        REFERENCE_MAP.put("Tau", (char)932);
        REFERENCE_MAP.put("Upsilon", (char)933);
        REFERENCE_MAP.put("Phi", (char)934);
        REFERENCE_MAP.put("Chi", (char)935);
        REFERENCE_MAP.put("Psi", (char)936);
        REFERENCE_MAP.put("Omega", (char)937);
        REFERENCE_MAP.put("alpha", (char)945);
        REFERENCE_MAP.put("beta", (char)946);
        REFERENCE_MAP.put("gamma", (char)947);
        REFERENCE_MAP.put("delta", (char)948);
        REFERENCE_MAP.put("epsilon", (char)949);
        REFERENCE_MAP.put("zeta", (char)950);
        REFERENCE_MAP.put("eta", (char)951);
        REFERENCE_MAP.put("theta", (char)952);
        REFERENCE_MAP.put("iota", (char)953);
        REFERENCE_MAP.put("kappa", (char)954);
        REFERENCE_MAP.put("lambda", (char)955);
        REFERENCE_MAP.put("mu", (char)956);
        REFERENCE_MAP.put("nu", (char)957);
        REFERENCE_MAP.put("xi", (char)958);
        REFERENCE_MAP.put("omicron", (char)959);
        REFERENCE_MAP.put("pi", (char)960);
        REFERENCE_MAP.put("rho", (char)961);
        REFERENCE_MAP.put("sigmaf", (char)962);
        REFERENCE_MAP.put("sigma", (char)963);
        REFERENCE_MAP.put("tau", (char)964);
        REFERENCE_MAP.put("upsilon", (char)965);
        REFERENCE_MAP.put("phi", (char)966);
        REFERENCE_MAP.put("chi", (char)967);
        REFERENCE_MAP.put("psi", (char)968);
        REFERENCE_MAP.put("omega", (char)969);
        REFERENCE_MAP.put("thetasym", (char)977);
        REFERENCE_MAP.put("upsih", (char)978);
        REFERENCE_MAP.put("piv", (char)982);
        REFERENCE_MAP.put("bull", (char)8226);
        REFERENCE_MAP.put("hellip", (char)8230);
        REFERENCE_MAP.put("prime", (char)8242);
        REFERENCE_MAP.put("Prime", (char)8243);
        REFERENCE_MAP.put("oline", (char)8254);
        REFERENCE_MAP.put("frasl", (char)8260);
        REFERENCE_MAP.put("weierp", (char)8472);
        REFERENCE_MAP.put("image", (char)8465);
        REFERENCE_MAP.put("real", (char)8476);
        REFERENCE_MAP.put("trade", (char)8482);
        REFERENCE_MAP.put("alefsym", (char)8501);
        REFERENCE_MAP.put("larr", (char)8592);
        REFERENCE_MAP.put("uarr", (char)8593);
        REFERENCE_MAP.put("rarr", (char)8594);
        REFERENCE_MAP.put("darr", (char)8595);
        REFERENCE_MAP.put("harr", (char)8596);
        REFERENCE_MAP.put("crarr", (char)8629);
        REFERENCE_MAP.put("lArr", (char)8656);
        REFERENCE_MAP.put("uArr", (char)8657);
        REFERENCE_MAP.put("rArr", (char)8658);
        REFERENCE_MAP.put("dArr", (char)8659);
        REFERENCE_MAP.put("hArr", (char)8660);
        REFERENCE_MAP.put("forall", (char)8704);
        REFERENCE_MAP.put("part", (char)8706);
        REFERENCE_MAP.put("exist", (char)8707);
        REFERENCE_MAP.put("empty", (char)8709);
        REFERENCE_MAP.put("nabla", (char)8711);
        REFERENCE_MAP.put("isin", (char)8712);
        REFERENCE_MAP.put("notin", (char)8713);
        REFERENCE_MAP.put("ni", (char)8715);
        REFERENCE_MAP.put("prod", (char)8719);
        REFERENCE_MAP.put("sum", (char)8721);
        REFERENCE_MAP.put("minus", (char)8722);
        REFERENCE_MAP.put("lowast", (char)8727);
        REFERENCE_MAP.put("radic", (char)8730);
        REFERENCE_MAP.put("prop", (char)8733);
        REFERENCE_MAP.put("infin", (char)8734);
        REFERENCE_MAP.put("ang", (char)8736);
        REFERENCE_MAP.put("and", (char)8743);
        REFERENCE_MAP.put("or", (char)8744);
        REFERENCE_MAP.put("cap", (char)8745);
        REFERENCE_MAP.put("cup", (char)8746);
        REFERENCE_MAP.put("int", (char)8747);
        REFERENCE_MAP.put("there4", (char)8756);
        REFERENCE_MAP.put("sim", (char)8764);
        REFERENCE_MAP.put("cong", (char)8773);
        REFERENCE_MAP.put("asymp", (char)8776);
        REFERENCE_MAP.put("ne", (char)8800);
        REFERENCE_MAP.put("equiv", (char)8801);
        REFERENCE_MAP.put("le", (char)8804);
        REFERENCE_MAP.put("ge", (char)8805);
        REFERENCE_MAP.put("sub", (char)8834);
        REFERENCE_MAP.put("sup", (char)8835);
        REFERENCE_MAP.put("nsub", (char)8836);
        REFERENCE_MAP.put("sube", (char)8838);
        REFERENCE_MAP.put("supe", (char)8839);
        REFERENCE_MAP.put("oplus", (char)8853);
        REFERENCE_MAP.put("otimes", (char)8855);
        REFERENCE_MAP.put("perp", (char)8869);
        REFERENCE_MAP.put("sdot", (char)8901);
        REFERENCE_MAP.put("lceil", (char)8968);
        REFERENCE_MAP.put("rceil", (char)8969);
        REFERENCE_MAP.put("lfloor", (char)8970);
        REFERENCE_MAP.put("rfloor", (char)8971);
        REFERENCE_MAP.put("lang", (char)9001);
        REFERENCE_MAP.put("rang", (char)9002);
        REFERENCE_MAP.put("loz", (char)9674);
        REFERENCE_MAP.put("spades", (char)9824);
        REFERENCE_MAP.put("clubs", (char)9827);
        REFERENCE_MAP.put("hearts", (char)9829);
        REFERENCE_MAP.put("diams", (char)9830);
        REFERENCE_MAP.put("OElig", (char)338);
        REFERENCE_MAP.put("oelig", (char)339);
        REFERENCE_MAP.put("Scaron", (char)352);
        REFERENCE_MAP.put("scaron", (char)353);
        REFERENCE_MAP.put("Yuml", (char)376);
        REFERENCE_MAP.put("circ", (char)710);
        REFERENCE_MAP.put("tilde", (char)732);
        REFERENCE_MAP.put("ensp", (char)8194);
        REFERENCE_MAP.put("emsp", (char)8195);
        REFERENCE_MAP.put("thinsp", (char)8201);
        REFERENCE_MAP.put("zwnj", (char)8204);
        REFERENCE_MAP.put("zwj", (char)8205);
        REFERENCE_MAP.put("lrm", (char)8206);
        REFERENCE_MAP.put("rlm", (char)8207);
        REFERENCE_MAP.put("ndash", (char)8211);
        REFERENCE_MAP.put("mdash", (char)8212);
        REFERENCE_MAP.put("lsquo", (char)8216);
        REFERENCE_MAP.put("rsquo", (char)8217);
        REFERENCE_MAP.put("sbquo", (char)8218);
        REFERENCE_MAP.put("ldquo", (char)8220);
        REFERENCE_MAP.put("rdquo", (char)8221);
        REFERENCE_MAP.put("bdquo", (char)8222);
        REFERENCE_MAP.put("dagger", (char)8224);
        REFERENCE_MAP.put("Dagger", (char)8225);
        REFERENCE_MAP.put("permil", (char)8240);
        REFERENCE_MAP.put("lsaquo", (char)8249);
        REFERENCE_MAP.put("rsaquo", (char)8250);
        REFERENCE_MAP.put("euro", (char)8364);
    }

    private static String a(String linkText, String url, String title) {
        if (title != null) {
            title = " title=\"" + escapeHTML(title) + "\"";
        } else {
            title = "";
        }
        return "<a href=\"" + urlEncode(url) + "\"" + title + ">" + linkText + "</a>";
    }
    
    private static String img(String alt, String url, String title) {
        return null;
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
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class(" \t")),
                PEG.rule$OneOrMore(
                    PEG.rule$Class("^ \t\n")),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class(" \t"),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^\n")))),
            PEG.rule$OneOrMore(
                PEG.rule$Not(PEG.rule$Literal(fence)),
                PEG.rule$Any()),
            PEG.rule$Literal(fence),
            PEG.rule$ZeroOrMore(
                PEG.rule$Literal(fc)))
            .action($$ -> {
                String lang = $$.get(3).length() > 1
                    ? $$.get(3).get(1).pack().getValue()
                    : "";
                if (!"".equals(lang)) {
                    lang = " class=\"language-" + lang + "\"";
                }
                StringBuffer sb = new StringBuffer();
                PEGNode $4 = $$.get(4);
                for (int i = 0; i < $4.length(); i++) {
                    sb.append($4.get(i).get(1).getValue());
                }
                $$.setValue("<pre><code" + lang + ">"+ escapeHTML(sb.toString().replaceAll("^\\s+",  "")) + "</code></pre>");
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
                    PEG.rule$Sequence(
                        PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                        PEG.rule$Literal(marker.toString()))),
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
                PEGContext ctx = new PEGContext(s);
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

    private Rule createListItemX(String marker) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= 4; j++) {
                String prefix = spaces(i) + marker + spaces(j);
            }
        }
        return null;
    }
    
    private Rule createListItemXX(String prefix) {
        return null;
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
            PEGContext ctx = new PEGContext(text);
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
            return node.getValue();
        } catch (PEGException e) {
            throw new MarkdownException(e);
        }
    }

    private String filterReferences(String text) {
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
                Character c = REFERENCE_MAP.get(matcher.group(8));
                if (c != null) {
                    decoded.append((char)c);
                } else {
                    decoded.append(matcher.group());
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
        MD_PUNCTUATION, MD_CHAR, MD_WS, MD_EMPTY, MD_EOL, MD_EOF, MD_EOL_OR_EOF, MD_PRESPACES,
        MD_HTML, HTML_TAG_NAME, HTML_ATTR, HTML_ATTR_NAME, HTML_ATTR_VALUE,
        HTML_OPEN_TAG, HTML_CLOSING_TAG, HTML_COMMENT, HTML_INSTRUCTION, HTML_DECLARATION,
        HTML_CDATA,
        MD_AST, MD_ASTAST, MD_BAR, MD_BARBAR, MD_AUTO_LINK_INNER, MD_LINK_TEXT, MD_LINK_PAREN,
        MD_LIST_AST, MD_LIST_PLUS, MD_LIST_BAR, MD_TABLE, MD_BLANK_LINE,
        MD_NUMERIC_REF, MD_ENTITY_REF/*, MD_PCDATA*/
        ,MD_STX_H1_UNDER, MD_STX_H2_UNDER
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
