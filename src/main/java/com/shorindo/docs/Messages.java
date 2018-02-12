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
public enum Messages {
    //INFO
    I0001("パス[{0}]を[{1}]にマップします。"),
    I0002("コンポーネント[{0}]をロードしました。"),
    I0003("クラス[{0}]をパス[{1}]にマッピングします。"),

    //WARN
    W1001("プロパティ[{0}]をセットできません。"),
    W1002("クラス[{0}]の情報を取得できません。"),
    W1003("アクション[{0}]に適合するメソッドがありません。"),

    //ERROR
    E2001("メッセージ[{0}]が見つかりません。"),
    E2002("メッセージ[{0}]に[{1}]をセットできません。"),
    E2003("パス[{0}]に対する処理が見つかりません。"),
    E9999("未知のエラー");
    ;

    String message;
    private Messages(String message) {
        this.message = message;
    }
    private Messages(String ja, String en) {
    }
    public String getCode() {
        return name();
    }
    public String getMessage(Object...args) {
        return MessageFormat.format(message, args);
    }
}
