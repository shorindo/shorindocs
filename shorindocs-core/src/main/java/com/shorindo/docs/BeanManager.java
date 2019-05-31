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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.shorindo.docs.annotation.Implementation;

/**
 * 
 */
@SuppressWarnings("unchecked")
public abstract class BeanManager {
    private static Map<Class<?>,Class<?>> injectMap = new ConcurrentHashMap<Class<?>,Class<?>>();
    private static Map<Class<?>,Object> instanceMap = new ConcurrentHashMap<Class<?>,Object>();

    public static <T> void bind(Class<T> itfc, Class<? extends T> impl) {
        injectMap.put(itfc, impl);
        instanceMap.remove(itfc);
    }

    public static <T> T inject(Class<T> itfc) {

        try {
            if (instanceMap.containsKey(itfc)) {
                return (T)instanceMap.get(itfc);
            } else if (injectMap.containsKey(itfc)){
                T instance = (T)injectMap.get(itfc).newInstance();
                instanceMap.put(itfc, instance);
                return instance;
            } else if (itfc.getAnnotation(Implementation.class) != null) {
                return (T)itfc.getAnnotation(Implementation.class).value().newInstance();
            } else {
                throw new InstantiationException("No bean defined for '" + itfc + "'.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
