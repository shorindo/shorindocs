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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.database.SchemaEntity;
import com.shorindo.docs.database.SchemaType;

/**
 * 
 */
public class UserEntity extends SchemaEntity {
    private static final ActionLogger LOG = ActionLogger.getLogger(UserEntity.class);
    private static final String ENTITY_NAME = "AUTH_USER";

    private String userId;
    private String loginId;
    private String displayName;
    private String password;
    private String mail;
    private int status;
    private Date createdDate;
    private Date updatedDate;

    @Override
    public String getEntityName() {
        return ENTITY_NAME;
    }

    @Override
    public SchemaType[] getTypes() {
        return UserTypes.values();
    }

    @Override
    public SchemaType getType(String name) {
        return UserTypes.valueOf(name);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
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

    private static enum UserTypes implements SchemaType {
        USER_ID     ("varchar",  36, 0, 1, true,  true,  null),
        LOGIN_ID    ("varchar",  80, 0, 0, true,  true,  null),
        PASSWORD    ("varchar",  80, 0, 0, true,  false, null),
        DISPLAY_NAME("varchar",  80, 0, 0, true,  true,  null),
        MAIL        ("varchar",  80, 0, 0, false, false, null),
        STATUS      ("smallint",  0, 0, 0, true,  false, 1),
        CREATED_DATE("timestamp", 0, 0, 0, true,  false, null),
        UPDATED_DATE("timestamp", 0, 0, 0, true,  false, null)
        ;

        private String jdbcType;
        private int size;
        private int precision;
        private int primary;
        private boolean notNull;
        private boolean unique;
        private Object defaultValue;
        private Field field;
        private Method setMethod;
        private Method getMethod;

        private UserTypes(String jdbcType, int size, int precision,
                int primary, boolean notNull, boolean unique, Object defaultValue) {
            this.jdbcType = jdbcType;
            this.size = size;
            this.precision = precision;
            this.precision = primary;
            this.notNull = notNull;
            this.unique = unique;
            this.defaultValue = defaultValue;

            String beanName = BeanUtil.snake2camel(name(), false);
            try {
                field = UserEntity.class.getDeclaredField(beanName);
                field.setAccessible(true); // TODO そのうち除去
            } catch (NoSuchFieldException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5119, e, name(), beanName);
                return;
            }

            String setterName = "set" + BeanUtil.snake2camel(name(), true);
            try {
                setMethod = UserEntity.class.getMethod(setterName, field.getType());
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), setterName);
            }

            String getterName = "get" + BeanUtil.snake2camel(name(), true);
            try {
                getMethod = UserEntity.class.getMethod(getterName);
            } catch (NoSuchMethodException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            } catch (SecurityException e) {
                LOG.error(DocsMessages.E_5120, e, name(), getterName);
            }
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

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public Method getSetMethod() {
            return setMethod;
        }

        @Override
        public Method getGetMethod() {
            return getMethod;
        }
    }

}
