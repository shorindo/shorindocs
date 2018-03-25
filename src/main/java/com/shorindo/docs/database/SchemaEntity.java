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

import static com.shorindo.docs.database.DatabaseMessages.*;

import java.lang.reflect.Field;
import java.util.Locale;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.ApplicationContext;

/**
 * 
 */
public abstract class SchemaEntity {
    private static final ActionLogger LOG = ActionLogger.getLogger(SchemaEntity.class);
    private static final Locale LANG = ApplicationContext.getLang();

    public SchemaEntity() throws DatabaseException {
        int count = 0;
        for (Field field : getClass().getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                count++;
            }
        }
        if (count == 0) {
            throw new DatabaseException("カラム指定アノテーションが１つもありません");
        }
    }

    public final String getEntityName() throws DatabaseException {
        Table table = getClass().getAnnotation(Table.class);
        if (table != null) {
            return table.value();
        } else {
            throw new DatabaseException(DTBS_5125);
        }
    }

//    public final void setByName(String name, Object value) throws DatabaseException {
//        if (type != null) {
//            try {
//                Method method = type.getSetMethod();
//                method.invoke(this, value);
//            } catch (IllegalAccessException e) {
//                throw new DatabaseException(e);
//            } catch (IllegalArgumentException e) {
//                throw new DatabaseException(e);
//            } catch (InvocationTargetException e) {
//                throw new DatabaseException(e);
//            }
//        } else {
//            throw new DatabaseException(DocsMessages.E_5124.getMessage(name));
//        }
//    }
//
    public final Object getByName(String name) throws DatabaseException {
        for (Field field : getClass().getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && name.equals(column.name())) {
                try {
                    field.setAccessible(true);
                    return field.get(this);
                } catch (IllegalArgumentException e) {
                    throw new DatabaseException(DTBS_5119.getMessage(LANG, name));
                } catch (IllegalAccessException e) {
                    throw new DatabaseException(DTBS_5119.getMessage(LANG, name));
                }
            }
        }
        throw new DatabaseException(DTBS_5124.getMessage(LANG, name));
    }

    /**
     * 
     */
    public DatabaseSchema.Table getTableSchema() throws DatabaseException {
        DatabaseSchema.Table tableSchema = new DatabaseSchema.Table();
        tableSchema.setName(getEntityName());

        for (Field field : getClass().getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                DatabaseSchema.Column columnSchema = new DatabaseSchema.Column();
                columnSchema.setName(column.name());
                columnSchema.setType(column.typeName());
                columnSchema.setJavaType(field.getType().toString());
                columnSchema.setSize(column.size());
                columnSchema.setPrecision(column.precision());
                columnSchema.setPrimaryKey(column.primaryKey());
                columnSchema.setUnique(column.unique());
                columnSchema.setNotNull(column.notNull());
                tableSchema.addColumn(columnSchema);
            }
        }
        if (tableSchema.getColumnList().size() == 0) {
            throw new DatabaseException(DTBS_5126.getMessage(LANG));
        }

        return tableSchema;
    }
}
