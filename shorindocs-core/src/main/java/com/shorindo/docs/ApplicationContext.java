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

import static com.shorindo.docs.ApplicationMessages.*;
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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.shorindo.docs.ApplicationContextConfig.Action;
import com.shorindo.docs.ApplicationContextConfig.Bean;
import com.shorindo.docs.ApplicationContextConfig.Include;
import com.shorindo.docs.ApplicationContextConfig.Property;
import com.shorindo.docs.TxEvent.TxEventType;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.Transactional;

/**
 * 
 */
public class ApplicationContext {
    private static final Locale DEFAULT_LANG = Locale.JAPANESE;
    private static final ActionLogger LOG = ActionLogger.getLogger(ApplicationContext.class);
    private static ApplicationContext rootContext;
    private String namespace;
    private Properties props = new Properties();
    private Map<String,ApplicationContext> pluginMap = new ConcurrentHashMap<>();
    private Map<Class<?>,Class<?>> interfaceMap = new ConcurrentHashMap<>();
    private Map<Class<?>,Object> instanceMap = new ConcurrentHashMap<>();
    private Map<Pattern,Object> actionMap = new ConcurrentHashMap<>();
    private Set<TxEventListener> listenerSet = new HashSet<TxEventListener>();

    public static void addContext(String namespace, ApplicationContext context) {
        rootContext.pluginMap.put(namespace, context);
    }

    public static ApplicationContext getPlugin(String namespace) {
        return rootContext.pluginMap.get(namespace);
    }

    public static void init(InputStream is) throws IOException {
        rootContext = load(is);
    }

    public static ApplicationContext load(InputStream is) throws IOException {
        ApplicationContextConfig config = ApplicationContextConfig.load(is);
        ApplicationContext context = new ApplicationContext(config);
        return context;
    }

    /**
     * @param config
     */
    private ApplicationContext(ApplicationContextConfig config) {
        this.namespace = config.getNamespace();
        if (namespace != null && "".equals(namespace)) {
            rootContext.pluginMap.put(namespace, this);
        }

        for (Include include : config.getIncludes()) {
            LOG.debug("include({0})", include.getFile());
            include(include.getFile());
        }
        for (Property prop : config.getProperties()) {
            props.put(prop.getName(), prop.getValue());
        }
        for (Bean bean : config.getBeans()) {
            try {
                String name = bean.getName();
                String clazz = bean.getClassName();
                if (clazz == null) {
                    Class<?> cls = Class.forName(name);
                    addBeanPrivate(cls);
                } else {
                    Class<?> iface = Class.forName(name);
                    Class<?> impl = Class.forName(clazz);
                    if (iface.isAssignableFrom(impl)) {
                        addBeanPrivate(iface, impl);
                    } else {
                        throw new BeanNotFoundException(name + " -> " + clazz);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Action action : config.getActions()) {
            if (actionMap.containsKey(action.getPath())) {
                LOG.warn(APPL_3001, action.getPath());
            } else {
                try {
                    LOG.info(APPL_003, action.getPath(), action.getName());
                    Class<?> clazz = Class.forName(action.getName());
                    if (!ActionController.class.isAssignableFrom(clazz)) {
                        throw new BeanNotFoundException(action.getName() + " is not ActionController");
                    }
                    Object impl = instanceMap.get(clazz);
                    if (impl != null) {
                        actionMap.put(Pattern.compile(action.getPath()), impl);
                    } else {
                        addBeanPrivate(clazz);
                        actionMap.put(Pattern.compile(action.getPath()), getBeanPrivate(clazz));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BeanNotFoundException(action.getName() + " -> " + action.getName());
                }
            }
        }
    }

    private void include(String fileName) {
        try (InputStream is = ApplicationContext.class.getClassLoader().getResourceAsStream(fileName)) {
            ApplicationContext context = load(is);
            // TODO
            for (Entry<Object,Object> prop : context.props.entrySet()) {
                props.put(prop.getKey(), prop.getValue());
            }
        } catch (Exception e) {
            LOG.error(APPL_5001, e, fileName);
        }
    }

    /**
     * 
     * @return
     */
    public static Properties getProperties() {
        return rootContext.props;
    }

    /**
     * 
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return rootContext.props.getProperty(key);
    }

    /**
     * 
     * @return
     */
    public static Locale getLang() {
        return DEFAULT_LANG;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * 
     */
    public static void getClassPath() {
        LOG.info("context path=" + Thread.currentThread().getContextClassLoader().getResource("").getPath());
        String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String path : paths) {
            LOG.info("path=" + path);
        }
    }

    /**
     * @param impl
     */
    public static synchronized void addBean(Class<?> impl) {
        rootContext.addBeanPrivate(impl);
    }

    /**
     * @param impl
     */
    private synchronized void addBeanPrivate(Class<?> impl) {
        if (interfaceMap.containsKey(impl)) {
            LOG.warn(DOCS_9006, impl.getName());
        } else {
            LOG.info(APPL_001, impl.getName());
            interfaceMap.put(impl, impl);
            instanceMap.remove(impl);
        }
    }

    public <T> List<T> findBeans(Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Entry<Class<?>,Class<?>> entry : interfaceMap.entrySet()) {
            if (clazz.isAssignableFrom(entry.getKey())) {
                result.add((T)getBean(entry.getKey()));
            }
        }
        return result;
    }

    /**
     * beanを登録する
     * 
     * @param itfc beanのインタフェース
     * @param impl beanの実装クラス
     */
    public static synchronized <T> void addBean(Class<T> itfc, Class<?> impl) {
        rootContext.addBeanPrivate(itfc, impl);
    }

    private synchronized <T> void addBeanPrivate(Class<T> itfc, Class<?> impl) {
        if (interfaceMap.containsKey(itfc)) {
            LOG.warn(DOCS_9006, itfc.getName());
        } else {
            LOG.info(APPL_002, itfc.getName(), impl.getName());
            interfaceMap.put(itfc, impl);
            instanceMap.remove(itfc);
        }
    }

//    public static <T> List<Object> findBeans(Class<T> clazz) {
//        return rootContext.findBeansPrivate(clazz);
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> List<Object> findBeansPrivate(Class<T> clazz) {
//        List<Object> result = new ArrayList<>();
//        for (Entry<Class<?>,Class<?>> entry : interfaceMap.entrySet()) {
//            if (clazz.isAssignableFrom(entry.getKey())) {
//                result.add(entry.getKey());
//            }
//        }
//        for (Entry<String,ApplicationContext> entry : contextMap.entrySet()) {
//            result.addAll(entry.getValue().findBeansPrivate(clazz));
//        }
//        return result;
//    }

    /**
     * beanをシングルトンで取得する
     * 
     * @param itfc beanのインタフェース
     * @return     bean
     */
    public static synchronized <T> T getBean(Class<T> itfc) {
        try {
            return rootContext.getBeanPrivate(itfc);
        } catch (BeanNotFoundException e) {
            for (Entry<String,ApplicationContext> entry : rootContext.pluginMap.entrySet()) {
                try {
                    return entry.getValue().getBeanPrivate(itfc);
                } catch (BeanNotFoundException ex) {
                }
            }
        }
        throw new BeanNotFoundException(itfc.getName());
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> T getBeanPrivate(Class<T> itfc) throws BeanNotFoundException {
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
                throw new BeanNotFoundException("No implementation defined for '" + itfc + "'.");
            }
        } catch (Exception e) {
            throw new BeanNotFoundException(e);
        }
    }

    /**
     * 
     * @param path
     * @return
     */
    public static Object getAction(String path) {
        return rootContext.getActionPrivate(path);
    }

    private Object getActionPrivate(String path) {
        return actionMap.entrySet()
            .stream()
            .filter(e -> { return e.getKey().matcher(path).matches(); })
            .map(e -> { return e.getValue(); })
            .findFirst()
            .orElse(null);
    }

    /**
     * トランザクションリスナーを登録する
     */
    private void addListener(TxEventListener listener) {
        listenerSet.add(listener);
    }

    /**
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

    /**
     * トランザクションリスナーにイベントを送信する
     */
    private void sendEvent(boolean transactional, TxEventType type,
            Object instance, Method method) {
        if (transactional) {
            for (TxEventListener listener : listenerSet) {
                listener.onEvent(new TxEvent(type, instance, method));
            }
        }
    }

    public static Map<String,ApplicationContext> getPlugins() {
        return rootContext.pluginMap;
    }
}
