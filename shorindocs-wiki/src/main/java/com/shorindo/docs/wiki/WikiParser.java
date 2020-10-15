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
package com.shorindo.docs.wiki;

import static com.shorindo.docs.wiki.WikiParser.WikiRules.*;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.tools.PEGCombinator;
import com.shorindo.tools.PEGCombinator.PEGContext;
import com.shorindo.tools.PEGCombinator.PEGException;
import com.shorindo.tools.PEGCombinator.PEGNode;
import com.shorindo.tools.PEGCombinator.RuleTypes;
import com.shorindo.tools.PEGCombinator.UnmatchException;

/**
 * 
 */
public class WikiParser {
    private static ActionLogger LOG = ActionLogger.getLogger(WikiParser.class);
    private static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.define(WIKI_DOCUMENT,
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(HEAD1),
                        PEG.rule(HEAD2),
                        PEG.rule(HEAD3),
                        PEG.rule(HEAD4),
                        PEG.rule(HEAD5),
                        PEG.rule(HEAD6),
                        PEG.rule$Any())))
            .action($$ -> {
                StringBuffer sb = new StringBuffer();
                PEGNode $0 = $$.get(0);
                for (int i = 0; i < $0.length(); i++) {
                    sb.append($0.get(i).get(0).getValue());
                }
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(HEAD1,
                PEG.rule$Literal("======"),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("======"),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")));
        PEG.define(HEAD2,
                PEG.rule$Literal("====="),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("====="),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")));
        PEG.define(HEAD3,
                PEG.rule$Literal("===="),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("===="),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")))
            .action(WikiParser::header3);
        PEG.define(HEAD4,
                PEG.rule$Literal("==="),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("==="),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")))
            .action(WikiParser::header4);
        PEG.define(HEAD5,
                PEG.rule$Literal("=="),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("=="),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")));
        PEG.define(HEAD6,
                PEG.rule$Literal("="),
                PEG.rule$OneOrMore(PEG.rule$Class("^=")),
                PEG.rule$Literal("="),
                PEG.rule$ZeroOrMore(PEG.rule$Class("\r\n")));
    }
    
    private static PEGNode header1(PEGNode $$) {
        return header($$, "h1");
    }

    private static PEGNode header2(PEGNode $$) {
        return header($$, "h2");
    }

    private static PEGNode header3(PEGNode $$) {
        return header($$, "h3");
    }

    private static PEGNode header4(PEGNode $$) {
        return header($$, "h4");
    }

    private static PEGNode header5(PEGNode $$) {
        return header($$, "h5");
    }

    private static PEGNode header6(PEGNode $$) {
        return header($$, "h6");
    }

    private static PEGNode header(PEGNode $$, String tag) {
        PEGNode $1 = $$.get(1);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < $1.length(); i++) {
            sb.append($1.get(i).get(0).getValue());
        }
        $$.clear();
        $$.setValue("<" + tag + ">" + sb.toString() + "</" + tag + ">");
        return $$;
    }
    public void parse(String text) {
        try {
            PEGNode node = PEG.rule(WIKI_DOCUMENT).accept(PEG.createContext(text));
            LOG.debug(node.toString());
            LOG.debug(node.getValue());
        } catch (UnmatchException e) {
            e.printStackTrace();
        } catch (PEGException e) {
			e.printStackTrace();
		}
    }

    public enum WikiRules implements com.shorindo.tools.PEGCombinator.RuleTypes {
        WIKI_DOCUMENT, HEAD1, HEAD2, HEAD3, HEAD4, HEAD5, HEAD6
        ;
    }
}
