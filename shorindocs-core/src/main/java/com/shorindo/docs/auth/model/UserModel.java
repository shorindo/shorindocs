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
package com.shorindo.docs.auth.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class UserModel implements Princimal {
    private String userId = "anonymous";
    private String loginName;
    private String displayName;
    private String mail;
    private List<GroupModel> groupList = new ArrayList<GroupModel>();

    public String getId() {
        return userId;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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
