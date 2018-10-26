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

import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("DOCS_OUTLOGGER")
public class OutloggerEntity extends SchemaEntity {
    //private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerEntity.class);

    public OutloggerEntity() throws DatabaseException {
        super();
    }

    @Column(name="DOCUMENT_ID", typeName="varchar", size=36, primaryKey=1)
    private String documentId;

    @Column(name="LOGGER_ID", typeName="varchar", size=36, primaryKey=2)
    private String loggerId;

    @Column(name="VERSION", typeName="int")
    private int version;

    @Column(name="PARENT_ID", typeName="varchar", size=36)
    private String parentId;

    @Column(name="DISPLAY_ORDER", typeName="integer")
    private int displayOrder;

    @Column(name="LEVEL", typeName="smallint")
    private short level;

    @Column(name="CONTENT", typeName="text")
    private String content;

    @Column(name="CONTENT_CACHE", typeName="text", notNull=false)
    private String contentCache;

    @Column(name="CREATE_USER", typeName="varchar", size=36)
    private String createUser;

    @Column(name="CREATE_DATE", typeName="timestamp")
    private java.util.Date createDate;

    @Column(name="UPDATE_USER", typeName="varchar", size=36)
    private String updateUser;

    @Column(name="UPDATE_DATE", typeName="timestamp")
    private java.util.Date updateDate;

    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getLoggerId() {
        return loggerId;
    }
    public void setLoggerId(String loggerId) {
        this.loggerId = loggerId;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
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
    public String getContentCache() {
        return contentCache;
    }
    public void setContentCache(String contentCache) {
        this.contentCache = contentCache;
    }
    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public java.util.Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }
    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public java.util.Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(java.util.Date updateDate) {
        this.updateDate = updateDate;
    }
}
