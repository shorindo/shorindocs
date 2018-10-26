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
package com.shorindo.xuml;

import java.util.Locale;

import com.shorindo.docs.ActionMessages;

/**
 * 
 */
public class XumlException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final Locale LANG = Locale.JAPANESE;

    /**
     * 
     */
    public XumlException() {
        super();
    }

    /**
     * @param message
     */
    public XumlException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public XumlException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public XumlException(String message, Throwable cause) {
        super(message, cause);
    }

    public XumlException(ActionMessages messages) {
        super(messages.getCode() + ":" + messages.getMessage(LANG));
    }
}
