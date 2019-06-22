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

import static com.shorindo.docs.repository.DatabaseMessages.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.IdentityProvider;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.entity.GroupEntity;
import com.shorindo.docs.auth.entity.SessionEntity;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.auth.model.GroupModel;
import com.shorindo.docs.auth.model.UserModel;
import com.shorindo.docs.document.DocumentService;
import com.shorindo.docs.document.DocumentServiceImpl;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.Transactionable;

/**
 * 
 */
public class AuthenticateService extends DocumentServiceImpl {
    private static final ActionLogger LOG = ActionLogger.getLogger(AuthenticateService.class);

    protected AuthenticateService() {
        validate();
    }

    public void validate() {
        InputStream is = getClass().getResourceAsStream("AuthenticateService.dsdl");
        try {
            DatabaseSchema schema = repositoryService.loadSchema(is);
            repositoryService.validateSchema(schema);
        } catch (DatabaseException e) {
            LOG.error(DBMS_5123);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.warn(DBMS_5103, e);
            }
        }
    }

    /**
     * 
     * @param userId
     * @param password
     * @return
     * @throws AuthenticateException
     */
    public SessionEntity login(String loginName, String password) throws AuthenticateException {
        try {
            return repositoryService.transaction(
                    new Transactionable<SessionEntity>() {
                        @Override
                        public SessionEntity run(Object... params) throws DatabaseException {
                            List<UserEntity> userList = repositoryService.query(
                                    "SELECT * " +
                                    "FROM AUTH_USER WHERE LOGIN_NAME=?",
                                    UserEntity.class,
                                    loginName);
                            if (userList.size() == 0) {
                                return null;
                            }

                            UserEntity userEntity = userList.get(0);
                            List<GroupEntity> groupList = repositoryService.query(
                                    "SELECT G.* " +
                                    "FROM   AUTH_GROUP G, AUTH_GROUP_MEMBER M " +
                                    "WHERE  G.GROUP_ID=M.GROUP_ID " +
                                    "AND    M.MEMBER_ID=?",
                                    GroupEntity.class,
                                    userEntity.getUserId());
                            for (GroupEntity group : groupList) {
                                userEntity.addGroup(group);
                            }
                            return null;
                        }
                    });
        } catch (DatabaseException e) {
            throw new AuthenticateException(e);
        }
    }

    public void logout(SessionEntity session) {
    }

    public UserEntity createUser(UserEntity entity) throws AuthenticateException {
        try {
            entity.setUserId(String.valueOf(IdentityProvider.newId()));
            entity.setPassword(hashPassword(entity.getPassword()));
            entity.setDisplayName("displayName");
            entity.setCreatedDate(new java.sql.Date(new Date().getTime()));
            entity.setUpdatedDate(new java.sql.Date(new Date().getTime()));
            entity.setStatus(0);
            repositoryService.insert(entity);
            return entity;
        } catch (DatabaseException e) {
            throw new AuthenticateException(e);
        }
    }

    public UserEntity updateUser() {
        return null;
    }

    public UserEntity removeUser() {
        return null;
    }

    public GroupEntity createGroup() {
        return null;
    }

    public GroupEntity updateGroup() {
        return null;
    }

    public GroupEntity removeGroup() {
        return null;
    }

    private static final String SEEDS = "0123456789abcd";
    protected String hashPassword(String password) {
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                sb.append(SEEDS.charAt((int)(Math.random() * SEEDS.length())));
            }
            String salt = sb.toString();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < 1000; i++) {
                md5.update(salt.getBytes());
                md5.update(password.getBytes());
            }
            byte[] digest = md5.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean confitmPassword(String password, String hash) {
        try {
            String salt = hash.substring(0, 4);
            StringBuffer sb = new StringBuffer(salt);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < 1000; i++) {
                md5.update(salt.getBytes());
                md5.update(password.getBytes());
            }
            byte[] digest = md5.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
