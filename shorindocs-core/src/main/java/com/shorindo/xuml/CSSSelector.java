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
                    PEG.rule(ALL_OPERATOR),
                    PEG.rule(ALL_SELECTOR)))
            .pack($$ -> {
                Node $0 = $$.get(0);
                Node $1 = $$.get(1);
                $$.clear();
                $$.add($0);
                for (int i = 0; i < $1.length(); i++) {
                    $$.add($1.get(i));
                }
                return $$;
            });
        PEG.rule(ALL_SELECTOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(GENERAL_SELECTOR),
                    PEG.rule(ELEMENT_SELECTOR),
                    PEG.rule(CLASS_SELECTOR),
                    PEG.rule(ID_SELECTOR),
                    PEG.rule(ATTR_SELECTOR)))
            .pack($$ -> {
                return $$.get(0);
            });
        PEG.rule(GENERAL_SELECTOR)
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
                    sb.append($1.get(i).getValue().toString());
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
                    sb.append($2.get(i).getValue().toString());
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
                    sb.append($2.get(i).getValue().toString());
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
                    sb.append($1.get(i).getValue().toString());
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
                            sb.append($1.get(i).getValue());
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
                            sb.append($1.get(i).getValue());
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
        PEG.rule(ALL_OPERATOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(CHILD_OPERATOR),
                    PEG.rule(DESCENDANT_OPERATOR)))
            .pack($$ -> {
                return $$.get(0);
            });
        PEG.rule(CHILD_OPERATOR)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$Literal(">"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .pack($$ -> {
                Node $1 = $$.get(1);
                $1.setType(CHILD_OPERATOR);
                return $1;
            });
        PEG.rule(DESCENDANT_OPERATOR)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Literal(" ")))
            .pack($$ -> {
                Node $0 = $$.get(0).get(0);
                $0.setType(DESCENDANT_OPERATOR);
                return $0;
            });
    }

    public static List<CSSSelector> parse(String selector) throws UnmatchException {
        Node node = PEG.rule(CSS_SELECTOR).accept(new BacktrackReader(selector));
        System.out.println(node.getSource() + " -> " + node.toString());
        //return visit(node);
        return null;
    }
    
    private CSSTypes type = GENERAL_SELECTOR;
    private String name;
    private String id;
    private String classes;
    private String attrs;

    public CSSSelector() {
    }
    
    public CSSTypes getType() {
        return type;
    }

    private void setType(CSSTypes type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getClasses() {
        return classes;
    }

    private void setClasses(String classes) {
        this.classes = classes;
    }

    public String getAttrs() {
        return attrs;
    }

    private void setAttrs(String attrs) {
        this.attrs = attrs;
    }

    public List<Element> find(Element target) {
        return null;
    }

    protected enum CSSTypes implements RuleTypes {
        CSS_SELECTOR, ALL_SELECTOR, GENERAL_SELECTOR, ELEMENT_SELECTOR,
        CLASS_SELECTOR, ID_SELECTOR, ATTR_SELECTOR, ATTR_NAME, ATTR_VALUE,
        ALL_OPERATOR, CHILD_OPERATOR, DESCENDANT_OPERATOR, ATTR_COMPARATOR
        ;
    }
}
