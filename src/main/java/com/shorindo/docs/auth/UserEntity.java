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

import java.util.Date;

import com.shorindo.docs.database.Column;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("AUTH_USER")
public class UserEntity extends SchemaEntity {

    @Column("USER_ID")
    private String userId;

    @Column("LOGIN_NAME")
    private String loginName;

    @Column("DISPLAY_NAME")
    private String displayName;

    @Column("PASSWORD")
    private String password;

    @Column("MAIL")
    private String mail;

    @Column("ROLE_NAME")
    private String roleName = "PUBLIC";

    @Column("ACL_ID")
    private String aclId;

    @Column("STATUS")
    private int status;

    @Column("CREATED_DATE")
    private Date createdDate;

    @Column("UPDATED_DATE")
    private Date updatedDate;

}
