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
package com.shorindo.docs.repository;

import static com.shorindo.docs.repository.DatabaseMessages.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public abstract class RepositoryStatement {
    private static final ActionLogger LOG =
            ActionLogger.getLogger(RepositoryStatement.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected String tableName;
    protected Map<Field, ColumnMapper> columnMap;
    protected String sql;

    public RepositoryStatement(Class<?> clazz) throws RepositoryException {
        applyTableName(clazz);
        applyColumnMapper(clazz);
    }

    public String getTableName() {
        return tableName;
    }

    public Map<Field,ColumnMapper> getColumnMapperList() {
        return columnMap;
    }

    private void applyTableName(Class<?> clazz) throws RepositoryException {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.value();
        } else {
            throw new RepositoryException(DBMS_5125);
        }
    }

    private void applyColumnMapper(Class<?> clazz) {
        columnMap = new LinkedHashMap<Field,ColumnMapper>();
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            columnMap.put(field, new ColumnMapper(field, column));
        }
        columnMap = Collections.unmodifiableMap(columnMap);
    }

    protected void setHolders(PreparedStatement stmt, int index, ColumnMapper entry, Object entity) throws IllegalArgumentException, IllegalAccessException, SQLException {
        Object value;
        Field field = entry.getField();
        field.setAccessible(true);
        switch(entry.getFieldType()) {
        case STRING:
            stmt.setString(index++, (String)field.get(entity));
            break;
        case INTEGER:
            stmt.setInt(index++, (int)field.get(entity));
            break;
        case INTEGER_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setInt(index++, (Integer)value);
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case LONG:
            stmt.setLong(index++, (long)field.get(entity));
            break;
        case LONG_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setLong(index++, (Long)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case DATE:
            value = field.get(entity);
            if (value != null) {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                stmt.setString(index++, format.format((Date)field.get(entity)));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case BOOLEAN:
            stmt.setBoolean(index++, (boolean)field.get(entity));
            break;
        case BOOLEAN_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setBoolean(index++, (Boolean)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case BYTE:
            stmt.setByte(index++, (byte)field.get(entity));
            break;
        case BYTE_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setByte(index++, (Byte)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case SHORT:
            stmt.setShort(index++, (short)field.get(entity));
            break;
        case SHORT_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setShort(index++, (Short)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case FLOAT:
            stmt.setFloat(index++, (float)field.get(entity));
            break;
        case FLOAT_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setFloat(index++, (Float)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case DOUBLE:
            stmt.setDouble(index++, (double)field.get(entity));
            break;
        case DOUBLE_OBJECT:
            value = field.get(entity);
            if (value != null) {
                stmt.setDouble(index++, (Double)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        case BIGDECIMAL:
            value = field.get(entity);
            if (value != null) {
                stmt.setBigDecimal(index++, (BigDecimal)field.get(entity));
            } else {
                stmt.setObject(index++, null);
            }
            break;
        default:
            stmt.setObject(index++, null);
            LOG.warn(DBMS_5129, field.getName(), field.getType());
        }
    }

    protected static class ColumnMapper {
        private Field field;
        private FieldTypes fieldType;
        private Column column;

        public ColumnMapper(Field field, Column column) {
            this.field = field;
            this.fieldType = FieldTypes.of(field.getType());
            this.column = column;
        }
        public Field getField() {
            return field;
        }
        public FieldTypes getFieldType() {
            return fieldType;
        }
        public Column getColumn() {
            return column;
        }
    }

    protected enum FieldTypes {
        STRING(String.class),
        INTEGER(int.class),
        INTEGER_OBJECT(Integer.class),
        LONG(long.class),
        LONG_OBJECT(Long.class),
        DATE(Date.class),
        BOOLEAN(boolean.class),
        BOOLEAN_OBJECT(Boolean.class),
        BYTE(byte.class),
        BYTE_OBJECT(Byte.class),
        SHORT(short.class),
        SHORT_OBJECT(Short.class),
        FLOAT(float.class),
        FLOAT_OBJECT(Float.class),
        DOUBLE(double.class),
        DOUBLE_OBJECT(Double.class),
        BIGDECIMAL(BigDecimal.class),
        OBJECT(Object.class);

        private Class<?> clazz;

        private FieldTypes(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getType() {
            return clazz;
        }

        public static FieldTypes of(Class<?> clazz) {
            for (FieldTypes types : values()) {
                if (types.getType().isAssignableFrom(clazz)) {
                    return types;
                }
            }
            return null;
        }
    }
}
