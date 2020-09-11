/*
 * Copyright 2018 Shorindo, Inc.
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
package com.shorindo.docs.auth.entity;

import java.util.Date;

import com.shorindo.docs.model.SessionModel;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.SchemaEntity;
import com.shorindo.docs.repository.Table;

/**
 * 
 */
@Table("AUTH_SESSION")
public class SessionEntity extends SchemaEntity implements SessionModel {
    @Column(name="SESSION_ID", primaryKey=1)
    private String sessionId;

    @Column(name="USER_ID")
    private String userId;

    @Column(name="STATUS")
    private int status;

    @Column(name="CREATED_DATE")
    private Date createdDate;

    @Column(name="EXPIRED_DATE")
    private Date expiredDate;

    private UserEntity user;

    public SessionEntity(String sessionId, UserEntity user) throws RepositoryException {
        super();
        this.sessionId = sessionId;
        this.user = user;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public UserModel getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

}
