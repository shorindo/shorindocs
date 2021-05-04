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

import java.util.List;

import com.shorindo.docs.model.GroupModel;
import com.shorindo.docs.model.SessionModel;
import com.shorindo.docs.model.UserModel;

/**
 * 
 */
public interface AuthenticateService {
    public void validate();

    // user information
    public void setUser(UserModel user);
    public UserModel getUser();

    // session management
    public SessionModel login(String loginName, String password) throws AuthenticateException;
    public void logout(String sessionId);
    public UserModel authenticate(String sessionId, UserModel user) throws AuthenticateException;

    // user management
    public UserModel createUser(UserModel model) throws AuthenticateException;
    public UserModel updateUser(UserModel model);
    public UserModel removeUser(UserModel model);
    public List<UserModel> searchUser();

    // group management
    public GroupModel createGroup(GroupModel model);
    public GroupModel updateGroup(GroupModel model);
    public GroupModel removeGroup(GroupModel model);
    public List<GroupModel> searchGroup();
}
