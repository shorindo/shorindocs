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
@Table("DOCUMENT")
public class DocumentEntity extends SchemaEntity {

    private static final ActionLogger LOG = ActionLogger.getLogger(DocumentEntity.class);

    @Column(name="DOCUMENT_ID", typeName="varchar", size=36)
    private String documentId;

    @Column(name="CONTENT_TYPE", typeName="varchar", size=255)
    private String contentType;

    @Column(name="STATUS", typeName="smallint")
    private int status;

    @Column(name="TITLE", typeName="varchar", size=80)
    private String title;

    @Column(name="BODY", typeName="text")
    private String body;

    @Column(name="CREATE_DATE", typeName="timestamp")
    private Timestamp createDate;

    @Column(name="UPDATE_DATE", typeName="timestamp")
    private Timestamp updateDate;

    @Column(name="OWNER_ID", typeName="varchar", size=36)
    private String ownerId;

    @Column(name="ACL_ID", typeName="varchar", size=36)
    private String aclId;

    public DocumentEntity() throws DatabaseException {
        super();
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
    public Timestamp getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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
    public String getAclId() {
        return aclId;
    }
    public void setAclId(String aclId) {
        this.aclId = aclId;
    }
}
