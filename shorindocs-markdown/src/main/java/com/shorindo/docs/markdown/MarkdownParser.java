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

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.util.PEGCombinator;
import com.shorindo.util.PEGCombinator.BacktrackReader;
import com.shorindo.util.PEGCombinator.PEGNode;
import com.shorindo.util.PEGCombinator.RuleTypes;
import com.shorindo.util.PEGCombinator.UnmatchException;

/**
 * 
 */
public class MarkdownParser {
    private static ActionLogger LOG = ActionLogger.getLogger(MarkdownParser.class);
    private static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.rule(WIKI_DOCUMENT)
            .define(
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(HEAD2S),
                        PEG.rule(HEAD1S),
                        PEG.rule(HEAD1D),
                        PEG.rule(HEAD2D),
                        PEG.rule(BLOCK_PRE),
                        PEG.rule(LINE),
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
        PEG.rule(HEAD1D)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Class("^\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Class("\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Literal("=")),
                PEG.rule$Optional(PEG.rule(EOL)))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    sb.append($0.get(i).get(0).getValue());
                }
                $$.setValue("<h1>" + sb.toString().trim() + "</h1>");
                return $$;
            });
        // # 見出し１
        PEG.rule(HEAD1S)
            .define(
                PEG.rule$Literal("#"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$ZeroOrMore(PEG.rule$Any()),
                PEG.rule$Choice(
                    PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")),
                    PEG.rule(EOF)))
            .pack($$ -> {
                PEGNode $2 = $$.get(2);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue());
                }
                $$.setValue("<h1>" + sb.toString().trim() + "</h1>");
                return $$;
            });
        // 見出し２
        // --------
        PEG.rule(HEAD2D)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Class("^\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Class("\r\n")),
                PEG.rule$OneOrMore(PEG.rule$Literal("-")),
                PEG.rule$Choice(
                    PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")),
                    PEG.rule(EOF)))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    sb.append($0.get(i).get(0).getValue());
                }
                $$.setValue("<h2>" + sb.toString().trim() + "</h2>");
                return $$;
            });
        // ## 見出し２
        PEG.rule(HEAD2S)
            .define(
                PEG.rule$Literal("##"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$ZeroOrMore(PEG.rule$Any()),
                PEG.rule$Choice(
                    PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")),
                    PEG.rule(EOF)))
            .pack($$ -> {
                PEGNode $2 = $$.get(2);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue());
                }
                $$.setValue("<h2>" + sb.toString().trim() + "</h2>");
                return $$;
            });
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
                            PEG.rule$Class("^_")),
                            PEG.rule$Literal("_"))
                        .pack($$ -> {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < $$.get(1).length(); i++) {
                                sb.append($$.get(1).get(i).get(0).getValue());
                            }
                            $$.setValue("<i>" + sb.toString() + "</i>");
                            return $$;
                        }),
                   PEG.rule$Sequence(
                       PEG.rule$Literal("*"),
                       PEG.rule$OneOrMore(
                           PEG.rule$Class("^*")),
                           PEG.rule$Literal("*")))
                       .pack($$ -> {
                           StringBuffer sb = new StringBuffer();
                           for (int i = 0; i < $$.get(1).length(); i++) {
                               sb.append($$.get(1).get(i).get(0).getValue());
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
                            PEG.rule$Class("^_")),
                            PEG.rule$Literal("__"))
                    .pack($$ -> {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < $$.get(1).length(); i++) {
                            sb.append($$.get(1).get(i).get(0).getValue());
                        }
                        $$.setValue("<b>" + sb.toString() + "</b>");
                        return $$;
                    }),
                    PEG.rule$Sequence(
                        PEG.rule$Literal("**"),
                        PEG.rule$OneOrMore(
                            PEG.rule$Class("^*")),
                            PEG.rule$Literal("**")))
                   .pack($$ -> {
                       StringBuffer sb = new StringBuffer();
                       for (int i = 0; i < $$.get(1).length(); i++) {
                           sb.append($$.get(1).get(i).get(0).getValue());
                       }
                       $$.setValue("<b>" + sb.toString() + "</b>");
                       return $$;
                   }))
        .pack($$ -> {
            $$.setValue($$.get(0).getValue());
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
            PEGNode node = PEG.rule(WIKI_DOCUMENT).accept(reader);
            LOG.debug(node.toString());
            LOG.debug(node.getValue());
            if (reader.available() > 0) {
                throw new MarkdownException(reader.subString(reader.position()));
            }
            return node.getValue();
        } catch (UnmatchException e) {
            throw new MarkdownException(e);
        }
    }

    public enum MarkdownRules implements RuleTypes {
        WIKI_DOCUMENT, HEAD1D, HEAD1S, HEAD2D, HEAD2S, HEAD3S, HEAD4S, HEAD5S, HEAD6S,
        BLOCK_PRE, BLOCK_CODE, LINE, INLINE, ITALIC, BOLD, ESCAPED, SPECIAL, CHAR,
        EOL, EOF
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
