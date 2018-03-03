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
package com.shorindo.docs;

import java.text.MessageFormat;

/**
 * 
 */
public enum DocsMessages implements ActionMessages {
    E_1000("データベース接続の初期化に失敗しました。"),
    E_1001("トランザクションの実行に失敗しました。"),
    E_1002("データベース接続のオープンに失敗しました。"),
    E_1003("データベース接続のクローズに失敗しました。"),
    E_1004("トランザクションのコミットに失敗しました。"),
    E_1005("トランザクションのロールバックに失敗しました。"),
    E_9999("予期せぬエラー");

    private String message;

    private DocsMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage(Object... args) {
        return MessageFormat.format(message, args);
    }

}
