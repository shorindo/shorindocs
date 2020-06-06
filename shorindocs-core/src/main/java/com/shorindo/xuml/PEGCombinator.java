/*
 * Copyright 2017-2020 Shorindo, Inc.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shorindo.docs.action.ActionLogger;

/**
 * PEGによる構文解析器を生成する
 */
public class PEGCombinator {
    private static ActionLogger LOG = ActionLogger.getLogger(PEGCombinator.class);
    private Map<RuleTypes,Rule> ruleMap;

    /**
     * 
     */
    public PEGCombinator() {
        ruleMap = new HashMap<RuleTypes,Rule>();
    }

    protected Rule rule(final RuleTypes ruleType) {
        if (!ruleMap.containsKey(ruleType)) {
            ruleMap.put(ruleType, new Rule(ruleType) {
                @Override
                public Node accept(BacktrackReader is) throws UnmatchException {
                    Node $$ = new Node(ruleType);
                    $$.setType(ruleType);
                    for (Rule rule : childRules) {
                        $$.add(rule.accept(is));
                    }
                    LOG.trace("rule[" + ruleType + "]");
                    return action.apply($$);
                }
            });
        }
        return ruleMap.get(ruleType);
    }

    protected Rule rule$Any() {
        return new Rule(Types.ANY) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                int c = is.read();
                if (c == -1) {
                    throw new UnmatchException();
                } else {
                    LOG.trace("rule$any() <= " + (char)c);
                    Node $$ = new Node(Types.ANY);
                    $$.setValue(String.valueOf((char)c));
                    return action.apply($$);
                }
            }
        };
    }

    protected Rule rule$Not(final Rule rule) {
        return new Rule(Types.NOT) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                int pos = is.position();
                try {
                    rule.accept(is);
                } catch (UnmatchException e) {
                    LOG.trace("rule$not() <= " + rule);
                    Node $$ = new Node(Types.NOT);
                    return action.apply($$);
                }
                is.reset(pos);
                throw new UnmatchException();
            }
        };
    }

    protected Rule rule$Literal(final String literal) {
        return new Rule(Types.LITERAL) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                int mark = is.position();
                for (int i = 0; i < literal.length(); i++) {
                    char c = literal.charAt(i);
                    int r = is.read();
                    if (c != r) {
                        is.reset(mark);
                        throw new UnmatchException();
                    }
                }
                LOG.trace("rule$literal() <= " + literal);
                Node $$ = new Node(Types.LITERAL);
                $$.setValue(literal);
                return action.apply($$);
            }
        };
    }
    
    protected Rule rule$Class(final String charClass) {
        return new Rule(Types.CLASS) {
            Pattern pattern = Pattern.compile("[" + charClass + "]");

            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                int curr = is.position();
                int c = is.read();
                Matcher m = pattern.matcher(String.valueOf((char)c));
                if (m.matches()) {
                    LOG.trace("rule$class(" + (char)c + ") <= " + charClass);
                    Node $$ = new Node(Types.CLASS);
                    $$.setValue(String.valueOf((char)c));
                    return action.apply($$);
                } else {
                    is.reset(curr);
                    throw new UnmatchException();
                }
            }
        };
    }
    protected Rule rule$ZeroOrMore(final Rule...rules) {
        return new Rule(Types.ZERO_OR_MORE) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                Node $$ = new Node(Types.ZERO_OR_MORE);
                while (true) {
                    int curr = is.position();
                    try {
                        for (Rule child : rules) {
                            $$.add(child.accept(is));
                        }
                    } catch (UnmatchException e) {
                        is.reset(curr);
                        break;
                    }
                }
                LOG.trace("rule$zeroOrMore <= " + toString(rules));
                return action.apply($$);
            }
        };
    }
    protected Rule rule$OneOrMore(final Rule...rules) {
        return new Rule(Types.ONE_OR_MORE) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                int count = 0;
                Node $$ = new Node(Types.ONE_OR_MORE);
                while (true) {
                    int curr = is.position();
                    try {
                        for (Rule child : rules) {
                            $$.add(child.accept(is));
                        }
                        count++;
                    } catch (UnmatchException e) {
                        is.reset(curr);
                        break;
                    }
                }
                if (count > 0) {
                    LOG.trace("rule$zeroOrMore <= " + toString(rules));
                    return action.apply($$);
                } else {
                    throw new UnmatchException();
                }
            }
        };
    }
    protected Rule rule$Sequence(final Rule...rules) {
        return new Rule(Types.SEQUENCE) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                //LOG.trace(toString());
                int curr = is.position();
                Node $$ = new Node(Types.SEQUENCE);
                try {
                    for (Rule child : rules) {
                        Node $n = (Node)child.accept(is);
                        $$.add($n);
                    }
                } catch (UnmatchException e) {
                    is.reset(curr);
                    throw e;
                }
                LOG.trace("rule$sequence <= " + toString(rules));
                return action.apply($$);
            }
        };
    }
    
    protected Rule rule$Choice(final Rule...rules) {
        return new Rule(Types.CHOICE) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                //LOG.trace(toString());
                int curr = is.position();
                for (Rule child : rules) {
                    try {
                        LOG.trace("rule$choice <= " + toString(rules));
                        Node $$ = child.accept(is);
                        return action.apply($$);
                    } catch (UnmatchException e) {
                        is.reset(curr);
                    }
                }
                throw new UnmatchException();
            }
        };
    }

    protected Rule rule$Optional(final Rule...rules) {
        return new Rule(Types.OPTIONAL) {
            @Override
            public Node accept(BacktrackReader is) throws UnmatchException {
                for (Rule child : rules) {
                    childRules.add(child);
                }

                int curr = is.position();
                Node $$ = new Node(Types.OPTIONAL);
                try {
                    for (Rule child : rules) {
                        $$.add(child.accept(is));
                    }
                    LOG.trace("rule$optional() <= " + toString(rules));
                } catch (UnmatchException e) {
                    $$.clear();
                    is.reset(curr);
                }
                return action.apply($$);
            }
        };
    }

    @SuppressWarnings("serial")
    public static class UnmatchException extends Exception {
        public UnmatchException() {}
        public UnmatchException(String msg) { super(msg); }
        public UnmatchException(Exception e) { super(e); }
    }
    
    /**
     * 
     */
    public static abstract class Rule {
        protected RuleTypes type;
        protected Function<Node,Node> action;
        protected List<Rule> childRules = new ArrayList<Rule>();

        public Rule(RuleTypes type) {
            this.type = type;
            this.action = new Function<Node,Node>() {
                @Override
                public Node apply(Node $$) {
                    return $$;
                }
            };
        }

        public abstract Node accept(BacktrackReader is)
                throws UnmatchException;

        public Rule define(Rule...rules) {
            for (Rule child : rules) {
                childRules.add(child);
            }
            return this;
        }

        public Rule get(int i) {
            return childRules.get(i);
        }
        
        public Rule pack(Function<Node,Node> action) {
            this.action = action;
            return this;
        }

        public String toString(Rule...rules) {
            StringBuffer sb = new StringBuffer(type.name());
            String sep = "";
            sb.append("[");
            for (Rule rule : rules) {
                sb.append(sep + rule);
                sep = ",";
            }
            sb.append("]");
            return sb.toString();
        }

        public String toString() {
            return toString(childRules.toArray(new Rule[]{}));
        }
    }

    /**
     * 
     */
    public static class Node {
        private RuleTypes type;
        private Object value;
        private boolean empty = false;
        private List<Node> childList = new ArrayList<Node>();

        public Node(RuleTypes type) {
            this.type = type;
        }
        public RuleTypes getType() {
            return type;
        }
        public void setType(RuleTypes type) {
            this.type = type;
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
        public boolean isEmpty() {
            return empty;
        }
        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
        public void add(Node child) {
            childList.add(child);
        }
        public Node get(int i) {
            return childList.get(i);
        }
        public int length() {
            return childList.size();
        }
        public void clear() {
            childList.clear();
        }
        public String getSource() {
            StringBuilder sb = new StringBuilder();
            if (value != null) {
                sb.append(value);
            }
            for (Node child : childList) {
                sb.append(child.getSource());
            }
            return sb.toString();
        }
        public String toString() {
            if (this.isEmpty()) {
                return "";
            }
            StringBuffer sb = new StringBuffer();
            sb.append("(");
            sb.append(getType());
            for (Node child : childList) {
                sb.append(" " + child.toString());
            }
            sb.append(")");
            return sb.toString();
        }
    }

    /**
     * 
     */
    public static class BacktrackReader {
        private List<Character> buffer;
        private int position = 0;

        public BacktrackReader(String text) {
            buffer = new ArrayList<>();
            for (int i = 0; i < text.length(); i++) {
                buffer.add(text.charAt(i));
            }
        }

        public void reset(int position) {
            this.position = position;
        }

        public int position() {
            return position;
        }

        public void clear() {
            position = 0;
            buffer = new ArrayList<>();
        }

        public int read() {
            if (available() > 0) {
                return buffer.get(position++);
            } else {
                return -1;
            }
        }

        public int available() {
            return buffer.size() - position;
        }
    }
    
    private enum Types implements RuleTypes {
        ANY, NOT, LITERAL, CLASS, ZERO_OR_MORE, ONE_OR_MORE, SEQUENCE,
        CHOICE, OPTIONAL;
    }

    public interface RuleTypes {
        public String name();
    }
}
