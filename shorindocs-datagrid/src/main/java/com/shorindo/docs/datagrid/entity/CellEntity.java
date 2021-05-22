package com.shorindo.docs.datagrid.entity;

public class CellEntity {
    private int id;
    private String name;
    private Object value;

    public CellEntity() {
    }
    public CellEntity id(int id) {
        this.id = id;
        return this;
    }
    public CellEntity name(String name) {
        this.name = name;
        return this;
    }
    public CellEntity value(Object value) {
        this.value = value;
        return this;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Object getValue() {
        return value;
    }
}
