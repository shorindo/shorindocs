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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 */
public abstract class DatabaseExecutor<T> {
    //private Connection conn;

    public abstract T run(Connection conn) throws SQLException;
    public abstract void beginTransaction(Connection conn) throws SQLException;
    public abstract void commitTransaction(Connection conn) throws SQLException;
    public abstract void rollbackTransaction(Connection conn) throws SQLException;

    protected final int execute(Connection conn, String sql, Object...params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeUpdate(sql);
    }

    protected final <E> List<E> query(Connection conn, String sql, Class<E> clazz, Object...params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        return null;
    }

    protected final int insert(SchemaEntity entity) throws SQLException {
        return 0;
    }
    
    protected final int update(SchemaEntity entity) throws SQLException {
        return 0;
    }

    protected final int remove(SchemaEntity entity) throws SQLException {
        return 0;
    }
}
