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
package com.shorindo.docs.outlogger;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum OutloggerMessages implements ActionMessages {
    @Message(lang="ja", content="サービス開始：{0}")
    OLOG_0001,
    @Message(lang="ja", content="サービス終了：{0} : {1}ms")
    OLOG_0002,
    @Message(lang="ja", content="未知のエラー")
    OLOG_9999
    ;
}
