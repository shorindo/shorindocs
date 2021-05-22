package com.shorindo.docs.datagrid.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shorindo.docs.datagrid.DataGridService.ColumnModel;
import com.shorindo.docs.datagrid.DataGridService.SchemaModel;

public class SchemaEntity {
    @JacksonXmlProperty(localName = "name", isAttribute=true)
    private String name;

    @JacksonXmlElementWrapper(localName = "columns")
    @JacksonXmlProperty(localName = "column")
    private List<ColumnEntity> columns = new ArrayList<>();

    public String getName() {
        return name;
    }

    public List<ColumnEntity> getColumns() {
        return columns;
    }

}
