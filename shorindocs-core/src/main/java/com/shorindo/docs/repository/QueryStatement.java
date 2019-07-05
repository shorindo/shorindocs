/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.docs.repository;

import static com.shorindo.docs.repository.DatabaseMessages.DBMS_5130;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.shorindo.docs.repository.RepositoryStatement.ColumnMapper;

/**
 * 
 */
public abstract class QueryStatement extends RepositoryStatement {

    public QueryStatement(Class<?> clazz) throws RepositoryException {
        super(clazz);
    }

    public abstract <T> T querySingle(Connection conn, T entity) throws RepositoryException;
    public abstract <T> List<T> queryList(Connection conn, T entity) throws RepositoryException;
    
    public static class SelectStatement extends QueryStatement {

        public SelectStatement(Class<?> clazz) throws RepositoryException {
            super(clazz);
            List<String> valueList = new ArrayList<String>();
            List<String> keyList = new ArrayList<String>();
            for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                Column column = entry.getValue().getColumn();
                if (column.primaryKey() > 0) {
                    keyList.add(column.name());
                } else {
                    valueList.add(column.name());
                }
            }
            if (keyList.size() == 0) {
                throw new RepositoryException(DBMS_5130, clazz.getSimpleName());
            }
            StringBuffer sb = new StringBuffer("SELECT ");
            String sep = "";
            for (String value : valueList) {
                sb.append(sep + value);
                sep = ", ";
            }
            sb.append(" FROM " + getTableName());
            sb.append(" WHERE ");
            sep = "";
            for (String key : keyList) {
                sb.append(sep + key + "=?");
                sep = " AND ";
            }
            sql = sb.toString();
        }

        @Override
        public <T> T querySingle(Connection conn, T entity) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> List<T> queryList(Connection conn, T entity) throws RepositoryException {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                List<T> resultList = new ArrayList<T>();
                int index = 1;
                for (Entry<Field,ColumnMapper> entry : columnMap.entrySet()) {
                    Column column = entry.getValue().getColumn();
                    if (column.primaryKey() > 0) {
                        setHolders(stmt, index++, entry.getValue(), entity);
                    }
                }
                ResultSet rset = stmt.executeQuery();
                while (rset.next()) {
                    
                }
                rset.close();
                return resultList;
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }
}
