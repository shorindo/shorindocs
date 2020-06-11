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

import static com.shorindo.xuml.CSSSelector.CSSTokens.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.shorindo.docs.action.ActionMessages;
import com.shorindo.util.PEGCombinator;
import com.shorindo.util.PEGCombinator.BacktrackReader;
import com.shorindo.util.PEGCombinator.PEGNode;
import com.shorindo.util.PEGCombinator.RuleTypes;
import com.shorindo.util.PEGCombinator.UnmatchException;
import com.shorindo.xuml.DOMBuilder.Element;

/**
 * 
 */
public class CSSSelector {
    protected static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.rule(CSS_SELECTOR)
            .define(
                PEG.rule(CSS_GROUP),
                PEG.rule$ZeroOrMore(
                    PEG.rule(CSS_GROUP_DELIMITER),
                    PEG.rule(CSS_GROUP)))
            .pack($$ -> {
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                $$.add($0);
                for (int i = 0; i < $1.length(); i++) {
                    $$.add($1.get(i).get(1));
                }
                return $$;
            });
        PEG.rule(CSS_GROUP)
            .define(
                PEG.rule(ALL_SELECTOR),
                PEG.rule$ZeroOrMore(
                    PEG.rule(ALL_COMBINATOR),
                    PEG.rule(ALL_SELECTOR)))
            .pack($$ -> {
                // (ALL_SELECTOR (ALL_COMBINATOR ALL_SELECTOR)*)
                // ↓
                // (DESCENDANT_COMBINATOR (ALL_SELECTOR)) (ALL_COMBINATOR (ALL_SELECTOR))*)
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                PEGNode combinator = new PEGNode(DESCENDANT_COMBINATOR);
                for (int i = 0; i < $0.length(); i++) {
                    combinator.add($0.get(i));
                }
                $$.add(combinator);
                for (int i = 0; i < $1.length(); i++) {
                    PEGNode c = $1.get(i).get(0);
                    PEGNode s = $1.get(i).get(1);
                    c.clear();
                    for (int j = 0; j < s.length(); j++) {
                        c.add(s.get(j));
                    }
                    $$.add(c);
                }
                return $$;
            });
        PEG.rule(CSS_GROUP_DELIMITER)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$Literal(","),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .pack($$ -> {
                return $$.get(1);
            });
        PEG.rule(ALL_SELECTOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(UNIVERSAL_SELECTOR),
                    PEG.rule(ELEMENT_SELECTOR),
                    PEG.rule(CLASS_SELECTOR),
                    PEG.rule(ID_SELECTOR),
                    PEG.rule(ATTR_SELECTOR)),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(CLASS_SELECTOR),
                        PEG.rule(ID_SELECTOR),
                        PEG.rule(ATTR_SELECTOR)
                    )))
            .pack($$ -> {
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                $$.add($0);
                for (int i = 0; i < $1.length(); i++) {
                    $$.add($1.get(i).get(0));
                }
                return $$;
            });
        PEG.rule(UNIVERSAL_SELECTOR)
            .define(
                PEG.rule$Literal("*"))
            .pack($$ -> {
                PEGNode $0 = $$.get(0);
                $$.clear();
                $$.setValue($0.getValue());
                return $$;
            });
        PEG.rule(ELEMENT_SELECTOR)
            .define(
                PEG.rule$Class("a-zA-Z"),
                PEG.rule$ZeroOrMore(
                    PEG.rule$Class("a-zA-Z0-9")))
            .pack($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue().toString());
                PEGNode $1 = $$.get(1);
                //PEGNode $2 = $$.get(2);
                for (int i = 0; i < $1.length(); i++) {
                    sb.append($1.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
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
                PEGNode $2 = $$.get(2);
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
                PEGNode $2 = $$.get(2);
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
                PEGNode $1 = $$.get(1);
                PEGNode $2 = $$.get(2);
                PEGNode $3 = $$.get(3);
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
                PEGNode $1 = $$.get(1);
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
                PEGNode $0 = $$.get(0);
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
                        PEGNode $1 = $$.get(1);
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
                        PEGNode $1 = $$.get(1);
                        for (int i = 0; i < $1.length(); i++) {
                            sb.append($1.get(i).get(0).getValue());
                        }
                        $$.clear();
                        $$.setValue(sb.toString());
                        return $$;
                    })))
                .pack($$ -> {
                    PEGNode $0 = $$.get(0);
                    $$.clear();
                    $$.setValue($0.getValue());
                    return $$;
                });
        PEG.rule(ALL_COMBINATOR)
            .define(
                PEG.rule$Choice(
                    PEG.rule(CHILD_COMBINATOR),
                    PEG.rule(SIBLING_COMBINATOR),
                    PEG.rule(ADJACENT_COMBINATOR),
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
                PEGNode $1 = $$.get(1);
                $1.setType(CHILD_COMBINATOR);
                return $1;
            });
        PEG.rule(DESCENDANT_COMBINATOR)
            .define(
                PEG.rule$OneOrMore(
                    PEG.rule$Literal(" ")))
            .pack($$ -> {
                PEGNode $0 = $$.get(0).get(0);
                $0.setType(DESCENDANT_COMBINATOR);
                return $0;
            });
        PEG.rule(SIBLING_COMBINATOR)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$Literal("~"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .pack($$ -> {
                PEGNode $1 = $$.get(1);
                $1.setType(SIBLING_COMBINATOR);
                return $1;
            });
        PEG.rule(ADJACENT_COMBINATOR)
            .define(
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
                PEG.rule$Literal("+"),
                PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .pack($$ -> {
                PEGNode $1 = $$.get(1);
                $1.setType(ADJACENT_COMBINATOR);
                return $1;
            });
    }

    public static List<List<CSSSelector>> parse(String selector) throws CSSException {
        List<List<CSSSelector>> resultList = new ArrayList<>();
        PEGNode node = parseCSS(selector);
        for (int i = 0; i < node.length(); i++) {
            PEGNode groupNode = node.get(i);
            List<CSSSelector> groupList = new ArrayList<>();
            for (int j = 0; j < groupNode.length(); j++) {
                PEGNode combNode = groupNode.get(j);
                CSSSelector cssSelector = new CSSSelector();

                for (int k = 0; k < combNode.length(); k++) {
                    PEGNode mainNode = combNode.get(k);
                    switch ((CSSTokens)mainNode.getType()) {
                    case UNIVERSAL_SELECTOR:
                        cssSelector.addSelector(new UniversalSelector());
                        break;
                    case ELEMENT_SELECTOR:
                        cssSelector.addSelector(new ElementSelector(mainNode.getValue()));
                        break;
                    case CLASS_SELECTOR:
                        cssSelector.addSelector(new ClassSelector(mainNode.getValue()));
                        break;
                    case ID_SELECTOR:
                        cssSelector.addSelector(new IdSelector(mainNode.getValue()));
                        break;
                    case ATTR_SELECTOR:
                        cssSelector.addSelector(new AttrSelector(mainNode));
                        break;
                    default:
                        throw new CSSException(CSSMessages.CSS_0001, node.getType());
                    }
                }
                
                switch ((CSSTokens)combNode.getType()) {
                case DESCENDANT_COMBINATOR:
                    cssSelector.setCombinator(CombinatorTypes.DESCENDANT);
                    break;
                case CHILD_COMBINATOR:
                    cssSelector.setCombinator(CombinatorTypes.CHILD);
                    break;
                case ADJACENT_COMBINATOR:
                    cssSelector.setCombinator(CombinatorTypes.ADJACENT);
                    break;
                case SIBLING_COMBINATOR:
                    cssSelector.setCombinator(CombinatorTypes.SIBLING);
                    break;
                default:
                    throw new CSSException(CSSMessages.CSS_0001, combNode.getType());
                }

                groupList.add(cssSelector);
            }
            resultList.add(groupList);
        }
        return resultList;
    }

    protected static PEGNode parseCSS(String selector) throws CSSException {
        BacktrackReader reader = new BacktrackReader(selector);
        PEGNode result;
        try {
            result = PEG.rule(CSS_SELECTOR).accept(reader);
        } catch (UnmatchException e) {
            throw new CSSException(e);
        }

        if (reader.available() > 0) {
            StringBuffer sb = new StringBuffer();
            int c = 0;
            while ((c = reader.read()) > 0) {
                sb.append((char)c);
            }
            throw new CSSException(CSSMessages.CSS_9999, sb.toString());
        }
        return result;
    }
    
    private CombinatorTypes combinator;
    private List<CSSSelector> selectors;

    protected CSSSelector() {
        selectors = new ArrayList<>();
    }
    
    public CombinatorTypes getCombinator() {
        return combinator;
    }
    
    public void setCombinator(CombinatorTypes combinator) {
        this.combinator = combinator;
    }

    public List<CSSSelector> getSelectors() {
        return selectors;
    }

    public void addSelector(CSSSelector selector) {
        this.selectors.add(selector);
    }

    public boolean match(Element element) {
        for (CSSSelector child : getSelectors()) {
            if (!child.match(element)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("(" + getCombinator());
        for (CSSSelector child : selectors) {
            sb.append(" " + child.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public static class UniversalSelector extends CSSSelector {
        public UniversalSelector() {
        }

        public boolean match(Element element) {
            return super.match(element);
        }
        
        public String toString() {
            return "(*)";
        }
    }

    public static class ElementSelector extends CSSSelector {
        private String name;

        public ElementSelector(String name) {
            this.name = name;
        }
        
        public boolean match(Element element) {
            if (!name.equals(element.getTagName())) {
                return false;
            }
            return super.match(element);
        }

        public String toString() {
            return "(ELEMENT " + name + ")";
        }
    }

    public static class ClassSelector extends CSSSelector {
        private String clazz;

        public ClassSelector(String clazz) {
            this.clazz = clazz;
        }
        
        public boolean match(Element element) {
            String classes = " " + element.getAttr("class") + " ";
            if (!classes.contains(" " + clazz + " ")) {
                return false;
            }
            return super.match(element);
        }

        public String toString() {
            return "(CLASST " + clazz + ")";
        }
    }
    
    public static class IdSelector extends CSSSelector {
        private String id;

        public IdSelector(String id) {
            this.id = id;
        }
        
        public boolean match(Element element) {
            String id = element.getAttr("id");
            if (!this.id.equals(id)) {
                return false;
            }
            return super.match(element);
        }
        
        public String toString() {
            return "(ID " + id + ")";
        }
    }

    public static class AttrSelector extends CSSSelector {
        private String attrName;
        private String attrValue;
        private String comparator;
        private boolean ignoreCase = false;
        private boolean useExpr = false;

        public AttrSelector(PEGNode node) {
            attrName = node.get(0).getValue();
            if (node.length() < 4) {
                useExpr = true;
                attrValue = node.get(2).getValue();
                comparator = node.get(1).getValue();
            } else if ("i".equals(node.get(3).getValue())) {
                ignoreCase = true;
            }
        }
        @Override
        public boolean match (Element element) {
            if (!useExpr) {
                if (element.getAttrs().containsKey(attrName)) {
                    return true;
                } else {
                    return false;
                }
            }

            String value = element.getAttr(attrName);
            switch (comparator) {
            case "=":
                return Objects.equals(attrValue, value);
            case "~=":
                return (" " + attrValue + " ").contains(" " + value + " ");
            case "|=":
                return (value == null ? "" : value).startsWith(attrValue + "-");
            case "^=":
                return (value == null ? "" : value).startsWith(attrValue);
            case "$=":
                return (value == null ? "" : value).endsWith(attrValue);
            case "*=":
                return (value == null ? "" : value).contains(attrValue);
            default:
                throw new RuntimeException(comparator + " is not valid comparator.");
            }
        }
        
        public String toString() {
            return "(ATTR " + attrName + ", " + comparator + ", " + attrValue + ")";
        }
    }

    public enum CSSTokens implements RuleTypes {
        CSS_SELECTOR, ALL_SELECTOR, UNIVERSAL_SELECTOR, ELEMENT_SELECTOR,
        CLASS_SELECTOR, ID_SELECTOR, ATTR_SELECTOR, ATTR_NAME, ATTR_VALUE,
        ATTR_COMPARATOR, ALL_COMBINATOR, CHILD_COMBINATOR, DESCENDANT_COMBINATOR,
        ADJACENT_COMBINATOR, SIBLING_COMBINATOR, CSS_GROUP, CSS_GROUP_DELIMITER
        ;
    }
    
    public enum CombinatorTypes {
        DESCENDANT, CHILD, SIBLING, ADJACENT;
    }
    
    public static class CSSException extends Exception {
        private static final long serialVersionUID = -2932706850240045484L;

        public CSSException(Throwable th) {
            super(th);
        }

        public CSSException(ActionMessages msg, Object...params) {
            super(msg.getMessage(params));
        }
    }
    
    public static enum CSSMessages implements ActionMessages {
        @Message(ja = "{0}はここでは使えません")
        CSS_0001,
        @Message(ja = "構文エラーです:{0}")
        CSS_9999
        ;

        private Map<String,MessageFormat> bundle;

        private CSSMessages() {
            bundle = ActionMessages.Util.bundle(this);
        }

        @Override
        public Map<String, MessageFormat> getBundle() {
            return bundle;
        }

        @Override
        public String getCode() {
            return ActionMessages.Util.getCode(this);
        }

        @Override
        public String getMessage(Object... params) {
            return ActionMessages.Util.getMessage(this, params);
        }

        @Override
        public String getMessage(Locale locale, Object... params) {
            return ActionMessages.Util.getMessage(this, params);
        }
        
    }
}
