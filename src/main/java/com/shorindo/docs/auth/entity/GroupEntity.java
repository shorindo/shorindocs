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
package com.shorindo.docs.auth.entity;

import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class GroupEntity extends SchemaEntity {
    private static final String ENTITY_NAME = "AUTH_GROUP";

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public SchemaType[] getTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.database.SchemaEntity#getType(java.lang.String)
     */
    @Override
    public SchemaType getType(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}
