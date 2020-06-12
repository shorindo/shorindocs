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

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.util.PEGCombinator;
import com.shorindo.util.PEGCombinator.BacktrackReader;
import com.shorindo.util.PEGCombinator.PEGNode;
import com.shorindo.util.PEGCombinator.RuleTypes;
import com.shorindo.util.PEGCombinator.Statistics;
import com.shorindo.util.PEGCombinator.UnmatchException;

/**
 * 
 */
public class MarkdownParser {
    private static ActionLogger LOG = ActionLogger.getLogger(MarkdownParser.class);
    private static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.rule(WIKI)
            .define(
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(HORIZONTAL),
                        PEG.rule(LIST),
                        PEG.rule(HEAD6),
                        PEG.rule(HEAD5),
                        PEG.rule(HEAD4),
                        PEG.rule(HEAD3),
                        PEG.rule(HEAD2),
                        PEG.rule(HEAD1),
                        PEG.rule(HEAD1UNDER),
                        PEG.rule(HEAD2UNDER),
                        PEG.rule(BLOCK_PRE),
                        PEG.rule(BLOCK_CODE),
                        PEG.rule(BLOCK_QUOTE),
                        PEG.rule(PARA),
                        PEG.rule(EOL))),
                PEG.rule(EOF))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    sb.append($0.get(i).get(0).getValue());
                }
                $$.setValue(sb.toString());
                return $$;
            });
        // 見出し１
        // ========
        PEG.rule(HEAD1UNDER)
            .define(
                PEG.rule(INLINE),
                PEG.rule$OneOrMore(PEG.rule$Class("\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Literal("=")),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h1>" + $$.get(0).getValue().trim() + "</h1>");
                return $$;
            });
        // # 見出し１
        PEG.rule(HEAD1)
            .define(
                PEG.rule$Literal("#"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h1>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h1>");
                return $$;
            });
        // 見出し２
        // --------
        PEG.rule(HEAD2UNDER)
            .define(
                PEG.rule(INLINE),
                PEG.rule$OneOrMore(PEG.rule$Class("\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Literal("-")),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h2>" + $$.get(0).getValue().trim() + "</h2>");
                return $$;
            });
        // ## 見出し２
        PEG.rule(HEAD2)
            .define(
                PEG.rule$Literal("##"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h2>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h2>");
                return $$;
            });
        // ## 見出し３
        PEG.rule(HEAD3)
            .define(
                PEG.rule$Literal("###"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h3>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h3>");
                return $$;
            });
        // ## 見出し４
        PEG.rule(HEAD4)
            .define(
                PEG.rule$Literal("####"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h4>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h4>");
                return $$;
            });
        // ## 見出し５
        PEG.rule(HEAD5)
            .define(
                PEG.rule$Literal("#####"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h5>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h5>");
                return $$;
            });
        // ## 見出し６
        PEG.rule(HEAD6)
            .define(
                PEG.rule$Literal("######"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue("<h6>" + $$.get(2).getValue().trim().replaceAll(" +#*$", "") + "</h6>");
                return $$;
            });
        // 整形済
        PEG.rule(BLOCK_PRE)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Choice(
                        PEG.rule$Literal("    "),
                        PEG.rule$Literal("\t")),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Class("^\r\n")),
                    PEG.rule$Optional(PEG.rule(EOL))))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    PEGNode $001 = $0.get(i).get(1);
                    for (int j = 0; j < $001.length(); j++) {
                        sb.append($001.get(j).get(0).getValue());
                    }
                    sb.append("\n");
                }
                $$.clear();
                $$.setValue("<pre>" + sb.toString() + "</pre>");
                return $$;
            });
        // コード
        PEG.rule(BLOCK_CODE)
            .define(
                PEG.rule$Literal("```"),
                PEG.rule$OneOrMore(
                    PEG.rule$Not(PEG.rule$Literal("```")),
                    PEG.rule$Any()),
                PEG.rule$Literal("```"))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $1 = $$.get(1);
                for (int i = 0; i < $1.length(); i++) {
                    sb.append($1.get(i).get(1).getValue());
                }
                $$.setValue("<pre><code>" + sb.toString() + "</code></pre>");
                return $$;
            });
        // 引用
        PEG.rule(BLOCK_QUOTE)
            .define(
                PEG.rule$Sequence(
                    PEG.rule$OneOrMore(PEG.rule$Literal(">")),
                    PEG.rule(INLINE),
                    PEG.rule$Optional(PEG.rule(EOL))),
                PEG.rule$ZeroOrMore(
                    PEG.rule$ZeroOrMore(PEG.rule$Literal(">")),
                    PEG.rule(INLINE),
                    PEG.rule$Optional(PEG.rule(EOL))))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer("<blockquote>");
                Stack<String> closer = new Stack<>();
                closer.push("</blockquote>");

                StringBuffer prefix = new StringBuffer();
                PEGNode $0 = $$.get(0).get(0).get(0);
                for (int i = 0; i < $0.length(); i++) {
                    prefix.append($0.get(i).getValue());
                }
                sb.append($$.get(0).get(1).getValue().trim());

                PEGNode $1 = $$.get(1);
                for (int i = 0; i < $1.length(); i++) {
                    StringBuffer nextPrefix = new StringBuffer();
                    PEGNode $10 = $1.get(i).get(0);
                    for (int j = 0; j < $10.length(); j++) {
                        nextPrefix.append($10.get(j).get(0).getValue());
                    }
                    if (nextPrefix.length() > prefix.length()) {
                        sb.append("<blockquote>");
                        closer.push("</blockquote>");
                    } else if (nextPrefix.length() < prefix.length() && nextPrefix.length() != 0) {
                        sb.append(closer.pop());
                    }
                    sb.append($1.get(i).get(1).getValue().trim());
                    prefix = nextPrefix;
                }
                sb.append(closer.pop());
                $$.setValue(sb.toString());
                return $$;
            });
        // リスト
        PEG.rule(LIST)
            .define(
                PEG.rule$Sequence(
                    PEG.rule$Choice(
                        PEG.rule$Class("-*")
                            .pack($$ -> {
                                $$.setValue("ul");
                                return $$;
                            }),
                        PEG.rule$Sequence(
                            PEG.rule$OneOrMore(
                                PEG.rule$Class("0-9")),
                            PEG.rule$Literal("."))
                            .pack($$ -> {
                                $$.setValue("ol");
                                return $$;
                            })),
                    PEG.rule(INLINE),
                    PEG.rule$Optional(PEG.rule(EOL))),
                PEG.rule$ZeroOrMore(
                    PEG.rule$ZeroOrMore(PEG.rule$Class(" \t")),
                    PEG.rule$Choice(
                        PEG.rule$Class("-*")
                            .pack($$ -> {
                                $$.setValue("ul");
                                return $$;
                            }),
                        PEG.rule$Sequence(
                            PEG.rule$OneOrMore(
                                PEG.rule$Class("0-9")),
                            PEG.rule$Literal("."))
                            .pack($$ -> {
                                $$.setValue("ol");
                                return $$;
                            })),
                    PEG.rule(INLINE),
                    PEG.rule$Optional(PEG.rule(EOL))))
            .pack($$ -> {
                int depth = 0;
                StringBuffer sb = new StringBuffer();
                String marker = $$.get(0).get(0).getValue();
                PEGNode $1 = $$.get(1);
                Stack<String> stack = new Stack<>();
                sb.append("<" + marker + ">");
                stack.push("</" + marker + ">");
                sb.append("<li>" + $$.get(0).get(1).getValue().trim() + "</li>");
                for (int i = 0; i < $1.length(); i++) {
                    String childMarker = $1.get(i).get(1).getValue();
                    int spaces = $1.get(i).get(0).length();
                    if (spaces >= depth + 2) {
                        sb.append("<" + childMarker + ">");
                        stack.push("</" + childMarker + ">");
                    } else if (spaces <= depth - 2) {
                        sb.append(stack.pop());
                    } else if (!marker.equals(childMarker)) {
                        sb.append(stack.pop());
                        sb.append("<" + childMarker + ">");
                        stack.push("</" + childMarker + ">");
                    }
                    sb.append("<li>" + $1.get(i).get(2).getValue().trim() + "</li>");
                    depth = spaces;
                    marker = childMarker;
                }
                while (stack.size() > 0) {
                    sb.append(stack.pop());
                }

                $$.setValue(sb.toString());
                return $$;
            });
        // 水平線
        PEG.rule(HORIZONTAL)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Class("*\\-_ ")),
                PEG.rule$Choice(
                    PEG.rule(EOL),
                    PEG.rule(EOF)))
            .pack($$ -> {
                $$.setValue("<hr>");
                return $$;
            });
        // パラグラフ
        PEG.rule(PARA)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule(LINE)))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < $$.get(0).length(); i++) {
                    sb.append($$.get(0).get(i).get(0).getValue());
                }
                $$.setValue("<p>" + sb.toString() + "</p>");
                return $$;
            });
        // 行
        PEG.rule(LINE)
            .define(
                PEG.rule(INLINE),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });
        PEG.rule(INLINE)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Choice(
                        PEG.rule(IMAGE),
                        PEG.rule(LINK),
                        PEG.rule(FLAT_LINK),
                        PEG.rule(BOLD),
                        PEG.rule(ITALIC),
                        PEG.rule(ESCAPED),
                        PEG.rule(SPECIAL),
                        PEG.rule(CHAR))))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    sb.append($0.get(i).get(0).getValue());
                }
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.rule(ITALIC)
            .define(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal("_"),
                        PEG.rule$OneOrMore(
                            PEG.rule$Not(PEG.rule$Literal("_")),
                            PEG.rule$Choice(
                                PEG.rule(BOLD),
                                PEG.rule$Any())),
                        PEG.rule$Literal("_"))
                        .pack($$ -> {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < $$.get(1).length(); i++) {
                                sb.append($$.get(1).get(i).get(1).getValue());
                            }
                            $$.setValue("<i>" + sb.toString() + "</i>");
                            return $$;
                        }),
                   PEG.rule$Sequence(
                       PEG.rule$Literal("*"),
                       PEG.rule$OneOrMore(
                           PEG.rule$Not(PEG.rule$Literal("*")),
                           PEG.rule$Choice(
                               PEG.rule(BOLD),
                               PEG.rule$Any())),
                       PEG.rule$Literal("*")))
                       .pack($$ -> {
                           StringBuffer sb = new StringBuffer();
                           for (int i = 0; i < $$.get(1).length(); i++) {
                               sb.append($$.get(1).get(i).get(1).getValue());
                           }
                           $$.setValue("<i>" + sb.toString() + "</i>");
                           return $$;
                       }))
            .pack($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });
        PEG.rule(BOLD)
            .define(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal("__"),
                        PEG.rule$OneOrMore(
                            PEG.rule$Not(PEG.rule$Literal("__")),
                            PEG.rule$Choice(
                                PEG.rule(ITALIC),
                                PEG.rule$Any())),
                        PEG.rule$Literal("__"))
                        .pack($$ -> {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < $$.get(1).length(); i++) {
                                PEGNode $n = $$.get(1).get(i).get(1);
                                sb.append($n.getValue());
                            }
                            $$.setValue("<b>" + sb.toString() + "</b>");
                            return $$;
                        }),
                    PEG.rule$Sequence(
                        PEG.rule$Literal("**"),
                        PEG.rule$OneOrMore(
                            PEG.rule$Not(PEG.rule$Literal("**")),
                            PEG.rule$Choice(
                                PEG.rule(ITALIC),
                                PEG.rule$Any())),
                        PEG.rule$Literal("**"))
                        .pack($$ -> {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < $$.get(1).length(); i++) {
                                PEGNode $n = $$.get(1).get(i).get(1);
                                sb.append($n.getValue());
                            }
                            $$.setValue("<b>" + sb.toString() + "</b>");
                            return $$;
                        })))
        .pack($$ -> {
            $$.setValue($$.get(0).getValue());
            return $$;
        });
        // URL
        PEG.rule(URL)
            .define(
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
                            PEG.rule(URL_PCHARS))),
                    // query
                    PEG.rule$Optional(
                        PEG.rule$Literal("?"),
                        PEG.rule(URL_PCHARS)),
                    // fragment
                    PEG.rule$Optional(
                        PEG.rule$Literal("#"),
                        PEG.rule(URL_PCHARS))
                ))
            .pack($$ -> {
                $$.setValue($$.getSource());
                return $$;
            });
        PEG.rule(URL_PCHARS)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Choice(
                        PEG.rule$Class("a-zA-Z0-9-._~!$&'*+,;=:@"),
                        PEG.rule$Sequence(
                            PEG.rule$Literal("%"),
                            PEG.rule$Class("0-9a-fA-F"),
                            PEG.rule$Class("0-9a-fA-F")))));
        PEG.rule(LINK)
            .define(
                PEG.rule$Literal("["),
                PEG.rule$OneOrMore(
                    PEG.rule$Class("^]")),
                PEG.rule$Literal("]("),
                PEG.rule(URL),
                PEG.rule$Literal(")"))
            .pack($$ -> {
                StringBuffer title = new StringBuffer();
                for (int i = 0; i < $$.get(1).length(); i++) {
                    title.append($$.get(1).get(i).get(0).getValue());
                }
                String url = $$.get(3).getValue();
                $$.setValue("<a href=\"" + url + "\">" + title.toString() + "</a>");
                return $$;
            });
        PEG.rule(FLAT_LINK)
            .define(
                PEG.rule(URL))
            .pack($$ -> {
                $$.setValue("<a href=\"" + $$.getSource() + "\">" + $$.getSource() + "</a>");
                return $$;
            });
        PEG.rule(IMAGE)
            .define(
                PEG.rule$Literal("!["),
                PEG.rule$OneOrMore(
                    PEG.rule$Class("^]")),
                PEG.rule$Literal("]("),
                PEG.rule(URL),
                PEG.rule$Literal(")"))
            .pack($$ -> {
                StringBuffer title = new StringBuffer();
                for (int i = 0; i < $$.get(1).length(); i++) {
                    title.append($$.get(1).get(i).get(0).getValue());
                }
                String url = $$.get(3).getValue();
                $$.setValue("<img src=\"" + url + "\" title=\"" + title.toString() + "\">");
                return $$;
            });
        PEG.rule(ESCAPED)
            .define(
                PEG.rule$Literal("\\"),
                PEG.rule$Any())
            .pack($$ -> {
                return $$.get(1);
            });
        PEG.rule(SPECIAL)
            .define(
                PEG.rule$Choice(
                    PEG.rule$Literal("<")
                        .pack($$ -> { $$.setValue("&lt;"); return $$; }),
                    PEG.rule$Literal(">")
                        .pack($$ -> { $$.setValue("&gt;"); return $$; }),
                    PEG.rule$Literal("&")
                        .pack($$ -> { $$.setValue("&amp;"); return $$; })))
            .pack($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });
        PEG.rule(CHAR)
            .define(
                PEG.rule$Class("^\r\n"))
            .pack($$ -> {
                $$.setValue($$.get(0).getValue());
                return $$;
            });
        PEG.rule(EOL)
            .define(
                PEG.rule$Optional(PEG.rule$Literal("\r")),
                PEG.rule$Literal("\n"))
            .pack($$ -> {
                $$.setValue("");
                return $$;
            });
        PEG.rule(EOF)
            .define(
                PEG.rule$Not(PEG.rule$Any()));
    }

    public String parse(String text) throws MarkdownException {
        try {
            BacktrackReader reader = new BacktrackReader(text);
            PEGNode node = PEG.rule(WIKI).accept(reader);
            //LOG.debug(node.toString());
            //LOG.debug(node.getValue());
            if (reader.available() > 0) {
                throw new MarkdownException(reader.subString(reader.position()));
            }
            if (LOG.isDebugEnabled()) {
                AtomicInteger called = new AtomicInteger();
                AtomicInteger success = new AtomicInteger();
                PEG.getStatistics()
                .entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    Float r1 = 100.0f * e1.getValue().getSuccess() / e1.getValue().getCalled();
                    Float r2 = 100.0f * e2.getValue().getSuccess() / e2.getValue().getCalled();
                    return r2.compareTo(r1);
                })
                .forEach(e -> {
                    called.addAndGet(e.getValue().getCalled());
                    success.addAndGet(e.getValue().getSuccess());
                    LOG.debug(e.getKey() + ": {0} / {1}",
                        e.getValue().getSuccess(),
                        e.getValue().getCalled());
                });
                LOG.debug("Total: {0} / {1}", success, called);
            }
            return node.getValue();
        } catch (UnmatchException e) {
            throw new MarkdownException(e);
        }
    }

    public enum MarkdownRules implements RuleTypes {
        WIKI, HEAD1UNDER, HEAD2UNDER, HEAD1, HEAD2, HEAD3, HEAD4, HEAD5, HEAD6,
        BLOCK_PRE, BLOCK_CODE, BLOCK_QUOTE, LIST, PARA, LINE, INLINE, ITALIC, BOLD,
        HORIZONTAL, LINK, FLAT_LINK, IMAGE, URL, URL_PCHARS, ESCAPED, SPECIAL,
        CHAR, EOL, EOF
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
