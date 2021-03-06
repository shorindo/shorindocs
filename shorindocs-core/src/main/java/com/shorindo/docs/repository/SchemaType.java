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
package com.shorindo.docs.repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 */
public interface SchemaType {
    public String getColumnName();
    public String getType();
    public int getSize();
    public int getPrecision();
    public int getPrimary();
    public boolean isNotNull();
    public boolean isUnique();
    public Object getDefault();
    public Field getField();
    public Method getSetMethod();
    public Method getGetMethod();
}
