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

import java.text.MessageFormat;
import java.util.Locale;

import com.shorindo.docs.ActionMessages;

/**
 * 
 */
public enum AuthenticateMessages implements ActionMessages {
    ;

    private String message;

    private AuthenticateMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name().replaceAll("_", "-");
    }

    @Override
    public String getMessage(Locale locale, Object... args) {
        return MessageFormat.format(message, args);
    }

}