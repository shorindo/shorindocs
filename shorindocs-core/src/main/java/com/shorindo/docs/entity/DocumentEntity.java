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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table(name = "docs_document")
public class DocumentEntity {

    @Id
    @Column(name="DOCUMENT_ID")
    private String documentId;

    @Column(name="CONTROLLER")
    private String controller;

    @Column(name="TITLE")
    private String title;

    @Column(name="CONTENT")
    private String content;

    @Column(name="CONTENT_CACHE")
    private String contentCache;

    @Column(name="OWNER_ID")
    private String ownerId;

    @Column(name="CREATE_USER")
    private String createUser;

    @Column(name="CREATE_DATE")
    private Timestamp createDate;

    @Column(name="UPDATE_USER")
    private String updateUser;

    @Column(name="UPDATE_DATE")
    private Timestamp updateDate;
}
