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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 */
public interface RepositoryService {
    public DatabaseSchema loadSchema(InputStream is);
    public List<String> validateSchema(DatabaseSchema schema) throws RepositoryException;
    public int createTableFromSchema(DatabaseSchema.Table table) throws RepositoryException;
    public String generateDDL(DatabaseSchema.Table table) throws RepositoryException;

    public int execute(String sql, Object...params) throws RepositoryException;
    public <E> Optional<E> querySingle(String sql, Class<E> clazz, Object...params) throws RepositoryException;
    public <E> List<E> queryList(String sql, Class<E> clazz, Object...params) throws RepositoryException;
    public List<Map<String,Object>> queryMap(String sql, Object...params) throws RepositoryException;
    public int insert(Object entity) throws RepositoryException;
    public int update(Object entity) throws RepositoryException;
    public int delete(Object entity) throws RepositoryException;

    public <E> E get(E entity) throws RepositoryException;
    public int put(Object entity) throws RepositoryException;
}
