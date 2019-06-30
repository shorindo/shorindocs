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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.model.GroupModel;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.Column;
import com.shorindo.docs.repository.SchemaEntity;
import com.shorindo.docs.repository.Table;

/**
 * 
 */
@Table("AUTH_USER")
public class UserEntity extends SchemaEntity implements UserModel {
    @SuppressWarnings("unused")
    private static final ActionLogger LOG = ActionLogger.getLogger(UserEntity.class);

    @Column(name="USER_ID", typeName="VARCHAR")
    private String userId = "anonymous";

    @Column(name="LOGIN_NAME", typeName="VARCHAR")
    private String loginName;

    @Column(name="DISPLAY_NAME", typeName="VARCHAR")
    private String displayName;

    @Column(name="PASSWORD", typeName="VARCHAR")
    private String password;

    @Column(name="MAIL", typeName="VARCHAR")
    private String mail;

    @Column(name="STATUS", typeName="INT")
    private int status;

    @Column(name="CREATED_DATE", typeName="TIMESTAMP")
    private Date createdDate;

    @Column(name="UPDATED_DATE", typeName="TIMESTAMP")
    private Date updatedDate;

    private List<GroupModel> groupList = new ArrayList<GroupModel>();

    public UserEntity() {
    }

    public UserEntity(UserModel model) {
        this.setUserId(model.getUserId());
        this.setDisplayName(model.getDisplayName());
        this.setLoginName(model.getLoginName());
        this.setMail(model.getMail());
        this.setPassword(model.getPassword());
    }

    public String getId() {
        return getUserId();
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

    public List<GroupModel> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupModel> groupList) {
        this.groupList = groupList;
    }

    public void addGroup(GroupModel group) {
        this.groupList.add(group);
    }
}
