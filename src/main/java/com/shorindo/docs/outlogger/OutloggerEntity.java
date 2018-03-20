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
package com.shorindo.docs.outlogger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("DOCS_OUTLOGGER")
public class OutloggerEntity extends SchemaEntity {
    public enum OutloggerTypes implements SchemaType {
        DOCUMENT_ID     ("varchar", 36, 0, 1, true, false, null),
        LOG_ID          ("varchar", 36, 0, 2, true, false, null),
        DISPLAY_ORDER   ("integer",  0, 0, 0, true, false, null),
        LEVEL           ("smallint", 0, 0, 0, true, false, null),
        CONTENT         ("text",     0, 0, 0, true, false, null),
        PARENT_ID       ("varchar", 36, 0, 0, true, false, null),
        CREATED_USER    ("varchar", 36, 0, 0, true, false, null),
        CREATED_DATE    ("datetime", 0, 0, 0, true, false, null),
        UPDATED_USER    ("varchar", 36, 0, 0, true, false, null),
        UPDATED_DATE    ("datetime", 0, 0, 0, true, false, null);

        private String jdbcType;
        private int size;
        private int precision;
        private int primary;
        private boolean notNull;
        private boolean unique;
        private Object defaultValue;
        private Field field;
        private Method setMethod;
        private Method getMethod;

        private OutloggerTypes(String jdbcType, int size, int precision,
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
                field = OutloggerEntity.class.getDeclaredField(beanName);
            } catch (NoSuchFieldException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            }

            String setterName = "set" + BeanUtil.snake2camel(name(), true);
            try {
                setMethod = OutloggerEntity.class.getMethod(setterName, field.getType());
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            }

            String getterName = "get" + BeanUtil.snake2camel(name(), true);
            try {
                getMethod = OutloggerEntity.class.getMethod(getterName);
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

    private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerEntity.class);
    private static final String ENTITY_NAME = "DOCS_OUTLOGGER";

    @Column(name="DOCUMENT_ID", typeName="varchar", size=36, primaryKey=1)
    private String documentId;

    @Column(name="LOG_ID", typeName="varchar", size=36, primaryKey=2)
    private String logId;

    @Column(name="DISPLAY_ORDER", typeName="integer")
    private int displayOrder;

    @Column(name="LEVEL", typeName="short")
    private short level;

    @Column(name="CONTENT", typeName="text")
    private String content;

    @Column(name="PARENT_ID", typeName="varchar", size=36)
    private String parentId;

    @Column(name="CREATED_USER", typeName="varchar", size=36)
    private String createdUser;

    @Column(name="CREATED_DATE", typeName="timestamp")
    private java.util.Date createdDate;

    @Column(name="UPDATED_USER", typeName="varchar", size=36)
    private String updatedUser;

    @Column(name="UPDATED_DATE", typeName="timestamp")
    private java.util.Date updatedDate;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }
    @Override
    public SchemaType[] getTypes() {
        return OutloggerTypes.values();
    }
    @Override
    public SchemaType getType(String name) {
        return OutloggerTypes.valueOf(name);
    }

    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getLogId() {
        return logId;
    }
    public void setLogId(String logId) {
        this.logId = logId;
    }
    public int getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    public short getLevel() {
        return level;
    }
    public void setLevel(short level) {
        this.level = level;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getCreatedUser() {
        return createdUser;
    }
    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }
    public java.util.Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(java.util.Date createdDate) {
        this.createdDate = createdDate;
    }
    public String getUpdatedUser() {
        return updatedUser;
    }
    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }
    public java.util.Date getUpdatedDate() {
        return updatedDate;
    }
    public void setUpdatedDate(java.util.Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
