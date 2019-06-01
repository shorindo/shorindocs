/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.docs.entity;

import java.sql.Timestamp;

import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.SchemaEntity;
import com.shorindo.docs.repository.Table;

/**
 * 
 */
@Table("docs_document")
public class DocumentEntity extends SchemaEntity {

    @Column(name="DOCUMENT_ID", typeName="VARCHAR")
    private String documentId;

    @Column(name="CONTROLLER", typeName="VARCHAR")
    private String controller;

    @Column(name="TITLE", typeName="VARCHAR")
    private String title;

    @Column(name="CONTENT", typeName="TEXT")
    private String content;

    @Column(name="CONTENT_CACHE", typeName="TEXT")
    private String contentCache;

    @Column(name="OWNER_ID", typeName="VARCHAR")
    private String ownerId;

    @Column(name="CREATE_USER", typeName="VARCHAR")
    private String createUser;

    @Column(name="CREATE_DATE", typeName="TIMESTAMP")
    private Timestamp createDate;

    @Column(name="UPDATE_USER", typeName="VARCHAR")
    private String updateUser;

    @Column(name="UPDATE_DATE", typeName="TIMESTAMP")
    private Timestamp updateDate;

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
}
