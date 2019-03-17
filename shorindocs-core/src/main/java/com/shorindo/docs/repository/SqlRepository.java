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
package com.shorindo.docs.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 */
public class SqlRepository implements Repository {

    /**
     * 
     */
    protected SqlRepository() {
    }

    @Override
    public <T> T put(T data) throws RepositoryException {
        try (Connection conn = getConnection()) {
            data.getClass().getAnnotation(Table.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T get(T key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T remove(T key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> find(Class<T> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    private Connection getConnection() {
        return null;
    }
}
