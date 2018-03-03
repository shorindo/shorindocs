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

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class UserModel extends SchemaEntity {
    private static final String TABLE_NAME = "USER";

    public enum UserType implements SchemaType {
        USER_ID     ("VARCHAR",  36, 1, true,  true,  String.class),
        LOGIN_NAME  ("VARCHAR",  80, 0, true,  true,  String.class),
        PASSWORD    ("VARCHAR",  80, 0, true,  false, String.class),
        DISPLAY_NAME("VARCHAR",  80, 0, true,  true,  String.class),
        MAIL        ("VARCHAR",  80, 0, false, false, String.class),
        ROLE_NAME   ("VARCHAR",  8,  0, true,  false, String.class),
        ACL_ID      ("VARCHAR",  36, 0, true,  false, String.class),
        STATUS      ("SMALLINT", 0,  0, true,  false, int.class),
        CREATED_DATE("DATETIME", 0,  0, true,  false, Date.class),
        UPDATED_DATE("DATETIME", 0,  0, true,  false, Date.class)
        ;
        private UserType(String typeName, int size, int primaryKey,
                boolean notNull, boolean unique, Class<?> javaType) {
        }
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public SchemaType[] getSchemaTypes() {
        return UserType.values();
    }

    @Column(
        name        = "USER_ID",
        size        = 36,
        primaryKey  = 1,
        unique      = true
    )
    private String userId;

    @Column(
        name        = "LOGIN_NAME",
        size        = 80,
        unique      = true
    )
    private String loginName;

    @Column(
        name        = "DISPLAY_NAME",
        size        = 80
    )
    private String displayName;

    @Column(
        name        = "PASSWORD",
        size        = 80
    )
    private String password;

    @Column(
        name        = "MAIL",
        size        = 80,
        notNull     = false
    )
    private String mail;

    @Column(
        name        = "ROLE_NAME",
        size        = 8
    )
    private String roleName = "PUBLIC";

    @Column(
        name        = "ACL_ID",
        size        = 36
    )
    private String aclId;

    @Column(
        name        = "STATUS",
        typeName    = "SMALLINT"
    )
    private int status;

    @Column(
        name        = "CREATED_DATE",
        typeName    = "DATETIME"        
    )
    private Date createdDate;

    @Column(
        name        = "UPDATED_DATE",
        typeName    = "DATETIME"        
    )
    private Date updatedDate;

    public static void main(String[] args) {
        Map<Integer,String> pkMap = new TreeMap<Integer,String>();
        for (Field field : UserModel.class.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%-16s", column.name()));
                String type = column.typeName();
                if (column.size() > 0) {
                    type += "(" + column.size() + ")";
                }
                sb.append(String.format("%-12s", type));
                if (column.unique()) {
                    sb.append(" UNIQUE");
                }
                if (column.notNull()) {
                    sb.append(" NOT NULL");
                }
                if (column.primaryKey() > 0) {
                    pkMap.put(column.primaryKey(), column.name());
                }
                System.out.println(sb.toString());
            }
        }
        if (pkMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("CONSTRAINT PRIMARY KEY(");
            String sep = "";
            for (String key : pkMap.values()) {
                sb.append(sep);
                sb.append(key);
                sep = ", ";
            }
            sb.append(")");
            System.out.println(sb.toString());
        }
    }
}
