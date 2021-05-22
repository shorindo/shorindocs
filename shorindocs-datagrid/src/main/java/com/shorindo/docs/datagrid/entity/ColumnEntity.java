package com.shorindo.docs.datagrid.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shorindo.docs.datagrid.DataGridService.ColumnModel;

public class ColumnEntity implements ColumnModel {
    @JacksonXmlProperty(localName="id", isAttribute=true)
    private int id;

    @JacksonXmlProperty(localName="name", isAttribute=true)
    private String name;

    @JacksonXmlProperty(localName="type", isAttribute=true)
    private String type;

    @JacksonXmlProperty(localName="required", isAttribute=true)
    private boolean required;

    @JacksonXmlProperty(localName="style", isAttribute=true)
    private String style;

    @JacksonXmlProperty(localName="defaultValue", isAttribute=true)
    private String defaultValue;

    private String value;

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public String getStyle() {
        return style;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    public String getValue() {
        return value;
    }
}
