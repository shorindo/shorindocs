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

import com.shorindo.docs.IdentityProvider;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.entity.GroupEntity;
import com.shorindo.docs.auth.entity.SessionEntity;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.document.DocumentServiceImpl;
import com.shorindo.docs.model.GroupModel;
import com.shorindo.docs.model.SessionModel;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.Transactional;

/**
 * 
 */
public class AuthenticateServiceImpl extends DocumentServiceImpl implements AuthenticateService {
    private static final ActionLogger LOG = ActionLogger.getLogger(AuthenticateServiceImpl.class);

    public AuthenticateServiceImpl() {
        validate();
    }

    public void validate() {
        InputStream is = getClass().getResourceAsStream("Authenticate.dsdl");
        try {
            DatabaseSchema schema = repositoryService.loadSchema(is);
            repositoryService.validateSchema(schema);
        } catch (RepositoryException e) {
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
    @Transactional
    public SessionModel login(String loginName, String password) throws AuthenticateException {
        try {
            List<UserEntity> userList = repositoryService.queryList(
                    "SELECT * " +
                    "FROM AUTH_USER WHERE LOGIN_NAME=?",
                    UserEntity.class,
                    loginName);
            if (userList.size() == 0) {
                return null;
            }

            UserEntity userEntity = userList.get(0);
            List<GroupEntity> groupList = repositoryService.queryList(
                    "SELECT G.* " +
                    "FROM   AUTH_GROUP G, AUTH_GROUP_MEMBER M " +
                    "WHERE  G.GROUP_ID=M.GROUP_ID " +
                    "AND    M.MEMBER_ID=?",
                    GroupEntity.class,
                    userEntity.getUserId());
            for (GroupEntity group : groupList) {
                userEntity.addGroup(group);
            }
            
            SessionEntity session = new SessionEntity(
                    Long.toHexString(IdentityProvider.newId()),
                    userEntity);
            session.setUserId(userEntity.getUserId());
            session.setCreatedDate(new Date());
            session.setExpiredDate(new Date());
            session.setStatus(0);
            repositoryService.insert(session);
            return session;
        } catch (RepositoryException e) {
            throw new AuthenticateException(e);
        }
    }

    @Transactional
    public void logout(String sessionId) {
    }

    public UserModel authenticate(String sessionId) throws AuthenticateException {
        try {
            List<UserEntity> userList = repositoryService.queryList(
                    "SELECT * " +
                    "FROM   AUTH_USER " +
                    "WHERE  USER_ID=( " +
                    "    SELECT USER_ID " +
                    "    FROM   AUTH_SESSION " +
                    "    WHERE  SESSION_ID=? AND STATUS=0 " +
                    ")",
                    UserEntity.class,
                    sessionId);
            if (userList.size() > 0)
                return userList.get(0);
            else
                return null;
        } catch (RepositoryException e) {
            throw new AuthenticateException(e);
        }
    }

    @Transactional
    public UserModel createUser(UserModel model) throws AuthenticateException {
        try {
            // TODO 重複チェック
            UserEntity entity = new UserEntity(model);
            entity.setUserId(Long.toHexString(IdentityProvider.newId()));
            entity.setPassword(hashPassword(model.getPassword()));
            entity.setCreatedDate(new java.sql.Date(new Date().getTime()));
            entity.setUpdatedDate(new java.sql.Date(new Date().getTime()));
//            entity.setStatus(0);
            repositoryService.insert(entity);
            return entity;
        } catch (RepositoryException e) {
            throw new AuthenticateException(e);
        }
    }

    private static final String SEEDS = "0123456789abcdef";
    private String hashPassword(String password) {
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

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#updateUser(com.shorindo.docs.auth.model.UserModel)
     */
    @Override
    public UserModel updateUser(UserModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#removeUser(com.shorindo.docs.auth.model.UserModel)
     */
    @Override
    public UserModel removeUser(UserModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#searchUser()
     */
    @Override
    public List<UserModel> searchUser() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#createGroup(com.shorindo.docs.auth.model.GroupModel)
     */
    @Override
    public GroupModel createGroup(GroupModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#updateGroup(com.shorindo.docs.auth.model.GroupModel)
     */
    @Override
    public GroupModel updateGroup(GroupModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#removeGroup(com.shorindo.docs.auth.model.GroupModel)
     */
    @Override
    public GroupModel removeGroup(GroupModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.auth.AuthenticateService#searchGroup()
     */
    @Override
    public List<GroupModel> searchGroup() {
        // TODO Auto-generated method stub
        return null;
    }

}
