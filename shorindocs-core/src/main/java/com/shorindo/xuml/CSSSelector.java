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

import static com.shorindo.xuml.CSSSelector.CSSTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.shorindo.xuml.DOMBuilder.Element;
import com.shorindo.xuml.PEGCombinator.BacktrackReader;
import com.shorindo.xuml.PEGCombinator.Node;
import com.shorindo.xuml.PEGCombinator.RuleTypes;
import com.shorindo.xuml.PEGCombinator.UnmatchException;

/**
 * 
 */
public class CSSSelector {
    private static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.rule(CSS_SELECTOR)
            .define(
                PEG.rule(ALL_SELECTOR),
                PEG.rule$ZeroOrMore(
                    PEG.rule(ALL_COMBINATOR),
                    PEG.rule(ALL_SELECTOR)))
            .pack($$ -> {
                // (ALL_SELECTOR (ALL_COMBINATOR ALL_SELECTOR)*)
                // ↓
                // (DESCENDANT_COMBINATOR (ALL_SELECTOR)) (ALL_COMBINATOR (ALL_SELECTOR))*
                Node $0 = $$.get(0);
                Node $1 = $$.get(1);
                $$.clear();
                Node combinator = new Node(DESCENDANT_COMBINATOR);
                combinator.add($0);
                $$.add(combinator);
                for (int i = 0; i < $1.length(); i++) {
                    Node c = $1.get(i).get(0);
                    Node s = $1.get(i).get(1);
                    c.clear();
                    c.add(s);
                    $$.add(c);
                }
                return $$;
            });
        PEG.rule(ALL_SELECTOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(UNIVERSAL_SELECTOR),
                    PEG.rule(ELEMENT_SELECTOR),
                    PEG.rule(CLASS_SELECTOR),
                    PEG.rule(ID_SELECTOR),
                    PEG.rule(ATTR_SELECTOR)))
            .pack($$ -> {
                return $$.get(0);
            });
        PEG.rule(UNIVERSAL_SELECTOR)
            .define(
                PEG.rule$Literal("*"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(CLASS_SELECTOR),
                        PEG.rule(ID_SELECTOR),
                        PEG.rule(ATTR_SELECTOR))));
        PEG.rule(ELEMENT_SELECTOR)
            .define(
                PEG.rule$Class("a-zA-Z"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("a-zA-Z0-9")),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(CLASS_SELECTOR),
                        PEG.rule(ID_SELECTOR),
                        PEG.rule(ATTR_SELECTOR))))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue().toString());
                Node $1 = $$.get(1);
                Node $2 = $$.get(2);
                for (int i = 0; i < $1.length(); i++) {
                    sb.append($1.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                for (int i = 0; i < $2.length(); i++) {
                    $$.add($2.get(i));
                }
                return $$;
            });
        PEG.rule(CLASS_SELECTOR)
            .define(
                PEG.rule$Literal("."),
                PEG.rule$Class("a-zA-Z"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("a-zA-Z0-9\\-")))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().toString());
                Node $2 = $$.get(2);
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.rule(ID_SELECTOR)
            .define(
                PEG.rule$Literal("#"),
                PEG.rule$Class("a-zA-Z"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("a-zA-Z0-9\\-")))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().toString());
                Node $2 = $$.get(2);
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.rule(ATTR_SELECTOR)
            .define(
                PEG.rule$Literal("["),
                PEG.rule(ATTR_NAME),
                PEG.rule$Optional(
                    PEG.rule(ATTR_COMPARATOR),
                    PEG.rule(ATTR_VALUE)),
                PEG.rule$Optional(
                    PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                    PEG.rule$Literal("i"),
                    PEG.rule$ZeroOrMore(PEG.rule$Literal(" "))
                    ),
                PEG.rule$Literal("]")
                )
            .pack($$ -> {
                Node $1 = $$.get(1);
                Node $2 = $$.get(2);
                Node $3 = $$.get(3);
                $$.clear();
                $$.add($1);
                if ($2.length() > 0) {
                    $$.add($2.get(0));
                    $$.add($2.get(1));
                }
                if ($3.length() > 0) {
                    $$.add($3.get(1));
                }
                return $$;
            });
        PEG.rule(ATTR_NAME)
            .define(
                PEG.rule$Class("a-zA-Z"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("a-zA-Z\\-")))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue().toString());
                Node $1 = $$.get(1);
                for (int i = 0; i < $1.length(); i++) {
                    sb.append($1.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.rule(ATTR_COMPARATOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule$Literal("="),
                    PEG.rule$Literal("~="),
                    PEG.rule$Literal("|="),
                    PEG.rule$Literal("^="),
                    PEG.rule$Literal("$="),
                    PEG.rule$Literal("*=")))
            .pack($$ -> {
                Node $0 = $$.get(0);
                $$.clear();
                $$.setValue($0.getValue());
                return $$;
            });
        PEG.rule(ATTR_VALUE)
            .define(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule$Literal("\""),
                        PEG.rule$ZeroOrMore(PEG.rule$Class("^\"")),
                        PEG.rule$Literal("\""))
                    .pack($$ -> {
                        StringBuffer sb = new StringBuffer();
                        Node $1 = $$.get(1);
                        for (int i = 0; i < $1.length(); i++) {
                            sb.append($1.get(i).get(0).getValue());
                        }
                        $$.clear();
                        $$.setValue(sb.toString());
                        return $$;
                    }),
                    PEG.rule$Sequence(
                        PEG.rule$Literal("'"),
                        PEG.rule$ZeroOrMore(PEG.rule$Class("^'")),
                        PEG.rule$Literal("'"))
                    .pack($$ -> {
                        StringBuffer sb = new StringBuffer();
                        Node $1 = $$.get(1);
                        for (int i = 0; i < $1.length(); i++) {
                            sb.append($1.get(i).get(0).getValue());
                        }
                        $$.clear();
                        $$.setValue(sb.toString());
                        return $$;
                    })))
                .pack($$ -> {
                    Node $0 = $$.get(0);
                    $$.clear();
                    $$.setValue($0.getValue());
                    return $$;
                });
        PEG.rule(ALL_COMBINATOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(CHILD_COMBINATOR),
                    PEG.rule(DESCENDANT_COMBINATOR)))
            .pack($$ -> {
                return $$.get(0);
            });
        PEG.rule(CHILD_COMBINATOR)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$Literal(">"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .pack($$ -> {
                Node $1 = $$.get(1);
                $1.setType(CHILD_COMBINATOR);
                return $1;
            });
        PEG.rule(DESCENDANT_COMBINATOR)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Literal(" ")))
            .pack($$ -> {
                Node $0 = $$.get(0).get(0);
                $0.setType(DESCENDANT_COMBINATOR);
                return $0;
            });
    }

    public static List<CSSSelector> parse(String selector) throws UnmatchException {
        List<CSSSelector> resultList = new ArrayList<>();
        Node node = PEG.rule(CSS_SELECTOR).accept(new BacktrackReader(selector));
        System.out.println(node.getSource() + " -> " + node.toString());
        for (int i = 0; i < node.length(); i++) {
            Node child = node.get(i);
//            resultList.add(new CSSSelector(child));
        }
        return resultList;
    }
    
    private CSSTypes type = UNIVERSAL_SELECTOR;
    private CombinatorTypes combinator;
    private String elementName;
    private List<CSSSelector> subSelectors;

    protected CSSSelector(Node node) {
        subSelectors = new ArrayList<>();
        switch ((CSSTypes)node.getType()) {
        case DESCENDANT_COMBINATOR:
        case CHILD_COMBINATOR:
        case ADJACENT_COMBINATOR:
        case SIBLING_COMBINATOR:
            break;
        default:
            throw new RuntimeException(node.getType() + " not allowed here.");
        }
        
        Node selector = node.get(0);
        switch ((CSSTypes)selector.getType()) {
        case UNIVERSAL_SELECTOR:
            this.type = UNIVERSAL_SELECTOR;
            break;
        case ELEMENT_SELECTOR:
            this.type = ELEMENT_SELECTOR;
            break;
        case CLASS_SELECTOR:
        case ID_SELECTOR:
        case ATTR_SELECTOR:
            this.type = UNIVERSAL_SELECTOR;
            break;
        default:
            throw new RuntimeException(node.getType() + " not allowed here.");
        }
    }
    
    public CombinatorTypes getCombinator() {
        return combinator;
    }
    
    public CSSTypes getType() {
        return type;
    }

    public boolean match(Element element) {
        return false;
    }

    protected enum CSSTypes implements RuleTypes {
        CSS_SELECTOR, ALL_SELECTOR, UNIVERSAL_SELECTOR, ELEMENT_SELECTOR,
        CLASS_SELECTOR, ID_SELECTOR, ATTR_SELECTOR, ATTR_NAME, ATTR_VALUE,
        ATTR_COMPARATOR, ALL_COMBINATOR, CHILD_COMBINATOR, DESCENDANT_COMBINATOR,
        ADJACENT_COMBINATOR, SIBLING_COMBINATOR
        ;
    }
    
    public enum CombinatorTypes {
        DESCENDANT, CHILD, SIBLING, ADJACENT;
    }
}
