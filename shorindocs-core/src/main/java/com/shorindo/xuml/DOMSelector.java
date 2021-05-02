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

import static com.shorindo.xuml.DOMSelector.CSSTokens.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.action.ActionMessages;
import com.shorindo.tools.PEGCombinator;
import com.shorindo.tools.PEGCombinator.PEGContext;
import com.shorindo.tools.PEGCombinator.PEGException;
import com.shorindo.tools.PEGCombinator.PEGNode;
//import com.shorindo.xuml.DOMBuilder.Element;

/**
 * 
 */
public class DOMSelector {
    private static final ActionLogger LOG = ActionLogger.getLogger(DOMSelector.class);
    protected static PEGCombinator PEG = new PEGCombinator();
    static {
        PEG.define(CSS_SELECTOR,
            PEG.rule(CSS_GROUP),
            PEG.rule$ZeroOrMore(
                PEG.rule(CSS_GROUP_DELIMITER),
                PEG.rule(CSS_GROUP)))
            .action($$ -> {
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                $$.add($0);
                for (int i = 0; i < $1.length(); i++) {
                    $$.add($1.get(i).get(1));
                }
                return $$;
            });
        PEG.define(CSS_GROUP,
            PEG.rule(ALL_SELECTOR),
            PEG.rule$ZeroOrMore(
                PEG.rule(ALL_COMBINATOR),
                PEG.rule(ALL_SELECTOR)))
            .action($$ -> {
                // (ALL_SELECTOR (ALL_COMBINATOR ALL_SELECTOR)*)
                // ↓
                // (DESCENDANT_COMBINATOR (ALL_SELECTOR)) (ALL_COMBINATOR (ALL_SELECTOR))*)
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                PEGNode combinator = new PEGNode($$.getContext(), DESCENDANT_COMBINATOR);
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
        PEG.define(CSS_GROUP_DELIMITER,
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
            PEG.rule$Literal(","),
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .action($$ -> {
                return $$.get(1);
            });
        PEG.define(ALL_SELECTOR,
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
                    PEG.rule(ATTR_SELECTOR))))
            .action($$ -> {
                PEGNode $0 = $$.get(0);
                PEGNode $1 = $$.get(1);
                $$.clear();
                $$.add($0);
                for (int i = 0; i < $1.length(); i++) {
                    $$.add($1.get(i).get(0));
                }
                return $$;
            });
        PEG.define(UNIVERSAL_SELECTOR,
            PEG.rule$Literal("*"))
            .action($$ -> {
                PEGNode $0 = $$.get(0);
                $$.clear();
                $$.setValue($0.getValue());
                return $$;
            });
        PEG.define(ELEMENT_SELECTOR,
            PEG.rule$Class("a-zA-Z"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z0-9")))
            .action($$ -> {
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
        PEG.define(CLASS_SELECTOR,
            PEG.rule$Literal("."),
            PEG.rule$Class("a-zA-Z"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z0-9\\-")))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().toString());
                PEGNode $2 = $$.get(2);
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(ID_SELECTOR,
            PEG.rule$Literal("#"),
            PEG.rule$Class("a-zA-Z"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z0-9\\-")))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(1).getValue().toString());
                PEGNode $2 = $$.get(2);
                for (int i = 0; i < $2.length(); i++) {
                    sb.append($2.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(ATTR_SELECTOR,
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
            PEG.rule$Literal("]"))
            .action($$ -> {
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
        PEG.define(ATTR_NAME,
            PEG.rule$Class("a-zA-Z"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Class("a-zA-Z\\-")))
            .action($$ -> {
                StringBuffer sb = new StringBuffer($$.get(0).getValue().toString());
                PEGNode $1 = $$.get(1);
                for (int i = 0; i < $1.length(); i++) {
                    sb.append($1.get(i).get(0).getValue().toString());
                }
                $$.clear();
                $$.setValue(sb.toString());
                return $$;
            });
        PEG.define(ATTR_COMPARATOR,
            PEG.rule$Choice(
                PEG.rule$Literal("="),
                PEG.rule$Literal("~="),
                PEG.rule$Literal("|="),
                PEG.rule$Literal("^="),
                PEG.rule$Literal("$="),
                PEG.rule$Literal("*=")))
            .action($$ -> {
                PEGNode $0 = $$.get(0);
                $$.clear();
                $$.setValue($0.getValue());
                return $$;
            });
        PEG.define(ATTR_VALUE,
            PEG.rule$Choice(
                PEG.rule$Sequence(
                    PEG.rule$Literal("\""),
                    PEG.rule$ZeroOrMore(PEG.rule$Class("^\"")),
                    PEG.rule$Literal("\""))
                    .action($$ -> {
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
                    .action($$ -> {
                        StringBuffer sb = new StringBuffer();
                        PEGNode $1 = $$.get(1);
                        for (int i = 0; i < $1.length(); i++) {
                            sb.append($1.get(i).get(0).getValue());
                        }
                        $$.clear();
                        $$.setValue(sb.toString());
                        return $$;
                    })))
                .action($$ -> {
                    PEGNode $0 = $$.get(0);
                    $$.clear();
                    $$.setValue($0.getValue());
                    return $$;
                });
        PEG.define(ALL_COMBINATOR,
            PEG.rule$Choice(
                PEG.rule(CHILD_COMBINATOR),
                PEG.rule(SIBLING_COMBINATOR),
                PEG.rule(ADJACENT_COMBINATOR),
                PEG.rule(DESCENDANT_COMBINATOR)))
            .action($$ -> {
                return $$.get(0);
            });
        PEG.define(CHILD_COMBINATOR,
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
            PEG.rule$Literal(">"),
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .action($$ -> {
                PEGNode $1 = $$.get(1);
                $1.setType(CHILD_COMBINATOR);
                return $1;
            });
        PEG.define(DESCENDANT_COMBINATOR,
            PEG.rule$OneOrMore(
                PEG.rule$Literal(" ")))
            .action($$ -> {
                PEGNode $0 = $$.get(0).get(0);
                $0.setType(DESCENDANT_COMBINATOR);
                return $0;
            });
        PEG.define(SIBLING_COMBINATOR,
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
            PEG.rule$Literal("~"),
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .action($$ -> {
                PEGNode $1 = $$.get(1);
                $1.setType(SIBLING_COMBINATOR);
                return $1;
            });
        PEG.define(ADJACENT_COMBINATOR,
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")),
            PEG.rule$Literal("+"),
            PEG.rule$ZeroOrMore(PEG.rule$Literal(" ")))
            .action($$ -> {
                PEGNode $1 = $$.get(1);
                $1.setType(ADJACENT_COMBINATOR);
                return $1;
            });
    }

    public List<Node> find(Node node, String text) {
        List<Node> resultList = new ArrayList<>();
        //long st = System.currentTimeMillis();
        try {
            List<List<DOMSelector>> list = parse(text);
            for (List<DOMSelector> groupList : list) {
                List<Node> filteredResult = new ArrayList<>();
                filteredResult.add(node);
                for (DOMSelector selector : groupList) {
                    filteredResult = filteredResult.stream()
                        .flatMap(n -> {
                            return find(n, selector).stream();
                        })
                        .collect(Collectors.toList());
                }
                resultList.addAll(filteredResult);
            }
        } catch (DOMSelectorException e) {
            LOG.error(e.getMessage(), e, text);
        } finally {
            //LOG.debug("findByCssSelector({0}) => count:{1} time:{2}ms",
            //    text, resultList.size(), (System.currentTimeMillis() - st));
        }
        return resultList;
    }

    private List<Node> find(Node node, DOMSelector selector) {
        List<Node> result = new ArrayList<>();
        switch (selector.getCombinator()) {
        case DESCENDANT:
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node child = node.getChildNodes().item(i);
                if (selector.match(child)) {
                    result.add(child);
                }
                result.addAll(find(child, selector));
            }
            break;
        case CHILD:
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node child = node.getChildNodes().item(i);
                if (selector.match(child)) {
                    result.add(child);
                }
            }
            break;
        case SIBLING:
            Node parent = node.getParentNode();
            for (int i = 0; i < parent.getChildNodes().getLength() - 1; i++) {
                Node curr = parent.getChildNodes().item(i);
                Node next = parent.getChildNodes().item(i + 1);
                if (curr == this) {
                    OUT:for (int j = i + 1; j < parent.getChildNodes().getLength(); j++) {
                        next = parent.getChildNodes().item(j);
                        if (selector.match(next)) {
                            result.add(next);
                            break OUT;
                        }
                    }
                }
            }
            break;
        case ADJACENT:
            parent = node.getParentNode();
            for (int i = 0; i < parent.getChildNodes().getLength() - 1; i++) {
                Node curr = parent.getChildNodes().item(i);
                Node next = parent.getChildNodes().item(i + 1);
                if (curr == this && selector.match(next)) {
                    result.add(next);
                    break;
                }
            }
            break;
        }
        return result;
    }

    public static List<List<DOMSelector>> parse(String selector) throws DOMSelectorException {
        List<List<DOMSelector>> resultList = new ArrayList<>();
        PEGNode node = parseCSS(selector);
        for (int i = 0; i < node.length(); i++) {
            PEGNode groupNode = node.get(i);
            List<DOMSelector> groupList = new ArrayList<>();
            for (int j = 0; j < groupNode.length(); j++) {
                PEGNode combNode = groupNode.get(j);
                DOMSelector domSelector = new DOMSelector();

                for (int k = 0; k < combNode.length(); k++) {
                    PEGNode mainNode = combNode.get(k);
                    switch ((CSSTokens)mainNode.getType()) {
                    case UNIVERSAL_SELECTOR:
                        domSelector.addSelector(new UniversalSelector());
                        break;
                    case ELEMENT_SELECTOR:
                        domSelector.addSelector(new ElementSelector(mainNode.getValue()));
                        break;
                    case CLASS_SELECTOR:
                        domSelector.addSelector(new ClassSelector(mainNode.getValue()));
                        break;
                    case ID_SELECTOR:
                        domSelector.addSelector(new IdSelector(mainNode.getValue()));
                        break;
                    case ATTR_SELECTOR:
                        domSelector.addSelector(new AttrSelector(mainNode));
                        break;
                    default:
                        throw new DOMSelectorException(DOMMessages.CSS_0001, node.getType());
                    }
                }

                switch ((CSSTokens)combNode.getType()) {
                case DESCENDANT_COMBINATOR:
                    domSelector.setCombinator(CombinatorTypes.DESCENDANT);
                    break;
                case CHILD_COMBINATOR:
                    domSelector.setCombinator(CombinatorTypes.CHILD);
                    break;
                case ADJACENT_COMBINATOR:
                    domSelector.setCombinator(CombinatorTypes.ADJACENT);
                    break;
                case SIBLING_COMBINATOR:
                    domSelector.setCombinator(CombinatorTypes.SIBLING);
                    break;
                default:
                    throw new DOMSelectorException(DOMMessages.CSS_0001, combNode.getType());
                }

                groupList.add(domSelector);
            }
            resultList.add(groupList);
        }
        return resultList;
    }

    protected static PEGNode parseCSS(String selector) throws DOMSelectorException {
        PEGContext reader = PEG.createContext(selector);
        PEGNode result;
        try {
            result = PEG.rule(CSS_SELECTOR).accept(reader);
        } catch (PEGException e) {
            throw new DOMSelectorException(e);
        }

        if (reader.available() > 0) {
            StringBuffer sb = new StringBuffer();
            int c = 0;
            while ((c = reader.read()) > 0) {
                sb.append((char)c);
            }
            throw new DOMSelectorException(DOMMessages.CSS_9999, sb.toString());
        }
        return result;
    }
    
    private CombinatorTypes combinator;
    private List<DOMSelector> selectors;

    protected DOMSelector() {
        selectors = new ArrayList<>();
    }
    
    public CombinatorTypes getCombinator() {
        return combinator;
    }

    public void setCombinator(CombinatorTypes combinator) {
        this.combinator = combinator;
    }

    public List<DOMSelector> getSelectors() {
        return selectors;
    }

    public void addSelector(DOMSelector selector) {
        this.selectors.add(selector);
    }

    public boolean match(Node node) {
        for (DOMSelector child : getSelectors()) {
            if (!child.match(node)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("(" + getCombinator());
        for (DOMSelector child : selectors) {
            sb.append(" " + child.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public static class UniversalSelector extends DOMSelector {
        public UniversalSelector() {
        }

        public boolean match(Element element) {
            return super.match(element);
        }
        
        public String toString() {
            return "(*)";
        }
    }

    public static class ElementSelector extends DOMSelector {
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

    public static class ClassSelector extends DOMSelector {
        private String clazz;

        public ClassSelector(String clazz) {
            this.clazz = clazz;
        }
        
        public boolean match(Element element) {
            String classes = " " + element.getAttribute("class") + " ";
            if (!classes.contains(" " + clazz + " ")) {
                return false;
            }
            return super.match(element);
        }

        public String toString() {
            return "(CLASST " + clazz + ")";
        }
    }

    public static class IdSelector extends DOMSelector {
        private String id;

        public IdSelector(String id) {
            this.id = id;
        }

        public boolean match(Element element) {
            String id = element.getAttribute("id");
            if (!this.id.equals(id)) {
                return false;
            }
            return super.match(element);
        }

        public String toString() {
            return "(ID " + id + ")";
        }
    }

    public static class AttrSelector extends DOMSelector {
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
        public boolean match (Node node) {
            if (!useExpr) {
                if ("".equals(getAttribute(node, attrName))) {
                    return false;
                } else {
                    return true;
                }
            }

            String value = getAttribute(node, attrName);
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

        private String getAttribute(Node node, String attrName) {
            Node attrValue = node.getAttributes().getNamedItem(attrName);
            if (attrValue == null) {
                return null;
            } else {
                return attrValue.getNodeValue();
            }
        }

        public String toString() {
            return "(ATTR " + attrName + ", " + comparator + ", " + attrValue + ")";
        }
    }

    public enum CSSTokens implements com.shorindo.tools.PEGCombinator.RuleTypes {
        CSS_SELECTOR, ALL_SELECTOR, UNIVERSAL_SELECTOR, ELEMENT_SELECTOR,
        CLASS_SELECTOR, ID_SELECTOR, ATTR_SELECTOR, ATTR_NAME, ATTR_VALUE,
        ATTR_COMPARATOR, ALL_COMBINATOR, CHILD_COMBINATOR, DESCENDANT_COMBINATOR,
        ADJACENT_COMBINATOR, SIBLING_COMBINATOR, CSS_GROUP, CSS_GROUP_DELIMITER
        ;
        
        public String getName() {
            return name();
        }
    }
    
    public enum CombinatorTypes {
        DESCENDANT, CHILD, SIBLING, ADJACENT;
    }
    
    public static class DOMSelectorException extends Exception {
        private static final long serialVersionUID = -2932706850240045484L;

        public DOMSelectorException(Throwable th) {
            super(th);
        }

        public DOMSelectorException(ActionMessages msg, Object...params) {
            super(msg.getMessage(params));
        }
    }
    
    public static enum DOMMessages implements ActionMessages {
        @Message(lang="ja", content="{0}はここでは使えません")
        CSS_0001,
        @Message(lang="ja", content="構文エラーです:{0}")
        CSS_9999
        ;
    }
}
