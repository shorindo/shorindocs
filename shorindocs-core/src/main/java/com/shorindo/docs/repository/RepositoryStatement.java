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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    protected Class<?> clazz;
    protected String tableName;
    protected Map<Field, ColumnMapper> columnMap;
    protected Map<String,ColumnMapper> namedMap;
    protected String sql;

    public RepositoryStatement(Class<?> clazz) throws RepositoryException {
        this.clazz = clazz;
        applyTableName(clazz);
        applyColumnMapper(clazz);
    }

    public String getTableName() {
        return tableName;
    }

    public Map<Field,ColumnMapper> getColumnMapperList() {
        return columnMap;
    }

    public ColumnMapper getColumnByName(String columnName) {
        return namedMap.get(columnName);
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
        namedMap = new LinkedHashMap<String,ColumnMapper>();
        for (Field field : clazz.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }
            ColumnMapper value = new ColumnMapper(field, column);
            columnMap.put(field, value);
            namedMap.put(column.name(), value);
        }
        columnMap = Collections.unmodifiableMap(columnMap);
        namedMap = Collections.unmodifiableMap(namedMap);
    }

    protected void setPlaceHolder(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value != null) {
            switch(FieldTypes.of(value.getClass())) {
            case STRING:
                stmt.setString(index++, (String)value);
                break;
            case INTEGER:
                stmt.setInt(index++, (int)value);
                break;
            case INTEGER_OBJECT:
                stmt.setInt(index++, (Integer)value);
                break;
            case LONG:
                stmt.setLong(index++, (long)value);
                break;
            case LONG_OBJECT:
                stmt.setLong(index++, (Long)value);
                break;
            case DATE:
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                stmt.setString(index++, format.format((Date)value));
                break;
            case BOOLEAN:
                stmt.setBoolean(index++, (boolean)value);
                break;
            case BOOLEAN_OBJECT:
                stmt.setBoolean(index++, (Boolean)value);
                break;
            case BYTE:
                stmt.setByte(index++, (byte)value);
                break;
            case BYTE_OBJECT:
                stmt.setByte(index++, (Byte)value);
                break;
            case SHORT:
                stmt.setShort(index++, (short)value);
                break;
            case SHORT_OBJECT:
                stmt.setShort(index++, (Short)value);
                break;
            case FLOAT:
                stmt.setFloat(index++, (float)value);
                break;
            case FLOAT_OBJECT:
                stmt.setFloat(index++, (Float)value);
                break;
            case DOUBLE:
                stmt.setDouble(index++, (double)value);
                break;
            case DOUBLE_OBJECT:
                stmt.setDouble(index++, (Double)value);
                break;
            case BIGDECIMAL:
                stmt.setBigDecimal(index++, (BigDecimal)value);
                break;
            default:
                LOG.warn(DBMS_5129, String.valueOf(index), value.getClass());
                stmt.setObject(index++, value);
            }
        } else {
            stmt.setObject(index++, null);
        }
    }

    protected void dispose(Statement stmt) {
        if (stmt != null)
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.warn(DBMS_9999, e);
            }
    }

    protected void dispose(ResultSet rset) {
        if (rset != null)
            try {
                rset.close();
            } catch (SQLException e) {
                LOG.warn(DBMS_9999, e);
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
