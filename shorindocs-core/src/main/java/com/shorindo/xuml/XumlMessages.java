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
package com.shorindo.xuml;

import java.text.MessageFormat;
import java.util.Locale;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum XumlMessages implements ActionMessages {
    XUML_1000("コンポーネント[{0}]をロードしました。"),
    XUML_1001("レンダリング開始：{0}"),
    XUML_1002("レンダリング終了：{0} : {1} ms"),
    XUML_3001("XUMLテンプレート[{0]]のクローズに失敗しました。"),
    XUML_3002("属性[{0}]は不明です。"),
    XUML_5001("コンポーネント[{0}]の指定ができませんでした。"),
    XUML_5010("XUMLエンジンの初期化に失敗しました。"),
    XUML_5020("XUMLのレンダリングに失敗しました。"),
    XUML_5030("ルート要素が'xuml'ではありません。"),
    XUML_5125("[{0}]の生成に失敗したため、Generalコンポーネントを使用します。")
    ;

    private String message;

    private XumlMessages(String message) {
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
