/*
 * Copyright 2016 Shorindo, Inc.
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
package com.shorindo.docs;

import java.util.Locale;

/**
 * 
 */
public class DocumentException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final Locale LANG = ApplicationContext.getLang();

    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentException(String message) {
        super(message);
    }

    public DocumentException(ActionMessages messages, Throwable cause) {
        super(messages.getCode() + ":" + messages.getMessage(LANG), cause);
    }
}
