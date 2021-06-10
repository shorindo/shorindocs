package com.shorindo.xuml;

import static com.shorindo.xuml.XumlMessages.*;
import static com.shorindo.xuml.XumlParser.XumlRules.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.tools.BeanUtil;
import com.shorindo.tools.BeanUtil.BeanNotFoundException;
import com.shorindo.tools.PEGCombinator;
import com.shorindo.tools.PEGCombinator.PEGContext;
import com.shorindo.tools.PEGCombinator.PEGException;
import com.shorindo.tools.PEGCombinator.PEGNode;
import com.shorindo.tools.PEGCombinator.Rule;
import com.shorindo.tools.PEGCombinator.RuleTypes;

public class XumlParser {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlParser.class);
    private static final PEGCombinator PEG = new PEGCombinator();
    private static final String PREFIX = "xuml:";
    private static final String TAG_XUML = PREFIX + "xuml";
    private static final String TAG_IMPORT = PREFIX + "import";
    private static final String TAG_TEMPLATE = PREFIX + "template";
    private static final String TAG_APPLY = PREFIX + "apply";
    private static final String TAG_SWITCH = PREFIX + "switch";
    private static final String TAG_CASE = PREFIX + "case";
    private static final String TAG_DEFAULT = PREFIX + "default";
    private static final String TAG_EACH = PREFIX + "each";
    private static final String TAG_LABEL = PREFIX + "label";
    private static final String TAG_TEXT = PREFIX + "text";

    static {
        PEG.define(XUML_XUML,
            createOpenTag(TAG_XUML),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_TEMPLATE),
                    PEG.rule(XUML_APPLY),
                    PEG.rule(XUML_IMPORT),
                    PEG.rule(XUML_SWITCH),
                    PEG.rule(XUML_EACH),
                    PEG.rule(XUML_LABEL),
                    PEG.rule(COMMENT),
                    PEG.rule(WS1),
                    PEG.rule(XUML_VARRIABLE),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_XUML));

        PEG.define(XUML_LABEL,
            createOpenTag(TAG_LABEL),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_TEXT),
                    PEG.rule(COMMENT),
                    PEG.rule(WS1))),
            createCloseTag(TAG_LABEL));

        PEG.define(XUML_TEXT,
            createOpenTag(TAG_TEXT),
            PEG.rule(XUML_LITERAL),
            createCloseTag(TAG_TEXT));

        PEG.define(XUML_TEMPLATE,
            createOpenTag(TAG_TEMPLATE),
            PEG.rule$ZeroOrMore(
                    PEG.rule$Choice(
                        PEG.rule(XUML_APPLY),
                        PEG.rule(XUML_SWITCH),
                        PEG.rule(XUML_EACH),
                        PEG.rule(COMMENT),
                        PEG.rule(WS1),
                        PEG.rule(XUML_VARRIABLE),
                        PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_TEMPLATE));

        PEG.define(XUML_APPLY,
            PEG.rule$Choice(
                createOmitTag(TAG_APPLY),
                PEG.rule$Sequence(
                    createOpenTag(TAG_APPLY),
                    PEG.rule$ZeroOrMore(
                        PEG.rule$Choice(
                            PEG.rule(XUML_TEMPLATE),
                            PEG.rule(COMMENT),
                            PEG.rule(WS1))),
                    createCloseTag(TAG_APPLY))));

        PEG.define(XUML_IMPORT,
            createOmitTag(TAG_IMPORT));

        PEG.define(XUML_SWITCH,
            createOpenTag(TAG_SWITCH),
            PEG.rule$OneOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_CASE),
                    PEG.rule(XUML_DEFAULT),
                    PEG.rule(COMMENT),
                    PEG.rule(WS1))),
            createCloseTag(TAG_SWITCH));

        PEG.define(XUML_CASE,
            createOpenTag(TAG_CASE),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(COMMENT),
                    PEG.rule(WS1),
                    PEG.rule(XUML_VARRIABLE),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_CASE));

        PEG.define(XUML_DEFAULT,
            createOpenTag(TAG_DEFAULT),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(COMMENT),
                    PEG.rule(WS1),
                    PEG.rule(XUML_VARRIABLE),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_DEFAULT));

        PEG.define(XUML_EACH,
            createOpenTag(TAG_EACH),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_APPLY),
                    PEG.rule(XUML_SWITCH),
                    PEG.rule(XUML_EACH),
                    PEG.rule(COMMENT),
                    PEG.rule(WS1),
                    PEG.rule(XUML_VARRIABLE),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_EACH));

        PEG.define(XUML_ATTR,
            PEG.rule(WS1),
            PEG.rule(ATTR_NAME),
            PEG.rule$Literal("="),
            PEG.rule(ATTR_VALUE))
        .action($$ -> {
            PEGNode name = $$.get(1);
            PEGNode value = $$.get(3);
            $$.clear();
            $$.add(name);
            $$.add(value);
            return $$;
        });

        PEG.define(ATTR_NAME,
            PEG.rule$RegExp("[a-zA-Z][a-zA-Z0-9\\-:]*"))
        .action($$ -> {
            $$.setValue($$.get(0).getValue());
            $$.clear();
            return $$;
        });

        PEG.define(ATTR_VALUE,
            PEG.rule$Literal("\""),
            PEG.rule$RegExp("[^\"]*"),
            PEG.rule$Literal("\""))
        .action($$ -> {
            $$.setValue($$.get(1).getValue());
            $$.clear();
            return $$;
        });

        PEG.define(XUML_VARRIABLE,
            PEG.rule$RegExp("[\\$\\*#@&]\\{[^\\}]+?\\}"))
            .action($$ -> {
                $$.pack();
                $$.clear();
                return $$;
            });

        PEG.define(XUML_LITERAL,
            PEG.rule$OneOrMore(
                PEG.rule$Not(
                    PEG.rule$Choice(
                        PEG.rule(XUML_VARRIABLE),
                        PEG.rule$RegExp("</?" + PREFIX))),
                PEG.rule$Any()))
        .action($$ -> {
            $$.pack();
            $$.clear();
            return $$;
        });

        PEG.define(COMMENT,
            PEG.rule$Literal("<!--"),
            PEG.rule$ZeroOrMore(
                PEG.rule$Not(PEG.rule$Literal("-->")),
                PEG.rule$Any()),
            PEG.rule$Literal("-->"));

        PEG.define(WS0, 
                PEG.rule$RegExp("\\s*"));

        PEG.define(WS1,
                PEG.rule$RegExp("\\s+"));

        PEG.define(EOF,
            PEG.rule$Not(PEG.rule$Any()));
    }

    protected enum XumlRules implements PEGCombinator.RuleTypes {
        XUML_XUML, XUML_TEMPLATE, XUML_APPLY, XUML_IMPORT, XUML_SWITCH, XUML_CASE, XUML_DEFAULT,
        XUML_EACH, XUML_LABEL, XUML_TEXT, XUML_VARRIABLE, XUML_LITERAL, XUML_ATTR,
        ATTR_NAME, ATTR_VALUE, WS0, WS1, EOF, COMMENT
    }

    private static Rule createOmitTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule$Literal("<" + tagName),
            PEG.rule$ZeroOrMore(PEG.rule(XUML_ATTR)),
            PEG.rule(WS0),
            PEG.rule$Literal("/>"))
            .action($$ -> {
                PEGNode attrs = $$.get(1);
                List<PEGNode> attrList = new ArrayList<>();
                for (int i = 0; i < attrs.length(); i++) {
                    attrList.add(attrs.get(i).get(0));
                }
                attrs.clear();
                for (PEGNode attr : attrList) {
                    attrs.add(attr);
                }
                $$.clear();
                $$.add(attrs);
                return $$;
            });
    }

    private static Rule createOpenTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule$Literal("<" + tagName),
            PEG.rule$ZeroOrMore(PEG.rule(XUML_ATTR)),
            PEG.rule(WS0),
            PEG.rule$Literal(">"))
            .action($$ -> {
                PEGNode attrs = $$.get(1);
                List<PEGNode> attrList = new ArrayList<>();
                for (int i = 0; i < attrs.length(); i++) {
                    attrList.add(attrs.get(i).get(0));
                }
                attrs.clear();
                for (PEGNode attr : attrList) {
                    attrs.add(attr);
                }
                $$.clear();
                $$.add(attrs);
                return $$;
            });
    }

    private static Rule createCloseTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule$Literal("</" + tagName + ">"),
            PEG.rule(WS0));
    }

    public static RootStatement compile(String text) throws XumlException, IOException {
        // 構文解析木を生成する
        PEGNode peg = parse(text);
        // 抽象構文木を生成する
        RootStatement root = new RootStatement(peg);
        // 実行形式を生成する
        root.setup();
        return root;
    }

    private static PEGNode parse(String text) throws XumlException {
        PEGContext ctx = PEG.createContext(text);
        try {
            PEGNode node = PEG.rule(XUML_XUML).accept(ctx);
            if (ctx.available() > 0) {
                throw new XumlException(XUML_5300, ctx.subString(ctx.position()));
            }
            return node.pack();
        } catch (PEGException e) {
            throw new XumlException(XUML_5300, ctx.subString(ctx.position()));
        }
    }

//    public static class XumlScope extends HashMap<String,Object> {
//        private static final long serialVersionUID = 1L;
//        private String lang = "ja";
//
//        public XumlScope() {
//        }
//        public XumlScope(Object bean) {
//            for (Method method : bean.getClass().getDeclaredMethods()) {
//                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
//                    if ("getClass".equals(method.getName())) {
//                        continue;
//                    }
//                    try {
//                        String name = BeanUtil.snake2camel(
//                            BeanUtil.camel2snake(method.getName().substring(3)), false);
//                        put(name, method.invoke(bean));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        public String getLang() {
//            return lang;
//        }
//        public void setLang(String lang) {
//            this.lang = lang;
//        }
//    }

    public interface Statement {
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException;
    }

    public static abstract class AbstractStatement implements Statement {
        private String name;
        private String value;
        private Map<String,String> attrsMap = new HashMap<>();
        private List<AbstractStatement> childList = new ArrayList<>();
        private AbstractStatement parent;

        public AbstractStatement(String name) {
            this.name = name;
        }
        public AbstractStatement(String name, String value) {
            this.name = name;
            this.value = value;
        }
        protected void setup() {
            List<AbstractStatement> temp = new ArrayList<>();
            for (AbstractStatement child : getChildList()) {
                temp.add(child);
            }
            for (AbstractStatement child : temp) {
                child.setup();
            }
        }
        public abstract void execute(OutputStream os, Map<String, Object> scope) throws IOException;
        public final RootStatement getRoot() {
            AbstractStatement curr = this;
            while (curr != null) {
                if (curr instanceof RootStatement) {
                    return (RootStatement)curr;
                } else {
                    curr = curr.getParent();
                }
            }
            throw new RuntimeException("root not found.");
        }
        public final String getStatementName() {
            return name;
        }
        public final String getStatementValue() {
            return value;
        }
        protected final AbstractStatement add(AbstractStatement child) {
            child.setParent(this);
            childList.add(child);
            return child;
        }
        protected final List<AbstractStatement> getChildList() {
            return childList;
        }
        protected final void setParent(AbstractStatement parent) {
            this.parent = parent;
        }
        protected final AbstractStatement getParent() {
            return parent;
        }
        protected final void errorFormat(XumlMessages message, Object...args) {
            if (getRoot().isStrict()) {
                throw new XumlException(XUML_3003, args);
            } else {
                LOG.warn(message, args);
            }
        }
        public String toString() {
            StringBuilder sb = new StringBuilder(name + "[");
            String sep = "";
            for (Entry<String,String> entry : attrsMap.entrySet()) {
                sb.append(sep + entry.getKey() + "=" + entry.getValue());
                sep = ", ";
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class RootStatement extends AbstractStatement {
        private Map<String,LabelStatement> labels;
        private Map<String,TemplateStatement> templates;
        private Map<String,RootStatement> namespaces;
        public RootStatement(PEGNode peg) throws IOException {
            super("root");
            labels = new HashMap<>();
            templates = new HashMap<>();
            namespaces = new HashMap<>();
            walk(this, peg);
        }
        @Override
        protected void setup() {
            for (AbstractStatement child : getChildList()) {
                switch (child.getStatementName()) {
                case "xuml":
                    break;
                default:
                    errorFormat(XUML_3004, child.getStatementName());
                }
            }
            super.setup();
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            AbstractStatement xumlStatement = getChildList().get(0);
            for (AbstractStatement child : xumlStatement.getChildList()) {
                child.execute(os, scope);
            }
        }

        private void walk(AbstractStatement curr, PEGNode node) throws IOException {
            RuleTypes type = node.getType();
            if (type instanceof XumlRules) {
                switch ((XumlRules)type) {
                case XUML_XUML:
                    curr = curr.add(new XumlStatement());
                    break;
                case XUML_IMPORT:
                    curr = curr.add(new ImportStatement());
                    break;
                case XUML_TEMPLATE:
                    curr = curr.add(new TemplateStatement());
                    break;
                case XUML_APPLY:
                    curr = curr.add(new ApplyStatement());
                    break;
                case XUML_SWITCH:
                    curr = curr.add(new SwitchStatement());
                    break;
                case XUML_CASE:
                    curr = curr.add(new CaseStatement());
                    break;
                case XUML_DEFAULT:
                    curr = curr.add(new DefaultStatement());
                    break;
                case XUML_EACH:
                    curr = curr.add(new EachStatement());
                    break;
                case XUML_LABEL:
                    curr = curr.add(new LabelStatement());
                    break;
                case XUML_TEXT:
                    curr = curr.add(new TextStatement());
                    break;
                case XUML_VARRIABLE:
                    curr = curr.add(new VariableStatement(node.getValue()));
                    break;
                case XUML_LITERAL:
                    curr = curr.add(new LiteralStatement(node.getValue()));
                    break;
                case XUML_ATTR:
                    String attrName = node.get(0).getValue();
                    String attrValue = node.get(1).getValue();
                    try {
                        BeanUtil.setProperty(curr, attrName, attrValue);
                    } catch (BeanNotFoundException e) {
                        errorFormat(XUML_3003, curr.getStatementName(), attrName);
                    }
                default:
                }
            }
            for (int i = 0; i < node.length(); i++) {
                walk(curr, node.get(i));
            }
        }
        public String getNamespace() {
            return ((XumlStatement)getChildList().get(0)).getNamespace();
        }
        public void addLabel(String name, LabelStatement label) {
            //LOG.debug("addLabel({0})", name);
            labels.put(name, label);
        }
        public LabelStatement getLabel(String name) {
            return labels.get(name);
        }
        public void addTemplate(String name, TemplateStatement template) {
            //LOG.debug(getRoot().getNamespace() + " -> addTemplate({0})", name);
            templates.put(name, template);
        }
        public TemplateStatement getTemplate(String name) {
            return templates.get(name);
        }
        public boolean isStrict() {
            return true;
        }
        public Map<String,RootStatement> getNamespaces() {
            return namespaces;
        }
        public void addNamespace(String namespace, RootStatement root) {
            namespaces.put(namespace, root);
        }
    }

    /**
     * <xuml:xuml namespace="layout">..</xuml:xuml>
     */
    public static class XumlStatement extends AbstractStatement {
        private String namespace;
        private String use;

        public XumlStatement() {
            super("xuml");
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            for (AbstractStatement child : this.getChildList()) {
                child.execute(os, scope);
            }
        }
        public String getUse() {
            return use;
        }
        public void setUse(String use) {
            this.use = use;
        }
        public String getNamespace() {
            return namespace;
        }
        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (namespace != null) attrs.add("namespace=" + namespace);
            if (use != null) attrs.add("use=" + use);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    /**
     * <xuml:import file="file.xuml"/>
     */
    public static class ImportStatement extends AbstractStatement {
        private String file;
        public ImportStatement() {
            super("import");
        }
        @Override
        protected void setup() {
            if (file == null) {
                throw new XumlException(XUML_3002, "file");
            }
            try {
                importFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
        }
        public String getFile() {
            return file;
        }
        public void setFile(String file) {
            this.file = file;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (file != null) attrs.add("file=" + file);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
        private void importFile() throws IOException {
            try {
                RootStatement root = (RootStatement)compile(readFile());
                XumlStatement xuml = (XumlStatement)root.getChildList().get(0);
//                for (Entry<String,TemplateStatement> child : root.templates.entrySet()) {
//                    fixNamespace(root.getNamespace(), child.getValue());
//                }
                getRoot().addNamespace(xuml.getNamespace(), root);
            } catch (XumlException e) {
                throw new IOException(e);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        private String readFile() throws IOException {
            InputStream is = ImportStatement.class
                .getClassLoader()
                .getResourceAsStream(file);
            if (is == null) {
                throw new IOException("file[" + file + "] not found.");
            }
            try (Reader reader = new InputStreamReader(is, "UTF-8")) {
                StringBuilder sb = new StringBuilder();
                char[] c = new char[2048];
                int len = 0;
                while ((len = reader.read(c)) > 0) {
                    sb.append(c, 0, len);
                }
                return sb.toString();
            }
        }
//        private void fixNamespace(String ns, AbstractStatement stmt) {
//            LOG.debug("fixNamespace(" + stmt.getStatementName() + ")");
//            switch (stmt.getStatementName()) {
//            case "template":
//                TemplateStatement temp = (TemplateStatement)stmt;
//                String nsName = (ns == null || "".equals(ns)) ?
//                    temp.getName() : ns + "." + temp.getName();
//                getRoot().addTemplate(nsName, temp);
//                break;
//            case "apply":
//                ApplyStatement apply = (ApplyStatement)stmt;
//                apply.setName((ns == null || "".equals(ns)) ?
//                    apply.getName() : ns + "." + apply.getName());
//                break;
//            default:
//            }
//            for (AbstractStatement child : stmt.getChildList()) {
//                fixNamespace(ns, child);
//            }
//        }
    }

    /**
     * <xuml:template name="..">..</xuml:template>
     */
    public static class TemplateStatement extends AbstractStatement {
        private String name;

        public TemplateStatement() {
            super("template");
        }
        @Override
        protected void setup() {
            if (name == null) {
                throw new XumlException(XUML_3002, "name");
            }
            getRoot().addTemplate(name, this);
            getParent().getChildList().remove(this);
            super.setup();
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            for (AbstractStatement child : this.getChildList()) {
                child.execute(os, scope);
            }
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (name != null) attrs.add("name=" + name);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    /**
     * <xuml:apply name=".."/>
     */
    public static class ApplyStatement extends AbstractStatement {
        private String name;

        public ApplyStatement() {
            super("apply");
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            RootStatement root = getRoot();
            TemplateStatement temp = null;
            String[] names = name.split("\\.");
            if (names.length == 1) {
                temp = getRoot().getTemplate(name);
            } else {
                root = getRoot().getNamespaces().get(names[0]);
                temp = root.getTemplate(names[1]);
            }
            if (temp != null) {
                // テンプレートを差し替える
                List<TemplateStatement> currList = new ArrayList<>();
                try {
                    for (AbstractStatement child : getChildList()) {
                        TemplateStatement next = (TemplateStatement)child;
                        TemplateStatement curr = root.getTemplate(next.getName());
                        if (curr != null) {
                            currList.add(curr);
                        }
                        root.addTemplate(next.getName(), next);
                    }
                    temp.execute(os, scope);
                } finally {
                    // テンプレートを戻す
                    for (AbstractStatement child : currList) {
                        TemplateStatement next = (TemplateStatement)child;
                        root.addTemplate(next.getName(), next);
                    }
                }
            } else {
                LOG.warn(XUML_3005, getRoot().getNamespace() + " -> " + name);
            }
        }
        public void setup() {
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (name != null) attrs.add("name=" + name);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    /**
     * <xuml:switch value="..">..</xuml:switch>
     */
    public static class SwitchStatement extends AbstractStatement {
        private String value;

        public SwitchStatement() {
            super("switch");
        }
        @Override
        protected void setup() {
            // TODO Auto-generated method stub
            super.setup();
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            String value = getValue();
            LOG.trace("{0}({1})", getStatementName(), value);
            VarExpression expr = VarExpression.newInstance(value, this);
            Object $$ = expr.getObject(scope);
            List<CaseStatement> cases = new ArrayList<>();
            DefaultStatement defaultStatement = null;
            for (AbstractStatement child : this.getChildList()) {
                if (child instanceof CaseStatement) {
                    cases.add((CaseStatement)child);
                } else if (child instanceof DefaultStatement) {
                    defaultStatement = (DefaultStatement)child;
                }
            }
            for (CaseStatement caseStatement : cases) {
                if (caseStatement.test($$)) {
                    caseStatement.execute(os, scope);
                    return;
                }
            }
            if (defaultStatement != null) {
                defaultStatement.execute(os, scope);
            }
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (value != null) attrs.add("value=" + value);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    /**
     * <xuml:case eq="..">..</xuml:case>
     * <xuml:case ne="..">..</xuml:case>
     */
    public static class CaseStatement extends AbstractStatement {
        private String op;
        private String expect;

        public CaseStatement() {
            super("case");
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            for (AbstractStatement child : this.getChildList()) {
                child.execute(os, scope);
            }
        }
        public boolean test(Object value) {
            //LOG.debug("test({0})", value);
            switch (op) {
            case "eq": return compare(expect, value) == 0;
            case "ne": return compare(expect, value) != 0;
            case "lt": return compare(expect, value) < 0;
            case "le": return compare(expect, value) <= 0;
            case "gt": return compare(expect, value) > 0;
            case "ge": return compare(expect, value) >= 0;
            default: return false;
            }
        }
        private int compare(String expect, Object actual) {
            if (actual == null) {
                return expect == null ? 0 : 1;
            } else if (actual instanceof String) {
                return ((String)actual).compareTo(expect);
            } else if (actual instanceof Integer) {
                return ((Integer) actual).compareTo(Integer.parseInt(expect));
            } else if (actual instanceof Long) {
                return ((Long) actual).compareTo(Long.parseLong(expect));
            } else if (actual instanceof Float) {
                return ((Float)actual).compareTo(Float.parseFloat(expect));
            } else if (actual instanceof Double) {
                return ((Double)actual).compareTo(Double.parseDouble(expect));
            } else if (actual instanceof BigDecimal) {
                return ((BigDecimal)actual).compareTo(new BigDecimal(expect));
            } else {
                throw new RuntimeException("invalid object : " + actual);
            }
        }
        public void setEq(String eq) {
            this.op = "eq";
            this.expect = eq;
        }
        public void setNe(String ne) {
            this.op = "ne";
            this.expect = ne;
        }
        public void setLt(String lt) {
            this.op = "lt";
            this.expect = lt;
        }
        public void setLe(String le) {
            this.op = "le";
            this.expect = le;
        }
        public void setGt(String gt) {
            this.op = "gt";
            this.expect = gt;
        }
        public void setGe(String ge) {
            this.op = "ge";
            this.expect = ge;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (op != null) attrs.add(op + "=" + expect);
            return new StringBuilder(getStatementName() + "[")
                .append(op + "=" + expect)
                .append("]")
                .toString();
        }
    }

    /**
     * <xuml:default>..</xuml:default>
     */
    public static class DefaultStatement extends AbstractStatement {
        public DefaultStatement() {
            super("default");
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            for (AbstractStatement child : this.getChildList()) {
                child.execute(os, scope);
            }
        }
        public String toString() {
            return getStatementName();
        }
    }

    /**
     * <xuml:each item=".." value="..">..</xuml:each>
     */
    public static class EachStatement extends AbstractStatement {
        private String item;
        private String value;
        private VarExpression expr;

        public EachStatement() {
            super("each");
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("{0}({1})", getStatementName(), value);
            for (Object o : expr.getList(scope)) {
                Object prev = scope.put(item, o);
                for (AbstractStatement child : this.getChildList()) {
                    child.execute(os, scope);
                }
                scope.remove(item);
                if (prev != null) {
                    scope.put(item, prev);
                }
            }
        }
        public String getValue() {
            return value;
        }
        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
        }
        public void setValue(String value) {
            this.expr = VarExpression.newInstance(value, this);
            this.value = value;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (item != null) attrs.add("item=" + item);
            if (value != null) attrs.add("value=" + value);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    public static class LabelStatement extends AbstractStatement {
        private String key;

        public LabelStatement() {
            super("label");
        }
        @Override
        protected void setup() {
            getRoot().addLabel(key, this);
            super.setup();
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
        }
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public String getText(String lang) {
            for (AbstractStatement child : getChildList()) {
                if (!(child instanceof TextStatement)) continue;
                TextStatement text = (TextStatement)child;
                if (lang.equals(text.getLang())) {
                    return text.getChildList().get(0).getStatementValue();
                }
            }
            return null;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (key != null) attrs.add("key=" + key);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    public static class TextStatement extends AbstractStatement {
        private String lang;
        private String text;

        public TextStatement() {
            super("text");
        }
        @Override
        protected void setup() {
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
        }
        public String getLang() {
            return lang;
        }
        public void setLang(String lang) {
            this.lang = lang;
        }
        public String toString() {
            List<String> attrs = new ArrayList<>();
            if (lang != null) attrs.add("lang=" + lang);
            return new StringBuilder(getStatementName() + "[")
                .append(String.join(" ", attrs))
                .append("]")
                .toString();
        }
    }

    /**
     * ${..} / @{..} / #{..} / *{..} / &{..}
     */
    public static class VariableStatement extends AbstractStatement {
        private VarExpression expr;

        protected VariableStatement(String value) {
            super("variable", value);
            expr = VarExpression.newInstance(value, this);
        }
        @Override
        protected void setup() {
            // TODO Auto-generated method stub
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            String value = getStatementValue();
            LOG.trace("{0}({1})", getStatementName(), value);
            if (value != null) {
                Object o = expr.getObject(scope);
                if (o != null) {
                    os.write((o.toString()).getBytes("UTF-8"));
                }
            }
        }
        public String toString() {
            return getStatementName() + "[" + getStatementValue() + "]";
        }
    }

    /**
     * ..
     */
    public static class LiteralStatement extends AbstractStatement {
        protected LiteralStatement(String value) {
            super("literal", value);
        }
        @Override
        protected void setup() {
        }
        @Override
        public void execute(OutputStream os, Map<String, Object> scope) throws IOException {
            LOG.trace("execute - {0}", getStatementName());
            if (getStatementValue() != null) {
                os.write(getStatementValue().getBytes("UTF-8"));
            }
        }
        public String toString() {
            String value = getStatementValue()
                .replaceAll("[\r\n\t\b]", " ");
            int len = value.length();
            if (len > 10) {
                value = value.substring(0, 10) + "..";
            }
            return getStatementName() + "[" + value + "]";
        }
    }

    public abstract static class VarExpression {
        private static final Pattern pattern = Pattern.compile("^([\\$\\*#@&])\\{(.*?)\\}$");
        private String name;

        public VarExpression(String name) {
            this.name = name;
        }
        public abstract Object getObject(Map<String, Object> scope);
        public static VarExpression newInstance(String expr, AbstractStatement stmt) {
            Matcher m = pattern.matcher(expr);
            if (m.matches()) {
                String type = m.group(1);
                String name = m.group(2);
                switch (type) {
                case "$": return new BeanExpression(name);
                case "*": return new RawExpression(name);
                case "@": return new PrefixExpression(name);
                case "#": return new LabelExpression(name, stmt);
                case "&": return new FunctionExpression(name);
                default:
                    throw new RuntimeException(type + " is unknown.");
                }
            } else {
                throw new RuntimeException(expr + " is unknown.");
            }
        }
        public String getName() {
            return name;
        }
        public List<Object> getList(Object bean) {
            List<Object> result = new ArrayList<>();
            try {
                Object object = BeanUtil.getValue(bean, name);
                if (object == null) {
                    result = new ArrayList<Object>();
                } else if (Iterable.class.isAssignableFrom(object.getClass())) {
                    for (Iterator<Object> iter = ((Iterable)object).iterator(); iter.hasNext();) {
                        result.add(iter.next());
                    }
                } else if (object.getClass().isArray()) {
                    result = Arrays.asList(object);
//                } else if (Map.class.isAssignableFrom(bean.getClass())) {
//                    for (Entry<Object,Object> entry : ((Map<Object,Object>)bean).entrySet()) {
//                        result.add(entry.getValue());
//                    }
                } else {
                    result = new ArrayList<Object>();
                    result.add(object);
                }
            } catch (BeanNotFoundException e) {
                LOG.warn(e.getMessage(), e);
            }
            return result;
        }
    }

    public static class BeanExpression extends VarExpression {
        public BeanExpression(String name) {
            super(name);
        }
        @Override
        public Object getObject(Map<String, Object> model) {
            try {
                Object result = BeanUtil.getValue(model, getName());
                return result != null ? escape(result) : null;
            } catch (BeanNotFoundException e) {
                LOG.warn(e.getMessage());
                return null;
            }
        }
        private String escape(Object o) {
            if (o == null) return null;
            return o.toString()
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;");
        }
    }
    public static class RawExpression extends VarExpression {
        public RawExpression(String name) {
            super(name);
        }
        @Override
        public Object getObject(Map<String, Object> scope) {
            try {
                return BeanUtil.getValue(scope, getName());
            } catch (BeanNotFoundException e) {
                LOG.warn(e.getMessage());
                return null;
            }
        }
    }
    public static class LabelExpression extends VarExpression {
        private AbstractStatement stmt;

        public LabelExpression(String name, AbstractStatement stmt) {
            super(name);
            this.stmt = stmt;
        }
        @Override
        public Object getObject(Map<String, Object> scope) {
            LabelStatement label = stmt.getRoot().getLabel(getName());
            Object lang = scope.get("lang");
            if (lang != null) {
                return label.getText(lang.toString());
            } else {
                return null; // TODO
            }
        }
    }
    public static class PrefixExpression extends VarExpression {
        public PrefixExpression(String name) {
            super(name);
        }
        public Object getObject(Map<String, Object> scope) {
            // FIXME
            return scope.get("context.path") + getName();
        }
    }
    public static class FunctionExpression extends VarExpression {

        public FunctionExpression(String name) {
            super(name);
        }

        @Override
        public Object getObject(Map<String, Object> scope) {
            return ApplicationContext
                .getBean(FunctionService.class)
                .execute(getName());
        }
        public List<Object> getList(Object bean) {
            List<Object> result = new ArrayList<>();
            Object object = ApplicationContext.getBean(FunctionService.class)
                .execute(getName());
            if (object == null) {
                result = new ArrayList<Object>();
            } else if (Iterable.class.isAssignableFrom(object.getClass())) {
                for (Iterator<Object> iter = ((Iterable)object).iterator(); iter.hasNext();) {
                    result.add(iter.next());
                }
            } else if (object.getClass().isArray()) {
                result = Arrays.asList(object);
            } else {
                result = new ArrayList<Object>();
                result.add(object);
            }
            return result;
        }
    }

    public static class XumlException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private XumlMessages type;
        public XumlException(XumlMessages type, Object...args) {
            super(type.getMessage(args));
            this.type = type;
        }
        public XumlMessages getType() {
            return type;
        }
    }
}
