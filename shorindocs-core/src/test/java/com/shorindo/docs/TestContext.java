/*
 * Copyright 2019 Shorindo, Inc.
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class TestContext {
    private static Map<Class<?>, Object> serviceMap = new HashMap<>();

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        TestContext.addService(TestController.class, TestControllerImpl.class);
        TestController service = TestContext.getService(TestController.class);
        System.out.println(service.foo("baz"));
    }

    public static void addService(Class<?> itfc, Class<?> impl) {
        try {
            Object target = impl.newInstance();
            Object proxy = Proxy.newProxyInstance(
                    TestContext.class.getClassLoader(),
                    new Class<?>[] { itfc },
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method,
                                Object[] args) throws Throwable {
                            System.out.println("method[" + method.getName() + "] invoke.");
                            try {
                                return method.invoke(target, args);
                            } finally {
                                System.out.println("method[" + method.getName() + "] end.");
                            }
                        }
                    });
            serviceMap.put(itfc, proxy);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T getService(Class<T> itfc) throws InstantiationException, IllegalAccessException {
        return (T)serviceMap.get(itfc);
    }

    public static interface TestController {
        public String foo(String in);
        public String bar(String in);
    }

    public static class TestControllerImpl implements TestController {

        @Override
        public String foo(String in) {
            return this.bar(in);
        }

        @Override
        public String bar(String in) {
            return "{" + in + "}";
        }
    }
}
