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
package com.shorindo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Rule rule(final RuleTypes ruleType) {
        if (!ruleMap.containsKey(ruleType)) {
            ruleMap.put(ruleType, new Rule(ruleType) {
                @Override
                public PEGNode accept(BacktrackReader is) throws UnmatchException {
                    PEGNode $$ = new PEGNode(ruleType);
                    int curr = is.position();
                    $$.setType(ruleType);
                    for (Rule rule : childRules) {
                        PEGNode childNode = rule.accept(is);
                        $$.add(childNode);
                    }
                    $$.setSource(is.subString(curr));
                    LOG.trace("rule({0})[{1}] accept <- {2}",
                        ruleType, curr, $$.getSource());
                    return action.apply($$);
                }
            });
        }
        return ruleMap.get(ruleType);
    }

    public Rule rule$Any() {
        return new Rule(Types.ANY) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                int curr = is.position();
                int c = is.read();
                if (c == -1) {
                    throw new UnmatchException();
                } else {
                    LOG.trace("rule$Any()[{0}] accept <- {1}", curr, (char)c);
                    PEGNode $$ = new PEGNode(Types.ANY);
                    $$.setSource(String.valueOf((char)c));
                    $$.setValue(String.valueOf((char)c));
                    return action.apply($$);
                }
            }
        };
    }

    // FIXME カーソルが移動しないので、EOF判定のときくらいしか使えない
    public Rule rule$Not(final Rule rule) {
        return new Rule(Types.NOT) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                int pos = is.position();
                try {
                    rule.accept(is);
                } catch (UnmatchException e) {
                    LOG.trace("rule$Not()[{0}] accept <- {1}", pos, rule);
                    PEGNode $$ = new PEGNode(Types.NOT);
                    $$.setSource("");
                    return action.apply($$);
                }
                is.reset(pos);
                throw new UnmatchException();
            }
        };
    }

    public Rule rule$Literal(final String literal) {
        return new Rule(Types.LITERAL) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                int mark = is.position();
                for (int i = 0; i < literal.length(); i++) {
                    char c = literal.charAt(i);
                    int r = is.read();
                    if (c != r) {
                        is.reset(mark);
                        throw new UnmatchException();
                    }
                }
                LOG.trace("rule$Literal()[{0}] accept <- {1}", mark, escape(literal));
                PEGNode $$ = new PEGNode(Types.LITERAL);
                $$.setSource(literal);
                $$.setValue(literal);
                return action.apply($$);
            }
        };
    }
    
    public Rule rule$Class(final String charClass) {
        return new Rule(Types.CLASS) {
            Pattern pattern = Pattern.compile("[" + charClass + "]");

            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                int curr = is.position();
                int c = is.read();
                if (c == -1) {
                    is.reset(curr);
                    throw new UnmatchException();
                }
                Matcher m = pattern.matcher(String.valueOf((char)c));
                if (m.matches()) {
                    LOG.trace("rule$Class({0})[{1}] accept <- {2}",
                        escape(charClass), curr, escape(String.valueOf((char)c)));
                    PEGNode $$ = new PEGNode(Types.CLASS);
                    $$.setSource(String.valueOf((char)c));
                    $$.setValue(String.valueOf((char)c));
                    return action.apply($$);
                } else {
                    is.reset(curr);
                    throw new UnmatchException();
                }
            }
        };
    }
    public Rule rule$ZeroOrMore(final Rule...rules) {
        return new Rule(Types.ZERO_OR_MORE) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                PEGNode $$ = new PEGNode(Types.ZERO_OR_MORE);
                int start = is.position();
                while (true) {
                    PEGNode seq = new PEGNode(Types.SEQUENCE);
                    int curr = is.position();
                    try {
                        for (Rule child : rules) {
                            seq.add(child.accept(is));
                        }
                    } catch (UnmatchException e) {
                        is.reset(curr);
                        break;
                    } finally {
                        if (seq.length() == rules.length) {
                            $$.add(seq);
                        }
                    }
                }
                $$.setSource(is.subString(start));
                LOG.trace("rule$zeroOrMore accept <- " + $$.getSource());
                return action.apply($$);
            }
        };
    }
    public Rule rule$OneOrMore(final Rule...rules) {
        return new Rule(Types.ONE_OR_MORE) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                int count = 0;
                PEGNode $$ = new PEGNode(Types.ONE_OR_MORE);
                int start = is.position();
                while (true) {
                    PEGNode seq = new PEGNode(Types.SEQUENCE);
                    int curr = is.position();
                    try {
                        for (Rule child : rules) {
                            seq.add(child.accept(is));
                        }
                        count++;
                    } catch (UnmatchException e) {
                        is.reset(curr);
                        break;
                    } finally {
                        if (seq.length() == rules.length) {
                            $$.add(seq);
                        }
                    }
                }
                if (count > 0) {
                    $$.setSource(is.subString(start));
                    LOG.trace("rule$zeroOrMore accept <- " + $$.getSource());
                    return action.apply($$);
                } else {
                    throw new UnmatchException();
                }
            }
        };
    }
    public Rule rule$Sequence(final Rule...rules) {
        return new Rule(Types.SEQUENCE) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                //LOG.trace(toString());
                int curr = is.position();
                PEGNode $$ = new PEGNode(Types.SEQUENCE);
                try {
                    for (Rule child : rules) {
                        PEGNode $n = (PEGNode)child.accept(is);
                        $$.add($n);
                    }
                } catch (UnmatchException e) {
                    is.reset(curr);
                    throw e;
                }
                $$.setSource(is.subString(curr));
                LOG.trace("rule$Sequence accept <- " + $$.getSource());
                return action.apply($$);
            }
        };
    }
    
    public Rule rule$Choice(final Rule...rules) {
        return new Rule(Types.CHOICE) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                //LOG.trace(toString());
                int curr = is.position();
                for (Rule child : rules) {
                    try {
                        PEGNode $$ = child.accept(is);
                        $$.setSource(is.subString(curr));
                        LOG.trace("rule$Choice accept <- " + $$.getSource());
                        return action.apply($$);
                    } catch (UnmatchException e) {
                        is.reset(curr);
                    }
                }
                throw new UnmatchException();
            }
        };
    }

    public Rule rule$Optional(final Rule...rules) {
        return new Rule(Types.OPTIONAL) {
            @Override
            public PEGNode accept(BacktrackReader is) throws UnmatchException {
                for (Rule child : rules) {
                    childRules.add(child);
                }

                int curr = is.position();
                PEGNode $$ = new PEGNode(Types.OPTIONAL);
                try {
                    for (Rule child : rules) {
                        $$.add(child.accept(is));
                    }
                    LOG.trace("rule$optional() <= " + toString(rules));
                } catch (UnmatchException e) {
                    $$.clear();
                    is.reset(curr);
                }
                $$.setSource(is.subString(curr));
                LOG.trace("rule$Optional accept <- " + $$.getSource());
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
        public RuleTypes type;
        protected Function<PEGNode,PEGNode> action;
        protected List<Rule> childRules = new ArrayList<Rule>();

        public Rule(RuleTypes type) {
            this.type = type;
            this.action = new Function<PEGNode,PEGNode>() {
                @Override
                public PEGNode apply(PEGNode $$) {
                    return $$;
                }
            };
        }

        public abstract PEGNode accept(BacktrackReader is)
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
        
        public Rule pack(Function<PEGNode,PEGNode> action) {
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

    private static String escape(String text) {
        return text.replaceAll("\t", "\\\\t")
            .replaceAll("\r", "\\\\r")
            .replaceAll("\n", "\\\\n");
    }

    /**
     * 
     */
    public static class PEGNode {
        private RuleTypes type;
        private String source;
        private String value;
        private boolean empty = false;
        private List<PEGNode> childList = new ArrayList<PEGNode>();

        public PEGNode(RuleTypes type) {
            this.type = type;
        }
        public RuleTypes getType() {
            return type;
        }
        public void setType(RuleTypes type) {
            this.type = type;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public boolean isEmpty() {
            return empty;
        }
        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
        public void add(PEGNode child) {
            childList.add(child);
        }
        public PEGNode get(int i) {
            return childList.get(i);
        }
        public int length() {
            return childList.size();
        }
        public void clear() {
            childList.clear();
        }
        public void setSource(String source) {
            this.source = source;
        }
        public String getSource() {
            return escape(source);
//            StringBuilder sb = new StringBuilder();
//            if (value != null) {
//                sb.append(value);
//            }
//            for (PEGNode child : childList) {
//                sb.append(child.getSource());
//            }
//            return sb.toString();
        }
        public String toString() {
            if (this.isEmpty()) {
                return "";
            }
            StringBuffer sb = new StringBuffer();
            sb.append("(");
            sb.append(getType());
            for (PEGNode child : childList) {
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
        private String source;
        private List<Character> buffer;
        private int position = 0;

        public BacktrackReader(String text) {
            source = text;
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
        
        public String subString(int start) {
            return source.substring(start, this.position);
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
