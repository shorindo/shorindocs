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
package com.shorindo.docs.document;

import java.util.Date;

import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.model.Permission;
import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.SchemaEntity;
import com.shorindo.docs.repository.Table;

/**
 * 
 */
@Table("docs_document")
public class DocumentEntity extends SchemaEntity implements DocumentModel {

    @Column(name="DOCUMENT_ID", primaryKey=1)
    private String documentId;

    @Column(name="VERSION", primaryKey=2)
    private int version = 0;

    @Column(name = "DOC_TYPE")
    private String docType;

    @Column(name="TITLE")
    private String title;

    @Column(name="CONTENT")
    private String content;

    @Column(name="OWNER_ID")
    private String ownerId;

    @Column(name="CREATE_USER")
    private String createUser;

    @Column(name="CREATE_DATE")
    private Date createDate;

    @Column(name="UPDATE_USER")
    private String updateUser;

    @Column(name="UPDATE_DATE")
    private Date updateDate;

    private String icon;
    private Permission permission = Permission.MANAGE;

    public DocumentEntity() {
    }

    public DocumentEntity(DocumentModel model) {
        setDocumentId(model.getDocumentId());
        setDocType(model.getDocType());
        setTitle(model.getTitle());
        setContent(model.getContent());
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
