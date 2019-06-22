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

/**
 * 
 */
public interface RepositoryService {
    public DatabaseSchema loadSchema(InputStream is);
    public List<String> validateSchema(DatabaseSchema schema) throws DatabaseException;
    public int createTableFromSchema(DatabaseSchema.Table table) throws DatabaseException;
    public String generateDDL(DatabaseSchema.Table table) throws DatabaseException;
    public <T> T transaction(Transactionable<T> t) throws DatabaseException;
    public int execute(String sql, Object...params) throws DatabaseException;
    public <E> List<E> query(String sql, Class<E> clazz, Object...params) throws DatabaseException;
    public <E extends SchemaEntity> E get(E entity) throws NotFoundException,DatabaseException;
    public int put(SchemaEntity entity) throws DatabaseException;
    public int remove(SchemaEntity entity) throws DatabaseException;
    public int insert(SchemaEntity entity) throws DatabaseException;
    public int update(SchemaEntity entity) throws DatabaseException;
}
