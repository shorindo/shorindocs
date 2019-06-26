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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.Transactional;

/**
 * サービスを管理する
 */
@SuppressWarnings("unchecked")
public abstract class ServiceFactory {
    private static ActionLogger LOG = ActionLogger.getLogger(ServiceFactory.class);
    private static Map<Class<?>,Class<?>> interfaceap = new ConcurrentHashMap<Class<?>,Class<?>>();
    private static Map<Class<?>,Object> instanceMap = new ConcurrentHashMap<Class<?>,Object>();
    private static Set<TransactionListener> listenerSet = new HashSet<TransactionListener>();

    /**
     * サービスを登録する
     * 
     * @param itfc サービスのインタフェース
     * @param impl サービスの実装クラス
     */
    public static synchronized <T> void addService(Class<T> itfc, Class<? extends T> impl) {
        interfaceap.put(itfc, impl);
        instanceMap.remove(itfc);
    }

    /**
     * サービスをシングルトンで取得する
     * 
     * @param itfc サービスのインタフェース
     * @return サービスのインスタンス
     */
    public static synchronized <T> T getService(Class<T> itfc) {
        try {
            if (instanceMap.containsKey(itfc)) {
                return (T)instanceMap.get(itfc);
            } else if (interfaceap.containsKey(itfc)){
                Class<T> implClass = (Class<T>)interfaceap.get(itfc);
                T instance = implClass.newInstance();
                Object proxy = Proxy.newProxyInstance(
                        ServiceFactory.class.getClassLoader(),
                        new Class<?>[] { itfc },
                        new InvocationHandler() {
                            /*
                             * TODO トランザクションが入れ子の対応
                             */
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                long st = System.currentTimeMillis();
                                LOG.trace("method[" + method.getName() + "] invoke:" + instance);
                                boolean transactional = isTransactional(implClass, method);
                                sendEvent(transactional, TransactionEvent.BEGIN);
                                try {
                                    Object result = method.invoke(instance, args);
                                    sendEvent(transactional, TransactionEvent.COMMIT);
                                    return result;
                                } catch (Throwable th) {
                                    sendEvent(transactional, TransactionEvent.ROLLBACK);
                                    throw th;
                                } finally {
                                    LOG.trace("method[" + method.getName() + "] end " +
                                            (System.currentTimeMillis() - st) + "ms");
                                }
                            }
                        });
                instanceMap.put(itfc, proxy);
                if (TransactionListener.class.isAssignableFrom(implClass)) {
                    addListener((TransactionListener)instance);
                }
                return (T)proxy;
            } else {
                throw new RuntimeException("No implementation defined for '" + itfc + "'.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * トランザクションリスナーを登録する
     */
    private static void addListener(TransactionListener listener) {
        listenerSet.add(listener);
    }

    /*
     * methodと同一のシグネチャを持つobjectのメソッドが@Transactionalアノテーションを
     * 持つかどうか判定する。
     * 
     * @param target
     * @param method
     * @return
     */
    private static boolean isTransactional(Class<?> target, Method method) {
        Parameter params[] = method.getParameters();
        Class<?> targetClasses[] = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            targetClasses[i] = params[i].getType();
        }
        try {
            Method targetMethod = target.getMethod(method.getName(), targetClasses);
            if (targetMethod.getAnnotation(Transactional.class) != null) {
                return true;
            }
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        }
        return false;
    }

    /*
     * トランザクションリスナーにイベントを送信する
     */
    private static void sendEvent(boolean transactional, TransactionEvent event) {
        if (transactional) {
            for (TransactionListener listener : listenerSet) {
                listener.onEvent(event);
            }
        }
    }
}
