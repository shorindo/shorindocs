/*
 * Copyright 2016-2018 Shorindo, Inc.
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

import java.sql.Timestamp;

import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("DOCS_DOCUMENT")
public class DocumentEntity extends SchemaEntity {

    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentEntity.class);

    @Column(name="DOCUMENT_ID", typeName="varchar", size=36)
    private String documentId;

    @Column(name="CONTROLLER", typeName="varchar", size=256)
    private String controller;

    @Column(name="TITLE", typeName="varchar", size=256)
    private String title;

    @Column(name="CONTENT", typeName="text")
    private String content;

    @Column(name="CONTENT_CACHE", typeName="text", notNull=false)
    private String contentCache;

    @Column(name="OWNER_ID", typeName="varchar", size=36)
    private String ownerId;

    @Column(name="CREATE_USER", typeName="varchar", size=36)
    private String createUser;

    @Column(name="CREATE_DATE", typeName="timestamp")
    private Timestamp createDate;

    @Column(name="UPDATE_USER", typeName="varchar", size=36)
    private String updateUser;

    @Column(name="UPDATE_DATE", typeName="timestamp")
    private Timestamp updateDate;

    public DocumentEntity() throws DatabaseException {
        super();
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getController() {
        return controller;
    }
    public void setController(String controller) {
        this.controller = controller;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public Timestamp getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public Timestamp getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
