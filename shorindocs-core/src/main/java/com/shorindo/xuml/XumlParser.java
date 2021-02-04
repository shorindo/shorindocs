package com.shorindo.xuml;

import static com.shorindo.xuml.XumlParser.XumlRules.*;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.tools.PEGCombinator;
import com.shorindo.tools.PEGCombinator.PEGContext;
import com.shorindo.tools.PEGCombinator.PEGException;
import com.shorindo.tools.PEGCombinator.PEGNode;
import com.shorindo.tools.PEGCombinator.Rule;

public class XumlParser {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlParser.class);
    private static final PEGCombinator PEG = new PEGCombinator();
    private static final String TAG_XUML = "xuml:xuml";
    private static final String TAG_IMPORT = "xuml:import";
    private static final String TAG_TEMPLATE = "xuml:template";
    private static final String TAG_APPLY = "xuml:apply";

    static {
        PEG.define(XUML_XUML,
            createOpenTag(TAG_XUML),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_TEMPLATE),
                    PEG.rule(XUML_APPLY),
                    PEG.rule(XUML_IMPORT),
                    PEG.rule(WS1),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_XUML));

        PEG.define(XUML_TEMPLATE,
            createOpenTag(TAG_TEMPLATE),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule(XUML_APPLY),
                    PEG.rule(XUML_IMPORT),
                    PEG.rule(XUML_LITERAL))),
            createCloseTag(TAG_TEMPLATE));

        PEG.define(XUML_APPLY,
            createOmitTag(TAG_APPLY));

        PEG.define(XUML_IMPORT,
            createOmitTag(TAG_IMPORT));

        PEG.define(ATTR_NAME,
            PEG.rule$RegExp("[a-zA-Z][a-zA-Z0-9\\-:]*"));

        PEG.define(ATTR_VALUE,
            PEG.rule$Sequence(
                PEG.rule$Literal("\""),
                PEG.rule$RegExp("[^\"]*"),
                PEG.rule$Literal("\"")));

        PEG.define(XUML_LITERAL,
            PEG.rule$OneOrMore(
                PEG.rule$Not(
                    PEG.rule$RegExp("</?xuml:")),
                PEG.rule$Any()));

        PEG.define(WS0, 
                PEG.rule$RegExp("\\s*"));

        PEG.define(WS1,
                PEG.rule$RegExp("\\s+"));

        PEG.define(EOF,
            PEG.rule$Not(PEG.rule$Any()));
    }

    public enum XumlRules implements PEGCombinator.RuleTypes {
        XUML_XUML, XUML_TEMPLATE, XUML_APPLY, XUML_IMPORT, XUML_SWITCH, XUML_CASE, XUML_FOR,
        XUML_LITERAL,
        ATTR_NAME, ATTR_VALUE,
        WS0, WS1, EOF
    }

    private static Rule createOmitTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule(WS0),
            PEG.rule$Literal("<" + tagName),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule(WS1),
                        PEG.rule(ATTR_NAME),
                        PEG.rule$Literal("="),
                        PEG.rule(ATTR_VALUE)),
                    PEG.rule(ATTR_NAME))),
            PEG.rule$Literal("/>"),
            PEG.rule(WS0));
    }

    private static Rule createOpenTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule(WS0),
            PEG.rule$Literal("<" + tagName),
            PEG.rule$ZeroOrMore(
                PEG.rule$Choice(
                    PEG.rule$Sequence(
                        PEG.rule(WS1),
                        PEG.rule(ATTR_NAME),
                        PEG.rule$Literal("="),
                        PEG.rule(ATTR_VALUE)),
                    PEG.rule(ATTR_NAME))),
            PEG.rule$Literal(">"),
            PEG.rule(WS0));
    }

    private static Rule createCloseTag(String tagName) {
        return PEG.rule$Sequence(
            PEG.rule(WS0),
            PEG.rule$Literal("</" + tagName + ">"),
            PEG.rule(WS0));
    }

    public PEGNode parse(String text) throws XumlException {
        PEGContext ctx = PEG.createContext(text);
        try {
            PEGNode node = PEG.rule(XUML_XUML).accept(ctx);
            if (ctx.available() > 0) {
                throw new XumlException(ctx.subString(ctx.position()));
            }
            return node.pack();
        } catch (PEGException e) {
            throw new XumlException(ctx.subString(ctx.position()));
            //throw new XumlException(e);
        }
    }

    public static class XumlException extends Exception {

        public XumlException(String message) {
            super(message);
        }
        public XumlException(Exception e) {
            super(e);
        }
    }
}
