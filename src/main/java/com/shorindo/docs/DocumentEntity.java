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
package com.shorindo.docs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class DocumentEntity extends SchemaEntity {
    public enum DocumentTypes implements SchemaType {
        DOCUMENT_ID ("varchar",  64, 0, 1, true, true, null),
        CONTENT_TYPE("varchar", 255, 0, 0, true, false, null),
        STATUS      ("int",       0, 0, 0, true, false, null),
        TITLE       ("text",      0, 0, 0, true, false, null),
        BODY        ("text",      0, 0, 0, true, false, null),
        CREATE_DATE ("timestamp", 0, 0, 0, true, false, null),
        UPDATE_DATE ("timestamp", 0, 0, 0, true, false, null),
        OWNER_ID    ("varchar",  64, 0, 0, true, false, null),
        ACL_ID      ("varchar",  64, 0, 0, true, false, null)
        ;

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

        private DocumentTypes(String jdbcType, int size, int precision,
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
                field = DocumentEntity.class.getDeclaredField(beanName);
                field.setAccessible(true); // TODO そのうち除去
            } catch (NoSuchFieldException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            }

            String setterName = "set" + BeanUtil.snake2camel(name(), true);
            try {
                setMethod = DocumentEntity.class.getMethod(setterName, field.getType());
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            }

            String getterName = "get" + BeanUtil.snake2camel(name(), true);
            try {
                getMethod = DocumentEntity.class.getMethod(getterName);
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            }
        }

        @Override
        public String getColumnName() {
            return name();
        }

        @Override
        public String getType() {
            return jdbcType;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public int getPrecision() {
            return precision;
        }

        @Override
        public int getPrimary() {
            return primary;
        }

        @Override
        public boolean isNotNull() {
            return notNull;
        }

        @Override
        public boolean isUnique() {
            return unique;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public Method getSetMethod() {
            return setMethod;
        }

        @Override
        public Method getGetMethod() {
            return getMethod;
        }
        
    }

    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentEntity.class);
    private static final String ENTITY_NAME = "DOCUMENT";

    private String documentId;
    private String contentType;
    private int status;
    private String title;
    private String body;
    private Date createDate;
    private Date updateDate;
    private String ownerId;

    private String aclId;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }
    @Override
    public SchemaType[] getTypes() {
        return DocumentTypes.values();
    }
    @Override
    public SchemaType getType(String name) {
        return DocumentTypes.valueOf(name);
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getAclId() {
        return aclId;
    }
    public void setAclId(String aclId) {
        this.aclId = aclId;
    }
}
