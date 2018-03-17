/*
 * Copyright 2016 Shorindo, Inc.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class SampleEntity extends SchemaEntity {
    public enum SampleTypes implements SchemaType {
        STRING_VALUE ("varchar", 36, 0, 1, true, false, null),
        BYTE_VALUE ("byte", 0, 0, 0, false, false, null),
        BYTE_OBJECT ("byte", 0, 0, 0, false, false, null),
        SHORT_VALUE ("smallint", 0, 0, 0, false, false, null),
        INT_VALUE ("int", 0, 0, 0, false, false, null),
        LONG_VALUE ("long", 0, 0, 0, false, false, null),
        FLOAT_VALUE ("float", 0, 0, 0, false, false, null),
        DOUBLE_VALUE ("double", 0, 0, 0, false, false, null),
        DATE_VALUE ("Date", 0, 0, 0, false, false, null),
        TIMESTAMP_VALUE ("Timestamp", 0, 0, 0, false, false, null),
        ;

        private String jdbcType;
        private String javaType;
        private int size;
        private int precision;
        private int primary;
        private boolean notNull;
        private boolean unique;
        private Object defaultValue;
        private Field field;
        private Method setMethod;
        private Method getMethod;

        private SampleTypes(String jdbcType, int size, int precision,
                int primary, boolean notNull, boolean unique, Object defaultValue) {
            this.jdbcType = jdbcType;
            this.size = size;
            this.precision = precision;
            this.precision = primary;
            this.notNull = notNull;
            this.unique = unique;
            this.defaultValue = defaultValue;

            String beanName = BeanUtil.snake2camel(name(), false);
            try {
                field = SampleEntity.class.getDeclaredField(beanName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            }

            String setterName = "set" + BeanUtil.snake2camel(name(), true);
            try {
                setMethod = SampleEntity.class.getMethod(setterName, field.getType());
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            }

            String getterName = "get" + BeanUtil.snake2camel(name(), true);
            try {
                getMethod = SampleEntity.class.getMethod(getterName);
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            }
        }

        @Override public String getColumnName() {
            return name();
        }
        @Override public String getType() {
            return jdbcType;
        }
        @Override public int getSize() {
            return size;
        }
        @Override public int getPrecision() {
            return precision;
        }
        @Override public int getPrimary() {
            return primary;
        }
        @Override public boolean isNotNull() {
            return notNull;
        }
        @Override public boolean isUnique() {
            return unique;
        }
        @Override public Object getDefault() {
            return defaultValue;
        }
        @Override public Field getField() {
            return field;
        }
        @Override public Method getSetMethod() {
            return setMethod;
        }
        @Override public Method getGetMethod() {
            return getMethod;
        }
    }

    private static final ActionLogger LOG = ActionLogger.getLogger(SampleEntity.class);
    private static final String ENTITY_NAME = "SAMPLE";

    private String stringValue;
    private byte byteValue;
    private Byte byteObject;
    private short shortValue;
    private int intValue;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private java.util.Date dateValue;
    private java.sql.Timestamp timestampValue;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }
    @Override
    public SchemaType[] getTypes() {
        return SampleTypes.values();
    }
    @Override
    public SchemaType getType(String name) {
        return SampleTypes.valueOf(name);
    }

    public String getStringValue() {
        return stringValue;
    }
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    public byte getByteValue() {
        return byteValue;
    }
    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }
    public Byte getByteObject() {
        return byteObject;
    }
    public void setByteObject(Byte byteObject) {
        this.byteObject = byteObject;
    }
    public short getShortValue() {
        return shortValue;
    }
    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }
    public int getIntValue() {
        return intValue;
    }
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
    public long getLongValue() {
        return longValue;
    }
    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }
    public float getFloatValue() {
        return floatValue;
    }
    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }
    public double getDoubleValue() {
        return doubleValue;
    }
    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }
    public java.util.Date getDateValue() {
        return dateValue;
    }
    public void setDateValue(java.util.Date dateValue) {
        this.dateValue = dateValue;
    }
    public java.sql.Timestamp getTimestampValue() {
        return timestampValue;
    }
    public void setTimestampValue(java.sql.Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }
}
