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
package com.shorindo.docs.database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocsMessages;

/**
 * 
 */
public abstract class SchemaEntity {
    private static final ActionLogger LOG = ActionLogger.getLogger(SchemaEntity.class);

    public abstract String getEntityName();
    public abstract SchemaType[] getTypes();
    public abstract SchemaType getType(String name);

    public final void setByName(String name, Object value) throws DatabaseException {
        SchemaType type = getType(name);
        if (type != null) {
            try {
                Method method = type.getSetMethod();
                method.invoke(this, value);
            } catch (IllegalAccessException e) {
                throw new DatabaseException(e);
            } catch (IllegalArgumentException e) {
                throw new DatabaseException(e);
            } catch (InvocationTargetException e) {
                throw new DatabaseException(e);
            }
        } else {
            throw new DatabaseException(DocsMessages.E_5124.getMessage(name));
        }
    }

    public final Object getByName(String name) throws DatabaseException {
        SchemaType type = getType(name);
        if (type != null) {
            try {
                Method method = type.getGetMethod();
                return method.invoke(this);
            } catch (IllegalAccessException e) {
                throw new DatabaseException(e);
            } catch (IllegalArgumentException e) {
                throw new DatabaseException(e);
            } catch (InvocationTargetException e) {
                throw new DatabaseException(e);
            }
        } else {
            throw new DatabaseException(DocsMessages.E_5124.getMessage(name));
        }
    }
}
