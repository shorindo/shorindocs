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

import static com.shorindo.docs.document.DocumentMessages.DOCS_9006;
import static com.shorindo.docs.document.DocumentMessages.DOCS_9999;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.shorindo.docs.ApplicationContextConfig.Property;
import com.shorindo.docs.TxEvent.TxEventType;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;
import com.shorindo.docs.repository.Transactional;

/**
 * 
 */
public class ApplicationContext {
    private static final ActionLogger LOG = ActionLogger.getLogger(ApplicationContext.class);
    private static final Properties props = new Properties();
    private static final Locale DEFAULT_LANG = Locale.JAPANESE;
    public static final String WEB_INF_CLASSES = "/WEB-INF/classes";
    public static final String WEB_INF_LIB = "/WEB-INF/lib";
    private static Map<Class<?>,Class<?>> interfaceMap = new ConcurrentHashMap<Class<?>,Class<?>>();
    private static Map<Class<?>,Object> instanceMap = new ConcurrentHashMap<Class<?>,Object>();
    private static Set<TxEventListener> listenerSet = new HashSet<TxEventListener>();

    public ApplicationContext() {
    }

    public static void loadProperties(InputStream is) {
        try {
            props.load(is);
        } catch (IOException e) {
            try {
                is.close();
            } catch (IOException e1) {
                LOG.error(DocumentMessages.DOCS_9999, e1);
            }
        }
    }

    public static void init(File file) throws IOException {
    	ApplicationContextConfig config = ApplicationContextConfig.load(file);
    	for (Property p : config.getProperties()) {
    		props.put(p.getName(), p.getValue());
    	}
    }

    public static void init(Properties p) {
        props.putAll(p);
    }

    public static Properties getProperties() {
        return props;
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static Locale getLang() {
        return DEFAULT_LANG;
    }
    
    public static void getClassPath() {
        LOG.info("context path=" + Thread.currentThread().getContextClassLoader().getResource("").getPath());
        String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String path : paths) {
            LOG.info("path=" + path);
        }
    }
    
    public static synchronized void addBean(Class<?> impl) {
        if (interfaceMap.containsKey(impl)) {
            LOG.warn(DOCS_9006, impl.getName());
        } else {
            interfaceMap.put(impl, impl);
            instanceMap.remove(impl);
        }
    }

    /**
     * beanを登録する
     * 
     * @param itfc beanのインタフェース
     * @param impl beanの実装クラス
     */
    public static synchronized <T> void addBean(Class<T> itfc, Class<? extends T> impl) {
        if (interfaceMap.containsKey(itfc)) {
            LOG.warn(DOCS_9006, itfc.getName());
        } else {
            interfaceMap.put(itfc, impl);
            instanceMap.remove(itfc);
        }
    }

    /**
     * beanを登録する
     * 
     * @param <T>  beanのタイプ
     * @param itfc beanのインターフェース
     * @param c    beanのインスタンス
     */
    public static synchronized <T> void addBean(Class<T> itfc, Function<Class<T>,T> c) {
        if (instanceMap.containsKey(itfc)) {
            LOG.warn(DOCS_9006, itfc.getName());
        } else {
            instanceMap.put(itfc, c.apply(null));
        }
    }

    /**
     * beanをシングルトンで取得する
     * 
     * @param itfc beanのインタフェース
     * @return     bean
     */
    @SuppressWarnings("unchecked")
	public static synchronized <T> T getBean(Class<T> itfc) {
        try {
            if (instanceMap.containsKey(itfc)) {
                return (T)instanceMap.get(itfc);
            } else if (interfaceMap.containsKey(itfc)){
                Class<T> implClass = (Class<T>)interfaceMap.get(itfc);
                if (implClass.getConstructors().length != 1) {
                	throw new BeanNotFoundException("duplicate " + itfc.getName());
                }
                Constructor<?> c = implClass.getConstructors()[0];
                List<Object> params = new ArrayList<>();
                for (Parameter p : c.getParameters()) {
                	params.add(getBean(p.getType()));
                }
                T instance = (T)c.newInstance(params.toArray());

                if (itfc.isInterface()) {
                	Object proxy = Proxy.newProxyInstance(
                        ApplicationContext.class.getClassLoader(),
                        new Class<?>[] { itfc },
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                long st = System.currentTimeMillis();
                                LOG.trace("method[" + method.getName() + "] invoke:" + instance);
                                boolean transactional = isTransactional(implClass, method);
                                sendEvent(transactional, TxEventType.BEGIN, instance, method);
                                try {
                                    Object result = method.invoke(instance, args);
                                    sendEvent(transactional, TxEventType.COMMIT, instance, method);
                                    return result;
                                } catch (Throwable th) {
                                    sendEvent(transactional, TxEventType.ROLLBACK, instance, method);
                                    throw th;
                                } finally {
                                    LOG.trace("method[" + method.getName() + "] end " +
                                            (System.currentTimeMillis() - st) + "ms");
                                }
                            }
                        });
                	instanceMap.put(itfc, proxy);
                	if (TxEventListener.class.isAssignableFrom(implClass)) {
                		addListener((TxEventListener)instance);
                	}
                	return (T)proxy;
                } else {
                	return instance;
                }
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
    private static void addListener(TxEventListener listener) {
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
        Class<?> paramClasses[] = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params[i].getType();
        }
        try {
            Method targetMethod = target.getMethod(method.getName(), paramClasses);
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
    private static void sendEvent(boolean transactional, TxEventType type,
            Object instance, Method method) {
        if (transactional) {
            for (TxEventListener listener : listenerSet) {
                listener.onEvent(new TxEvent(type, instance, method));
            }
        }
    }
}
