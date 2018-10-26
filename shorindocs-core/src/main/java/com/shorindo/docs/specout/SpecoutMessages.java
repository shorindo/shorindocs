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
package com.shorindo.docs.specout;

import java.text.MessageFormat;
import java.util.Locale;

import com.shorindo.docs.ActionMessages;

/**
 * 
 */
public enum SpecoutMessages implements ActionMessages {
    SPEC_0001("ドキュメント解析開始"),
    SPEC_0002("ドキュメント解析終了 : {0} ms"),
    SPEC_9001("ドキュメントの解析に失敗しました。");

    private String message;

    private SpecoutMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage(Locale locale, Object... args) {
        return MessageFormat.format(message, args);
    }

}
