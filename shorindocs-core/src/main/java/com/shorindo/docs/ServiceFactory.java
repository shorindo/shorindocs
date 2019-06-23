/*
 * Copyright 2018 Shorindo, Inc.
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
@SuppressWarnings("unchecked")
public abstract class ServiceFactory {
    private static ActionLogger LOG =
            ActionLogger.getLogger(ServiceFactory.class);
    private static Map<Class<?>,Class<?>> interfaceap =
            new ConcurrentHashMap<Class<?>,Class<?>>();
    private static Map<Class<?>,Object> instanceMap =
            new ConcurrentHashMap<Class<?>,Object>();

    public static synchronized <T> void addService(Class<T> itfc, Class<? extends T> impl) {
        interfaceap.put(itfc, impl);
        instanceMap.remove(itfc);
    }

    public static synchronized <T> T getService(Class<T> itfc) {
        try {
            if (instanceMap.containsKey(itfc)) {
                return (T)instanceMap.get(itfc);
            } else if (interfaceap.containsKey(itfc)){
                T instance = (T)interfaceap.get(itfc).newInstance();
                Object proxy = Proxy.newProxyInstance(
                        ServiceFactory.class.getClassLoader(),
                        new Class<?>[] { itfc },
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                long st = System.currentTimeMillis();
                                LOG.debug("method[" + method.getName() + "] invoke.");
                                try {
                                    return method.invoke(instance, args);
                                } finally {
                                    LOG.debug("method[" + method.getName() + "] end " +
                                            (System.currentTimeMillis() - st) + "ms");
                                }
                            }
                        });
                instanceMap.put(itfc, proxy);
                return (T)proxy;
            } else {
                throw new RuntimeException("No implementation defined for '" + itfc + "'.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
