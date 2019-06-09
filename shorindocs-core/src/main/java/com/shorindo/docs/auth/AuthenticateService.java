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
import java.util.List;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocumentService;
import com.shorindo.docs.auth.entity.GroupEntity;
import com.shorindo.docs.auth.entity.SessionEntity;
import com.shorindo.docs.auth.entity.UserEntity;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.Transactionable;

/**
 * 
 */
public class AuthenticateService extends DocumentService {
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
    public SessionEntity login(String userId, String password) throws AuthenticateException {
        try {
            return repositoryService.transaction(
                    new Transactionable<SessionEntity>() {
                        @Override
                        public SessionEntity run(Object... params) throws DatabaseException {
                            UserEntity user = new UserEntity();
                            user.setUserId(userId);
                            user = repositoryService.get(user);
                            if (user == null) {
                                throw new DatabaseException();
                            }

                            List<GroupEntity> groupList = repositoryService.query(
                                    "SELECT * FROM AUTH_GROUP WHERE USER_ID=?",
                                    GroupEntity.class,
                                    user.getUserId());
                            return null; // FIXME
                        }
                    });
        } catch (DatabaseException e) {
            throw new AuthenticateException(e);
        }
    }

    public void logout(SessionEntity session) {
    }

    public UserEntity createUser() {
        return null;
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

}