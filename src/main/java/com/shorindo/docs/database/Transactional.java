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
package com.shorindo.docs.database;

import static com.shorindo.docs.database.DatabaseMessages.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.shorindo.docs.ActionLogger;

/**
 * 
 */
public abstract class Transactional<T> extends DatabaseExecutor<T> {
    private static final ActionLogger LOG = ActionLogger.getLogger(Transactional.class);

    @Override
    public void beginTransaction(Connection conn) throws DatabaseException {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void commitTransaction(Connection conn) throws DatabaseException {
        LOG.debug(DB_1102);
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void rollbackTransaction(Connection conn) throws DatabaseException {
        LOG.info(DB_1103);
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
