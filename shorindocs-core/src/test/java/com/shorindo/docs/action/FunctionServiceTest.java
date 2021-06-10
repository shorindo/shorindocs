package com.shorindo.docs.action;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import jdk.nashorn.internal.ir.annotations.Ignore;

@Ignore
public class FunctionServiceTest {
    private Context ctx;
    private ScriptableObject globalScope;

    public static void main(String args[]) {
        FunctionServiceTest t = new FunctionServiceTest();
        Object r = t.execute("recents(0, 10)");
        //Object r = t.execute("new java.util.Date()");
        System.out.println(r);
    }

    public FunctionServiceTest() {
        this.ctx = Context.enter();
        ctx.setClassShutter(new ClassShutter() {

            @Override
            public boolean visibleToScripts(String fullClassName) {
                if ("com.shorindo.docs.action.FunctionServiceTest".equals(fullClassName)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        this.globalScope = ctx.initStandardObjects();
        String[] funcNames = { "recents" };
        globalScope.defineFunctionProperties(funcNames, FunctionServiceTest.class, ScriptableObject.DONTENUM);
    }

    public Object execute(String func) {
        Scriptable localScope = ctx.newObject(globalScope);
        return ctx.evaluateString(localScope, func, func, 0, null);
    }

//    public Object recents(int offset, int length) {
//        return "workd";
//    }

    public static Object recents(int offset, int length) {
        return "recents(" + offset + ", " + length + ")";
    }

//    public static Object recents() {
//        return recents(0, 20);
//    }
}
