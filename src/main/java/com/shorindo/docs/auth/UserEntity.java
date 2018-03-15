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
import com.shorindo.docs.database.SchemaType;
import com.shorindo.docs.database.Table;

/**
 * 
 */
@Table("AUTH_USER")
public class UserEntity extends SchemaEntity {
    private static final String ENTITY_NAME = "AUTH_USER";

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

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    public SchemaType[] getTypes() {
        return UserType.values();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAclId() {
        return aclId;
    }

    public void setAclId(String aclId) {
        this.aclId = aclId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    private static enum UserType implements SchemaType {
        USER_ID     ("varchar",  36, 0, 1, true,  true,  null),
        LOGIN_ID    ("varchar",  80, 0, 0, true,  true,  null),
        PASSWORD    ("varchar",  80, 0, 0, true,  false, null),
        DISPLAY_NAME("varchar",  80, 0, 0, true,  true,  null),
        MAIL        ("varchar",  80, 0, 0, false, false, null),
        STATUS      ("smallint",  0, 0, 0, true,  false, 1),
        CREATED_DATE("timestamp", 0, 0, 0, true,  false, null),
        UPDATED_DATE("timestamp", 0, 0, 0, true,  false, null)
        ;

        private String jdbcType = "varchar";
        private int size = 0;
        private int precision = 0;
        private int primary = 0;
        private boolean notNull = false;
        private boolean unique = false;
        private Object defaultValue = null;

        private UserType(String jdbcType, int size, int precision,
                int primary, boolean notNull, boolean unique, Object defaultValue) {
        }

        @Override
        public String getColumnName() {
            return name();
        }

        @Override
        public String getType() {
            return jdbcType;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public int getPrecision() {
            return precision;
        }

        @Override
        public int getPrimary() {
            return primary;
        }

        @Override
        public boolean isNotNull() {
            return notNull;
        }

        @Override
        public boolean isUnique() {
            return unique;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
        
    }
}
