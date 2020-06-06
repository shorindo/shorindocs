/*
 * Copyright 2015-2018 Shorindo, Inc.
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */
@XmlRootElement(name="schema")
public class DatabaseSchema {
    private String namespace;
    private List<Entity> entityList = new ArrayList<Entity>();
    private List<Relation> relationList = new ArrayList<Relation>();
    
    @XmlAttribute(name="namespace")
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @XmlElements({
        @XmlElement(name="table", type=Table.class),
        @XmlElement(name="view",  type=View.class)
    })
    public List<Entity> getEntityList() {
        return entityList;
    }
    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }
    public Entity getEntity(String name) {
        for (Entity entity : entityList) {
            if (name.equals(entity.getName())) {
                return entity;
            }
        }
        return null;
    }
    public DatabaseSchema addEntity(Entity entity) {
        entityList.add(entity);
        return this;
    }

    public static interface Entity {
        public String getName();
        public String getJavaClass();
        public Column getColumn(String name);
        public List<Column> getColumnList();
    }

    public static class Table implements Entity {
        private String name;
        private String javaClass;
        private List<String> aliasList = new ArrayList<String>();
        private List<Column> columnList = new ArrayList<Column>();

        @Override
        public String getName() {
            return name;
        }
        public Table setName(String name) {
            this.name = name;
            return this;
        }
        @XmlElement(name="alias")
        public List<String> getAliasList() {
            return aliasList;
        }
        public Table setAliasList(List<String> aliasList) {
            this.aliasList = aliasList;
            return this;
        }
        public Table addAlias(String alias) {
            aliasList.add(alias);
            return this;
        }
        public String getJavaClass() {
            return javaClass;
        }
        public void setJavaClass(String javaClass) {
            this.javaClass = javaClass;
        }
        @XmlElement(name="column")
        public List<Column> getColumnList() {
            return columnList;
        }
        public Table setColumnList(List<Column> columnList) {
            this.columnList = columnList;
            return this;
        }
        public Table addColumn(Column column) {
            columnList.add(column);
            return this;
        }
        public Column getColumn(String name) {
            for (Column column : columnList) {
                if (name.equals(column.getName())) {
                    return column;
                }
            }
            return null;
        }
    }

    public static class Column {
        private String name;
        private List<String> aliasList;
        private String type;
        private String javaType;
        private int size;
        private int precision;
        private int primaryKey = 0;
        private boolean notNull = false;
        private boolean unique = false;

        public String getName() {
            return name;
        }
        public Column setName(String name) {
            this.name = name;
            return this;
        }
        @XmlElement(name="alias")
        public List<String> getAliasList() {
            return aliasList;
        }
        public Column setAliasList(List<String> aliasList) {
            this.aliasList = aliasList;
            return this;
        }
        public Column addAlias(String alias) {
            aliasList.add(alias);
            return this;
        }
        public String getType() {
            return type;
        }
        public Column setType(String type) {
            this.type = type;
            return this;
        }
        public String getJavaType() {
            return javaType;
        }
        public Column setJavaType(String javaType) {
            this.javaType = javaType;
            return this;
        }
        public int getSize() {
            return size;
        }
        public Column setSize(int size) {
            this.size = size;
            return this;
        }
        public int getPrecision() {
            return precision;
        }
        public Column setPrecision(int precision) {
            this.precision = precision;
            return this;
        }
        public boolean isNotNull() {
            return notNull;
        }
        public Column setNotNull(boolean notNull) {
            this.notNull = notNull;
            return this;
        }
        public boolean isUnique() {
            return unique;
        }
        public void setUnique(boolean unique) {
            this.unique = unique;
        }
        public int getPrimaryKey() {
            return primaryKey;
        }
        public void setPrimaryKey(int primaryKey) {
            this.primaryKey = primaryKey;
        }
    }

    public static class View implements Entity {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public Column getColumn(String name) {
            return null;
        }

        @Override
        public List<Column> getColumnList() {
            return null;
        }

        @Override
        public String getJavaClass() {
            return null;
        }
    }

    public static class Relation {
    }
}
