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
package com.shorindo.docs.plugin;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.shorindo.docs.action.ActionEvent;
import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public class PluginContainer {
    private static ActionLogger LOG = ActionLogger.getLogger(PluginContainer.class);
    private static ThreadLocal<PluginContainer> threadContainer = new ThreadLocal<>();
    
    public static PluginContainer initContainer() {
        PluginContainer container = threadContainer.get();
        if (container == null) {
            container = new PluginContainer();
            threadContainer.set(container);
            //LOG.debug("initContainer() - new");
        } else {
            //LOG.debug("initContainer() - curr");
        }
        return container;
    }
    
    private Context ctx;
    private ScriptableObject globalScope;
    private Script script;

    private PluginContainer() {
        ctx = Context.enter();
        globalScope = ctx.initStandardObjects();
        script = ctx.compileString("'Hello';", "plugin", 1, null);
    }
    
    public Object onEnvent(ActionEvent event) {
        Scriptable scope = ctx.newObject(globalScope);
        return script.exec(ctx, scope);
    }
}
