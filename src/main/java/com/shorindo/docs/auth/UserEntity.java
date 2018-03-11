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
package com.shorindo.docs.auth;

import java.util.Date;

import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("AUTH_USER")
public class UserEntity extends SchemaEntity {
    private static final String ENTITY_NAME = "AUTH_USER";

    @Column("USER_ID")
    private String userId;

    @Column("LOGIN_NAME")
    private String loginName;

    @Column("DISPLAY_NAME")
    private String displayName;

    @Column("PASSWORD")
    private String password;

    @Column("MAIL")
    private String mail;

    @Column("ROLE_NAME")
    private String roleName = "PUBLIC";

    @Column("ACL_ID")
    private String aclId;

    @Column("STATUS")
    private int status;

    @Column("CREATED_DATE")
    private Date createdDate;

    @Column("UPDATED_DATE")
    private Date updatedDate;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAclId() {
        return aclId;
    }

    public void setAclId(String aclId) {
        this.aclId = aclId;
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
