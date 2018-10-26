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

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.database.SchemaEntity;

/**
 * 
 */
@Table("SAMPLE")
public class SampleEntity extends SchemaEntity {
    private static final ActionLogger LOG = ActionLogger.getLogger(SampleEntity.class);
    
    @Column(name="STRING_VALUE", typeName="varchar", size=80, primaryKey=1)
    private String stringValue;

    @Column(name="BYTE_VALUE", typeName="byte")
    private byte byteValue;

    @Column(name="BYTE_OBJECT", typeName="byte")
    private Byte byteObject;

    @Column(name="SHORT_VALUE", typeName="short")
    private short shortValue;

    @Column(name="SHORT_OBJECT", typeName="short")
    private short shortObject;

    @Column(name="INT_VALUE", typeName="int")
    private int intValue;

    @Column(name="INT_OBJECT", typeName="int")
    private Integer intObject;

    @Column(name="LONG_VALUE", typeName="long")
    private long longValue;

    @Column(name="LONG_OBJECT", typeName="long")
    private Long longObject;

    @Column(name="FLOAT_VALUE", typeName="float")
    private float floatValue;

    @Column(name="FLOAT_OBJECT", typeName="float")
    private Float floatObject;

    @Column(name="DOUBLE_VALUE", typeName="double")
    private double doubleValue;

    @Column(name="DOUBLE_OBJECT", typeName="double")
    private Double doubleObject;

    @Column(name="DATE_VALUE", typeName="date")
    private java.util.Date dateValue;

    @Column(name="TIMESTAMP_VALUE", typeName="timestamp")
    private java.sql.Timestamp timestampValue;

    public SampleEntity() throws DatabaseException {
        super();
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

    public short getShortObject() {
        return shortObject;
    }

    public void setShortObject(short shortObject) {
        this.shortObject = shortObject;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Integer getIntObject() {
        return intObject;
    }

    public void setIntObject(Integer intObject) {
        this.intObject = intObject;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public long getLongObject() {
        return longObject;
    }

    public void setLongObject(long longObject) {
        this.longObject = longObject;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public float getFloatObject() {
        return floatObject;
    }

    public void setFloatObject(float floatObject) {
        this.floatObject = floatObject;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public double getDoubleObject() {
        return doubleObject;
    }

    public void setDoubleObject(double doubleObject) {
        this.doubleObject = doubleObject;
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
