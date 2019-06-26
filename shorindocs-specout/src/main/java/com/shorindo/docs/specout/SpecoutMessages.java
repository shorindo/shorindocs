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
import java.util.Map;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum SpecoutMessages implements ActionMessages {
    @Message(ja = "ドキュメント解析開始")
    SPEC_0001,
    @Message(ja = "ドキュメント解析終了 : {0} ms")
    SPEC_0002,
    @Message(ja = "ドキュメントの解析に失敗しました。")
    SPEC_9001;

    private Map<String,MessageFormat> bundle;

    private SpecoutMessages() {
        bundle = ActionMessages.Util.bundle(this);
    }

    @Override
    public Map<String, MessageFormat> getBundle() {
        return bundle;
    }

    @Override
    public String getCode() {
        return ActionMessages.Util.getCode(this);
    }

    @Override
    public String getMessage(Object... params) {
        return ActionMessages.Util.getMessage(this, params);
    }

    @Override
    public String getMessage(Locale locale, Object... params) {
        return ActionMessages.Util.getMessage(this, params);
    }
}
