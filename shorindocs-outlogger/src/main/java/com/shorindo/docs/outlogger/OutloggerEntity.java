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

import net.arnx.jsonic.JSON;

import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.SchemaEntity;
import com.shorindo.docs.repository.Table;

/**
 * 
 */
@Table("DOCS_OUTLOGGER")
public class OutloggerEntity extends SchemaEntity implements OutloggerModel {
    //private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerEntity.class);

    public OutloggerEntity() {
        super();
    }

    @Column(name="DOCUMENT_ID", primaryKey=1)
    private String documentId;

    @Column(name="LOG_ID", primaryKey=2)
    private Integer logId;

    @Column(name="VERSION")
    private int version;

    @Column(name="PARENT_ID")
    private Integer parentId;

    @Column(name="DISPLAY_ORDER")
    private int displayOrder;

    @Column(name="LEVEL")
    private short level;

    @Column(name="CONTENT")
    private String content;

    @Column(name="CREATE_USER")
    private String createUser;

    @Column(name="CREATE_DATE")
    private java.util.Date createDate;

    @Column(name="UPDATE_USER")
    private String updateUser;

    @Column(name="UPDATE_DATE")
    private java.util.Date updateDate;

    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public Integer getLogId() {
        return logId;
    }
    public void setLogId(Integer logId) {
        this.logId = logId;
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
    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
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
    public String toString() {
        return JSON.encode(this);
    }

//    public OutloggerEntityBuilder builder() {
//        return new OutloggerEntityBuilder();
//    }
//    public static class OutloggerEntityBuilder {
//        private OutloggerEntity entity;
//        public OutloggerEntityBuilder() {
//            entity = new OutloggerEntity();
//        }
//    }
}
