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

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum AuthenticateMessages implements ActionMessages {
    @Message(lang="ja", content="パス[{0}]に対してユーザ[{1}]がアクセスしました。")
    AUTH_0001,
    @Message(lang="ja", content="ユーザ情報が取得できませんでした。")
    AUTH_0501;
}
