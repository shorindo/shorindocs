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
package com.shorindo.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * 
 */
public class MockUp<T> {
    private Class<?> targetClass;

    public MockUp() throws Exception {
        for (Type type : getGenericType(this, MockUp.class)) {
            targetClass = Class.forName(type.getTypeName());
            break;
        }
    }

    static Type[] getGenericType( Object obj, Class type ) {
        Class cl = obj.getClass();
        while ( cl != null  ) {
            Type t = cl.getGenericSuperclass();
            Type[] res = _getGenericType( type, t );
            if ( res != null ) {
                return res;
            }
            for ( Type t2 : cl.getGenericInterfaces() ) {
                res = _getGenericType( type, t2 );
                if ( res != null ) {
                    return res;
                }
            }
            cl = cl.getSuperclass();
        }
        return new Type[0];
    }

    private static Type[] _getGenericType( Class type, Type t ) {
        if ( t != null
            && t instanceof ParameterizedType
            && type.equals( ((ParameterizedType) t).getRawType()  ) ) {
            return type != null ? ((ParameterizedType) t).getActualTypeArguments() : new Type[0];
        }
        return null;
    }

    public static interface Target {
        public void foo();
        public void bar(int a, int b);
    }

    public static class TargetImpl implements Target {
        public void foo() {
            
        }
        public void bar(int a, int b) {
            
        }
    }

    public static void main(String[] args) throws Exception {
        MockUp<TargetImpl> mock = new MockUp<TargetImpl>() {
            
        };
    }
}
