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
package com.shorindo.docs.markdown;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum MarkdownMessages implements ActionMessages {
    @Message(lang="ja", content="マークダウンの解析開始")
    MKDN_1000,
    @Message(lang="ja", content="マークダウンの解析終了:{0}ms")
    MKDN_1001,
    @Message(lang="ja", content="ドキュメントの解析に失敗しました。")
    MKDN_9000
    ;
}
