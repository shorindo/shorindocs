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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.database.DatabaseExecutor;
import com.shorindo.docs.database.DatabaseSchema;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.database.DatabaseSchema.Entity;
import com.shorindo.docs.database.Transactional;
import com.shorindo.docs.database.Transactionless;

/**
 * 
 */
public class AuthenticateService {
    private static final ActionLogger LOG = ActionLogger.getLogger(AuthenticateService.class);
    private static final DatabaseService databaseService = DatabaseService.newInstance();
    private static final AuthenticateService authenticateService = new AuthenticateService();

    public static AuthenticateService getInstance() {
        return authenticateService;
    }

    private AuthenticateService() {
        validate();
    }

    public void validate() {
        InputStream is = getClass().getResourceAsStream("AuthenticateService.dsdl");
        try {
            DatabaseSchema schema = databaseService.loadSchema(is);
            databaseService.validateSchema(schema);
//            for (Entity entity : schema.getEntityList()) {
//                if (entity instanceof DatabaseSchema.Table) {
//                    doDDL((DatabaseSchema.Table)entity);
//                }
//            }
        } catch (SQLException e) {
            LOG.error(DocsMessages.E_5123);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.warn(DocsMessages.E_5103, e);
            }
        }
    }

    private void doDDL(DatabaseSchema.Table entity) {
        String tableName = entity.getName();
        try {
            final String ddl = databaseService.generateDDL((DatabaseSchema.Table)entity);
            LOG.debug("doDDL => " + ddl);
            databaseService.provide(new Transactionless<Integer>() {
                @Override
                public Integer run(Connection conn, Object...params) throws SQLException {
                    return exec(ddl);
                }
            
            }, ddl);
        } catch (SQLException e) {
            LOG.error(DocsMessages.E_5121, e, tableName);
        }
    }

    /*
     * 
     */
    private static final DatabaseExecutor<UserSessionEntity> LOGIN_EXEC = new Transactional<UserSessionEntity>() {
        @Override
        public UserSessionEntity run(Connection conn, Object... params)
                throws SQLException {
            String userId = (String)params[0];
            String password = (String)params[1];

            UserEntity user = new UserEntity();
            user.setUserId(userId);
            user = get(user);
            if (user == null) {
                throw new SQLException();
            }

            List<GroupEntity> groupList = query("SELECT * FROM AUTH_GROUP WHERE USER_ID=?",
                    GroupEntity.class,
                    user.getUserId());
            return null;
        }
    };

    /**
     * 
     * @param userId
     * @param password
     * @return
     * @throws AuthenticateException
     */
    public UserSessionEntity login(String userId, String password) throws AuthenticateException {
        try {
            return databaseService.provide(LOGIN_EXEC, userId, password);
        } catch (SQLException e) {
            throw new AuthenticateException(e);
        }
    }

    public void logout(UserSessionEntity session) {
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

    private String hashHash(String seed, String text) {
        return null;
    }
}
