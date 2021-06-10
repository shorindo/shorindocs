package com.shorindo.xuml;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.document.DocumentService;

public class FunctionServiceImpl implements FunctionService {
    private static final ActionLogger LOG = ActionLogger.getLogger(FunctionServiceImpl.class);
    private ThreadLocal<Context> threadContext = new ThreadLocal<>();
    private ThreadLocal<ScriptableObject> threadScope = new ThreadLocal<>();

    public FunctionServiceImpl() {
    }

    private Context getContext() {
        Context ctx = threadContext.get();
        if (ctx == null) {
          ctx = Context.enter();
          ctx.setClassShutter(new ClassShutter() {
              @Override
              public boolean visibleToScripts(String fullClassName) {
                  if (fullClassName.equals(FunctionServiceImpl.class.getName())) {
                      return true;
                  } else {
                      return false;
                  }
              }
          });
          threadContext.set(ctx);
          ScriptableObject globalScope = ctx.initStandardObjects();
          String[] funcNames = { "recents", "notices" };
          globalScope.defineFunctionProperties(funcNames, FunctionServiceImpl.class, ScriptableObject.DONTENUM);
          threadScope.set(globalScope);
        }
        return ctx;
    }

    @Override
    public Object execute(String func) {
        try {
            Context ctx = getContext();
            ScriptableObject globalScope = threadScope.get();
            Scriptable localScope = ctx.newObject(globalScope);
            return ctx.evaluateString(localScope, func, func, 0, null);
        } catch (Exception e) {
            LOG.error(DocumentMessages.DOCS_9999, e);
            return null;
        }
    }

    public static Object recents(int offset, int length) {
        return ApplicationContext
            .getBean(DocumentService.class)
            .recents(offset, length);
    }

    public static Object notices() {
        List<String> noticeList = new ArrayList<>();
        noticeList.add("foo");
        noticeList.add("bar");
        noticeList.add("baz");
        return noticeList;
    }
}
