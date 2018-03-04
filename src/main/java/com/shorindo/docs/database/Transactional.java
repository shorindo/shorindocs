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

import java.sql.Connection;
import java.sql.SQLException;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.DocsMessages;

/**
 * 
 */
public abstract class Transactional<T> extends DatabaseExecutor<T> {
    private static final ActionLogger LOG = ActionLogger.getLogger(Transactional.class);

    @Override
    public void beginTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    @Override
    public void commitTransaction(Connection conn) throws SQLException {
        LOG.debug(DocsMessages.I_1102);
        conn.commit();
    }

    @Override
    public void rollbackTransaction(Connection conn) throws SQLException {
        LOG.info(DocsMessages.I_1103);
        conn.rollback();
    }
}
