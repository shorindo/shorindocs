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
package com.shorindo.docs;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.junit.Test;

/**
 * 
 */
public class RhinoContext {

    /**
     * 実行ごとに異なるメモリ空間が使われること
     */
    @Test
    public void testEachExecution() {
        Context ctx = Context.enter();
        ScriptableObject globalScope = ctx.initStandardObjects();
        globalScope.put("console", globalScope, new JsConsole());
        Script script = ctx.compileString("console.log('typeof a = ', typeof a); typeof a;", "testEachExecution", 1, null);
        try {
            Scriptable localScope = ctx.newObject(globalScope);
            localScope.put("a", localScope, 123);
            Object result = script.exec(ctx, localScope);
            assertEquals("number", Context.jsToJava(result, String.class));

            localScope = ctx.newObject(globalScope);
            result = script.exec(ctx, localScope);
            assertEquals("undefined", Context.jsToJava(result, String.class));
        } finally {
            Context.exit();
        }
    }

    /**
     * - 異なるスレッドはでは異なるメモリ空間が使われること
     */
    @Test
    public void testThread() {
        final String source = "var sum = 0; for (var i = 0; i < 100000; i++) sum += i; sum;";
        
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Thread.currentThread().sleep(100000);
                } catch (InterruptedException s) {
                    System.out.println("start:" + Thread.currentThread().getId());
                    // スレッドごとにContextが必要
                    Context ctx = Context.enter();
                    ScriptableObject scopeA = ctx.initStandardObjects();
                    Scriptable that = ctx.newObject(scopeA);
                    Script script = ctx.compileString(source, "test", 0, null);
                    //scopeA.defineProperty("greet", "Hello-" + Thread.currentThread().getId(), 0);
                    Object resultA = script.exec(ctx, scopeA);
                    System.out.println("result = " + Context.jsToJava(resultA, Long.class));
                    Context.exit();
                }
            }
        };
        ThreadGroup group = new ThreadGroup("child");
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(group, runnable));
        }
        for (Thread th : threads) {
            th.start();
        }
        group.interrupt();
        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * - 異なるプラグインでは異なるメモリ空間が使われること
     */
    @Test
    public void testIndependentThread() {
        
    }
    
    public static class JsConsole {
        public void log(Object... params) {
            StringBuilder sb = new StringBuilder();
            for (Object param : params) {
                sb.append(param == null ? null : param.toString());
            }
            System.out.println(sb.toString());
        }
    }
}
