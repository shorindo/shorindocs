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

import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class AclModel extends SchemaEntity {
    private static final String TABLE_NAME = "ACL";

    public enum GroupType implements SchemaType {
        ACL_ID(),
        ACL_NAME();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public SchemaType[] getSchemaTypes() {
        return GroupType.values();
    }

}
