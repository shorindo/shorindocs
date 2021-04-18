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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.shorindo.docs.ApplicationContextConfig.Action;
import com.shorindo.docs.ApplicationContextConfig.Bean;
import com.shorindo.docs.ApplicationContextConfig.Property;
import com.shorindo.docs.TxEvent.TxEventType;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.Transactional;

/**
 * 
 */
public class ApplicationContext {
    private static final Locale DEFAULT_LANG = Locale.JAPANESE;
    private static final ActionLogger LOG = ActionLogger.getLogger(ApplicationContext.class);
    private static final Properties props = new Properties();
    public static final String WEB_INF_CLASSES = "/WEB-INF/classes";
    public static final String WEB_INF_LIB = "/WEB-INF/lib";
    private static Map<Class<?>,Class<?>> interfaceMap = new ConcurrentHashMap<>();
    private static Map<Class<?>,Object> instanceMap = new ConcurrentHashMap<>();
    private static Map<String,Object> actionMap = new ConcurrentHashMap<>();
    private static Set<TxEventListener> listenerSet = new HashSet<TxEventListener>();

    private ApplicationContext() {
    }

    public static ApplicationContextConfig load(File file) throws IOException {
    	ApplicationContextConfig config = ApplicationContextConfig.load(file);
    	evaluate(config);
    	return config;
    }

    public static ApplicationContextConfig load(String xml) throws IOException {
    	ApplicationContextConfig config = ApplicationContextConfig.load(xml);
		LOG.info(APPL_004, config.getNamespace());
    	evaluate(config);
    	return config;
    }

	private static void evaluate(ApplicationContextConfig config) {
    	for (Property prop : config.getProperties()) {
    		props.put(prop.getName(), prop.getValue());
    	}
    	for (Bean bean : config.getBeans()) {
    		try {
    			String name = bean.getName();
    			String clazz = bean.getClassName();
    			if (clazz == null) {
    				addBean(Class.forName(name));
    			} else {
    				Class<?> iface = Class.forName(name);
    				Class<?> impl = Class.forName(clazz);
    				if (iface.isAssignableFrom(impl)) {
    					addBean(iface, impl);
    				} else {
    					// TODO ERROR
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	for (Action action : config.getActions()) {
    		if (actionMap.containsKey(action.getPath())) {
    			LOG.warn("duplication action path={0}", action.getPath());
    		} else {
    			try {
    				LOG.info(APPL_003, action.getPath(), action.getName());
					Class<?> clazz = Class.forName(action.getName());
					Object impl = instanceMap.get(clazz);
					if (impl != null) {
						actionMap.put(action.getPath(), impl);
					} else {
						addBean(clazz);
						actionMap.put(action.getPath(), getBean(clazz));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
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
        	LOG.info(APPL_001, impl.getName());
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
    public static synchronized <T> void addBean(Class<T> itfc, Class<?> impl) {
        if (interfaceMap.containsKey(itfc)) {
            LOG.warn(DOCS_9006, itfc.getName());
        } else {
        	LOG.info(APPL_002, itfc.getName(), impl.getName());
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
                throw new BeanNotFoundException("No implementation defined for '" + itfc + "'.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAction(String path) {
    	
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
